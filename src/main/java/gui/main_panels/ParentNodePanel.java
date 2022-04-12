package gui.main_panels;

import control.exceptions.FunctionNodeInUseException;
import control.exceptions.JointConnectionFailedException;
import control.node.*;
import control.exceptions.CannotDeleteNodeException;
import control.type_enums.JointType;
import control.type_enums.NodeType;
import control.math_functions.MathFunctions;
import gui.node_components.GraphicJoint;
import gui.node_components.GraphicNode;
import gui.node_components.MaskPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public abstract class ParentNodePanel extends JPanel implements Serializable, NodeGraphicUnit {

    private NodeRequestAcceptor nodeControl;
    private ArrayList<GraphicNode> graphicNodes;
    private Point windowLocation;
    private int functionIndex;

    private boolean currentlyMoving;
    private int lastClickedX, lastClickedY;

    private double zoomFactor;
    private JLabel zoomLabel;

    private MouseMotionAdapter mouseMotionAdapter;

    private ArrayList<Integer> selectedNodeIndexes, copiedNodeIndexes;
    private boolean toggleShift, toggleCtrl, toggleG;

    public ParentNodePanel(NodeRequestAcceptor nodeControl, int functionIndex, Color backgroundColor) {
        super(null);

        this.nodeControl = nodeControl;

        this.functionIndex = functionIndex;
        this.graphicNodes = new ArrayList<>();
        this.windowLocation = new Point();

        this.currentlyMoving = false;
        this.lastClickedX = 0;
        this.lastClickedY = 0;

        this.zoomFactor = 1;
        this.zoomLabel = new JLabel();
        this.zoomLabel.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        this.add(this.zoomLabel);
        this.zoomLabel.setSize(50, 50);

        this.setOpaque(true);
        this.setBackground(backgroundColor);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastClickedX = e.getX();
                lastClickedY = e.getY();
                currentlyMoving = true;
                toggleG = false;
                selectedNodeIndexes.removeAll(selectedNodeIndexes);
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                currentlyMoving = false;
            }
        });

        this.mouseMotionAdapter = new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {

                if(currentlyMoving) {
                    Point relativeMovement = new Point(
                            e.getX() - lastClickedX,
                            e.getY() - lastClickedY
                    );
                    moveEverything(relativeMovement);
                    lastClickedX = e.getX();
                    lastClickedY = e.getY();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                onMouseMoved(new Point(e.getX(), e.getY()));
            }
        };
        this.addMouseMotionListener(this.mouseMotionAdapter);

        this.selectedNodeIndexes = new ArrayList<>();
        this.copiedNodeIndexes = new ArrayList<>();
        this.toggleShift = false;
        this.toggleCtrl = false;
        this.toggleG = false;
    }

    public void updateGraphicNodes(Point[] positions) {
        int[] nodeIndexArray = nodeControl.getNodeIndexArray(this.functionIndex);
        for(int i = 0; i < nodeIndexArray.length; i++) {
            int nodeIndex = nodeIndexArray[i];
            if(!this.graphicNodeWithNodeIndexExists(nodeIndex)) {
                Point position = positions[i];
                NodeType nodeType = this.nodeControl.getNodeType(this.functionIndex, nodeIndex);
                String nodeName;
                if(nodeType == null) {
                    nodeName = this.nodeControl.getSpecificNodeName(this.functionIndex, nodeIndex);
                } else {
                    nodeName = nodeType.getName();
                }
                this.addGraphicNode(this.functionIndex, nodeIndex, nodeType, nodeName);
            }
        }
    }

    private boolean graphicNodeWithNodeIndexExists(int nodeIndex) {
        for(GraphicNode graphicNode : this.graphicNodes) {
            if(graphicNode.getIndexes().y == nodeIndex) {
                return true;
            }
        }
        return false;
    }

    public Point getWindowLocation() {
        try {
            return this.getLocationOnScreen();
        } catch(IllegalComponentStateException e) {
            return new Point(0, 0);
        }
    }

    public NodeRequestAcceptor getNodeControl() {
        return this.nodeControl;
    }

    public int getFunctionIndex() {
        return this.functionIndex;
    }

    public ArrayList<GraphicNode> getGraphicNodes() {
        return this.graphicNodes;
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension size = this.getSize();
        this.zoomLabel.setLocation(size.width - 50, size.height - 50);
        this.zoomLabel.setText("x" + this.zoomFactor);

        for(int nodeIndex : this.selectedNodeIndexes) {
            GraphicNode graphicNode = this.findGraphicNode(nodeIndex);
            if(graphicNode != null) {
                g.setColor(Color.BLUE);
                Dimension ovalDimension = new Dimension(graphicNode.getWidth() / 5, graphicNode.getWidth() / 5);
                Point topLeft = new Point(graphicNode.getX() - 2, graphicNode.getY() - 2);
                Point bottomRight = new Point(
                        graphicNode.getX() + graphicNode.getWidth() - ovalDimension.width + 2,
                        graphicNode.getY() + graphicNode.getHeight() - ovalDimension.height + 2
                );
                g.fillOval(topLeft.x, topLeft.y, ovalDimension.width, ovalDimension.height);
                g.fillOval(bottomRight.x, topLeft.y, ovalDimension.width, ovalDimension.height);
                g.fillOval(topLeft.x, bottomRight.y, ovalDimension.width, ovalDimension.height);
                g.fillOval(bottomRight.x, bottomRight.y, ovalDimension.width, ovalDimension.height);

                Dimension halfOval = new Dimension(ovalDimension.width / 2, ovalDimension.height / 2);
                g.fillRect(topLeft.x, topLeft.y + halfOval.height, graphicNode.getWidth() + 4, graphicNode.getHeight() - (2 * halfOval.height));
                g.fillRect(topLeft.x + halfOval.width, topLeft.y, graphicNode.getWidth() - (2 * halfOval.width), graphicNode.getHeight() + 4);
            }
        }
    }

    public void moveEverything(Point relativeMovement) {
        for(GraphicNode graphicNode : this.graphicNodes) {
            this.moveGraphicNode(graphicNode, relativeMovement);
        }
        this.repaint();
    }

    public double getZoomFactor() {
        return this.zoomFactor;
    }

    private void zoom(boolean zoomIn) {
        double oldZoomFactor = this.zoomFactor;
        if(zoomIn && this.zoomFactor < 1.0) {
            this.zoomFactor = this.zoomFactor + 0.1;
        } else if(!zoomIn && this.zoomFactor > 0.1) {
            this.zoomFactor = this.zoomFactor - 0.1;
        } else {
            return;
        }
        BigDecimal bigDecimal = new BigDecimal(this.zoomFactor).setScale(1, RoundingMode.HALF_UP);
        this.zoomFactor = bigDecimal.doubleValue();

        for(GraphicNode graphicNode : this.graphicNodes) {

            graphicNode.setTotalSize(this.zoomFactor);

            int halfWidth = (int)Math.round(this.getWidth() / 2.0);
            int halfHeight = (int)Math.round(this.getHeight() / 2.0);

            this.moveEverything(new Point(-halfWidth, -halfHeight));
            graphicNode.setTotalLocation(
                    (int)Math.round((graphicNode.getX()) / oldZoomFactor),
                    (int)Math.round((graphicNode.getY()) / oldZoomFactor),
                    this.zoomFactor
            );
            this.moveEverything(new Point(halfWidth, halfHeight));
        }
        repaint();
    }

    public void paintNodeConnections(Graphics g, ArrayList<NodeConnection> nodeConnections) {
        for(NodeConnection nodeConnection : nodeConnections) {
            ThreeCoordinatePoint outputCoordinates = nodeConnection.getOutputCoordinates();
            ThreeCoordinatePoint inputCoordinates = nodeConnection.getInputCoordinates();
            GraphicJoint currentOutputGraphicJoint =
                    this.findGraphicNode(outputCoordinates.getY()).getOutputJoint(outputCoordinates.getZ());
            GraphicJoint currentInputGraphicJoint =
                    this.findGraphicNode(inputCoordinates.getY()).getInputJoint(inputCoordinates.getZ());

            int jointWidth = (int)Math.round(NodeControl.JOINT_WIDTH * this.getZoomFactor());
            int halfJointWidth = jointWidth / 2;

            Point startingPoint = new Point(currentOutputGraphicJoint.getX() + jointWidth, currentOutputGraphicJoint.getY() + halfJointWidth);
            Point endPoint = new Point(currentInputGraphicJoint.getX(), currentInputGraphicJoint.getY() + halfJointWidth);

            g.setColor(currentInputGraphicJoint.getColor());
            if(endPoint.x <= startingPoint.x) {
                g.drawLine(startingPoint.x, startingPoint.y, endPoint.x, endPoint.y);
            }
            else {
                int lowestY = (Math.min(startingPoint.y, endPoint.y));
                int deltaY = Math.max(startingPoint.y, endPoint.y) - lowestY;
                int lastY = startingPoint.y;
                for(int currentX = startingPoint.x; currentX <= endPoint.x; currentX++) {
                    double posY = MathFunctions.getBezier((double)(currentX - startingPoint.x) / (endPoint.x - startingPoint.x), startingPoint.y < endPoint.y);
                    int currentY = lowestY + (int)(deltaY * posY);
                    int gapY = lastY - currentY;
                    g.drawRect(currentX, currentY, 1, 1);
                    lastY = currentY;
                }
            }
        }
    }


    public void onOutputNodeReleased(int nodeIndex, int outputJointIndex) throws FunctionNodeInUseException, JointConnectionFailedException {
        this.getNodeControl().updateOutputJointReleased(this.functionIndex, nodeIndex, outputJointIndex);
    }

    public void onInputNodeHovered(int nodeIndex, int inputJointIndex) {
        this.getNodeControl().updateInputJointHovered(this.functionIndex, nodeIndex, inputJointIndex);
    }

    public void onInputConnectionDelete(int nodeIndex, int inputJointIndex) throws FunctionNodeInUseException {
        this.getNodeControl().deleteJointConnectionRequest(this.functionIndex, nodeIndex, inputJointIndex);
        this.repaint();
    }

    public void onNodeDelete(int nodeIndex) throws CannotDeleteNodeException, FunctionNodeInUseException {
        this.getNodeControl().deleteNodeRequest(this.functionIndex, nodeIndex);
        GraphicNode graphicNode = this.findGraphicNode(nodeIndex);
        this.graphicNodes.remove(graphicNode);

        this.remove(graphicNode);
        for(GraphicJoint inputJoint : graphicNode.getGraphicInputJoints()) {
            this.remove(inputJoint);
        }
        for(GraphicJoint outputJoint : graphicNode.getGraphicOutputJoints()) {
            this.remove(outputJoint);
        }
        if(graphicNode.getMaskPanel() != null) {
            this.remove(graphicNode.getMaskPanel());
        }
        this.repaint();
    }

    public void onKeyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_SHIFT -> {
                if(!this.toggleCtrl) {
                    this.toggleShift = true;
                }
            }
            case KeyEvent.VK_CONTROL -> {
                if(!this.toggleShift) {
                    this.toggleCtrl = true;
                }
            }
            case KeyEvent.VK_G -> {
                this.toggleG = !this.toggleG;
            }
            case KeyEvent.VK_C -> {
                if(this.toggleCtrl) {
                    this.copiedNodeIndexes.removeAll(this.copiedNodeIndexes);
                    this.copiedNodeIndexes.addAll(this.selectedNodeIndexes);
                }
            }
            case KeyEvent.VK_V -> {
                if(this.toggleCtrl) {
                    ArrayList<Integer> newNodeIndexes = this.nodeControl.copyNodes(this.copiedNodeIndexes, this.getFunctionIndex());
                    this.selectedNodeIndexes.removeAll(this.selectedNodeIndexes);
                    this.selectedNodeIndexes.addAll(newNodeIndexes);
                    this.toggleG = true;
                }
            }
            case KeyEvent.VK_A -> {
                if(this.toggleCtrl) {
                    this.selectedNodeIndexes.removeAll(this.selectedNodeIndexes);
                    for(GraphicNode graphicNode : this.graphicNodes) {
                        this.selectedNodeIndexes.add(graphicNode.getIndexes().y);
                        repaint();
                    }
                }
            }
            case KeyEvent.VK_J -> {
                ArrayList<ArrayList<Integer>> nodeSets = this.nodeControl.getNodeSets(this.getFunctionIndex());

                ArrayList<Integer> nodeSetIndexes = new ArrayList<>();
                for(int selectedNodeIndex : this.selectedNodeIndexes) {
                    for(int i = 0; i < nodeSets.size(); i++) {
                        if(nodeSets.get(i).contains(selectedNodeIndex) && !nodeSetIndexes.contains(i)) {
                            nodeSetIndexes.add(i);
                        }
                    }
                }
                if(nodeSetIndexes.size() == 1) {
                    this.selectedNodeIndexes.removeAll(this.selectedNodeIndexes);
                    this.selectedNodeIndexes.addAll(nodeSets.get(nodeSetIndexes.get(0)));
                    repaint();
                }
            }
            case KeyEvent.VK_PLUS -> {
                if(this.toggleCtrl) {
                    this.zoom(true);
                }
            }
            case KeyEvent.VK_MINUS -> {
                if(this.toggleCtrl) {
                    this.zoom(false);
                }
            }
        }
    }

    public void onKeyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
            this.toggleShift = false;
        }
        if(e.getKeyCode() == KeyEvent.VK_CONTROL) {
            this.toggleCtrl = false;
        }
    }

    public void onSelectionEvent(int nodeIndex) {
        if(this.toggleShift) {
            if(!this.selectedNodeIndexes.contains(nodeIndex)) {
                this.selectedNodeIndexes.add(nodeIndex);
            }
        } else if(this.toggleCtrl) {
            if(this.selectedNodeIndexes.contains(nodeIndex)) {
                this.selectedNodeIndexes.remove((Integer) nodeIndex);
            }
        } else {
            this.selectedNodeIndexes.removeAll(this.selectedNodeIndexes);
            this.selectedNodeIndexes.add(nodeIndex);
        }
        this.repaint();
    }

    public void onMouseMoved(Point location) {
        int locX = location.x;
        int locY = location.y;

        if(toggleG) {
            Point relativeMovement = new Point(
                    locX - lastClickedX,
                    locY - lastClickedY
            );
            for(int i : selectedNodeIndexes) {
                GraphicNode graphicNode = findGraphicNode(i);
                if(graphicNode != null) {
                    moveGraphicNode(graphicNode, relativeMovement);
                }
            }
            repaint();
        }
        lastClickedX = locX;
        lastClickedY = locY;
    }

    public Color getJointTypeColor(boolean input, int nodeIndex, int jointIndex) {
        JointType jointType = this.getNodeControl().getJointType(input, this.functionIndex, nodeIndex, jointIndex);
        if(jointType == null) {
            return Color.WHITE;
        } else {
            return jointType.getColor();
        }
    }

    @Override
    public void addGraphicNode(int functionIndex, int nodeIndex, NodeType nodeType, String nodeName) {

        String[] inputJointNames = this.getNodeControl().getInputJointNames(functionIndex, nodeIndex);
        String[] outputJointNames = this.getNodeControl().getOutputJointNames(functionIndex, nodeIndex);

        MaskPanel maskPanel = null;
        if(nodeType != null && nodeType.hasMaskOutput()) {
            maskPanel = new MaskPanel(this.getNodeControl().getMaskValuesFunctionForNode(functionIndex, nodeIndex));
        }
        GraphicNode newGraphicNode = new GraphicNode(
                new Point(functionIndex, nodeIndex),
                this,
                nodeName,
                inputJointNames,
                outputJointNames,
                maskPanel
        );
        this.add(newGraphicNode);
        newGraphicNode.setTotalSize(this.zoomFactor);
        newGraphicNode.setTotalLocation(10, 10, this.zoomFactor);

        this.graphicNodes.add(newGraphicNode);
        this.repaint();
    }

    @Override
    public GraphicNode findGraphicNode(int nodeIndex) {
        for(GraphicNode graphicNode : graphicNodes) {
            if(graphicNode.getIndexes().x == this.functionIndex && graphicNode.getIndexes().y == nodeIndex) {
                return graphicNode;
            }
        }
        return null;
    }

    @Override
    public void moveGraphicNode(GraphicNode graphicNode, Point relativeMovement) {
        graphicNode.setLocation(
                graphicNode.getX() + relativeMovement.x,
                graphicNode.getY() + relativeMovement.y
        );
        for(GraphicJoint graphicJoint : graphicNode.getGraphicInputJoints()) {
            graphicJoint.setLocation(
                    graphicJoint.getX() + relativeMovement.x,
                    graphicJoint.getY() + relativeMovement.y
            );
        }
        for(GraphicJoint graphicJoint : graphicNode.getGraphicOutputJoints()) {
            graphicJoint.setLocation(
                    graphicJoint.getX() + relativeMovement.x,
                    graphicJoint.getY() + relativeMovement.y
            );
        }
        if(graphicNode.getMaskPanel() != null) {
            graphicNode.getMaskPanel().setLocation(
                    graphicNode.getMaskPanel().getX() + relativeMovement.x,
                    graphicNode.getMaskPanel().getY() + relativeMovement.y
            );
        }
    }

    @Override
    public void cleanUpCanvas() {
        ArrayList<ArrayList<Integer>> nodeSets = this.nodeControl.getNodeSets(this.getFunctionIndex());

        for(ArrayList<Integer> nodeSet : nodeSets) {
            TwoIntegerCorrespondence rankCorresponence = new TwoIntegerCorrespondence();
            Point[] oldBounds = this.getNodeSetBounds(nodeSet);
            Point centerPoint = new Point(
                    oldBounds[0].x + ((oldBounds[1].x + oldBounds[0].x) / 2),
                    oldBounds[0].y + ((oldBounds[1].y - oldBounds[0].y) / 2)
            );
            for(int nodeIndex : nodeSet) {
                GraphicNode graphicNode = this.findGraphicNode(nodeIndex);
                if(graphicNode != null) {
                    graphicNode.setTotalLocation(centerPoint.x, centerPoint.y, this.zoomFactor);

                    int rank = this.nodeControl.getNodeRank(this.getFunctionIndex(), nodeIndex);
                    rankCorresponence.addValue(nodeIndex, rank);
                    graphicNode.setTotalLocation(graphicNode.getX() + rank * 200, graphicNode.getY(), this.zoomFactor);
                }
            }
        }
    }

    private Point[] getNodeSetBounds(ArrayList<Integer> nodeSet) {
        Point[] bounds = new Point[2];
        Point upperLeft = new Point();
        Point bottomRight = new Point();

        for(int nodeIndex : nodeSet) {
            GraphicNode graphicNode = this.findGraphicNode(nodeIndex);
            if(graphicNode != null) {
                Point nodeUpperLeft = graphicNode.getLocation();
                Point nodeBottomRight = new Point(nodeUpperLeft.x + graphicNode.getWidth(), nodeUpperLeft.y + graphicNode.getHeight());

                if(nodeUpperLeft.x < upperLeft.x) {
                    upperLeft.x = nodeUpperLeft.x;
                }
                if(nodeUpperLeft.y < upperLeft.y) {
                    upperLeft.y = nodeUpperLeft.y;
                }
                if(nodeBottomRight.x > bottomRight.x) {
                    bottomRight.x = nodeBottomRight.x;
                }
                if(nodeBottomRight.y > bottomRight.y) {
                    bottomRight.y = nodeBottomRight.y;
                }
            }
        }
        bounds[0] = upperLeft;
        bounds[1] = bottomRight;
        return bounds;
    }
}
