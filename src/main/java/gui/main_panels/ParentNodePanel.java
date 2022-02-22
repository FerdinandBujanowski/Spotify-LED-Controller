package gui.main_panels;

import control.SerializableFunction;
import control.node.ThreeCoordinatePoint;
import control.exceptions.CannotDeleteNodeException;
import control.node.NodeConnection;
import control.node.NodeControl;
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
import java.util.function.Function;

public abstract class ParentNodePanel extends JPanel implements Serializable {

    private NodeControl nodeControl;
    private ArrayList<GraphicNode> graphicNodes;
    private Point windowLocation;
    private int functionIndex;

    private boolean currentlyMoving;
    private int lastClickedX, lastClickedY;

    private double zoomFactor;
    private JButton zoomOutButton, zoomInButton;
    private JLabel zoomLabel;

    public ParentNodePanel(NodeControl nodeControl, int functionIndex, Color backgroundColor) {
        super(null);

        this.nodeControl = nodeControl;
        this.functionIndex = functionIndex;
        this.graphicNodes = new ArrayList<>();
        this.windowLocation = new Point();

        this.currentlyMoving = false;
        this.lastClickedX = 0;
        this.lastClickedY = 0;

        this.zoomFactor = 1;
        this.zoomOutButton = new JButton("-");
        this.zoomOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zoom(false);
            }
        });
        this.zoomInButton = new JButton("+");
        this.zoomInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zoom(true);
            }
        });

        this.zoomLabel = new JLabel();
        this.zoomLabel.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        this.add(this.zoomLabel);
        this.zoomLabel.setSize(50, 50);

        this.add(this.zoomOutButton);
        this.zoomOutButton.setSize(50, 50);
        this.add(this.zoomInButton);
        this.zoomInButton.setSize(50, 50);

        this.setOpaque(true);
        this.setBackground(backgroundColor);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastClickedX = e.getX();
                lastClickedY = e.getY();
                currentlyMoving = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                currentlyMoving = false;
            }
        });
        this.addMouseMotionListener(new MouseMotionAdapter() {
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
        });
    }

    public void setNodeControl(NodeControl nodeControl) {
        this.nodeControl = nodeControl;
    }

    public void addLogicNodeToNodeControl(NodeType nodeType, int x, int y, Object[] extraParameters) {
        int nextFreeIndex = this.getNodeControl().getNextFreeNodeIndex(this.functionIndex);
        this.getNodeControl().addNode(this.functionIndex, nodeType, nextFreeIndex, extraParameters);

        this.addGraphicNode(this.functionIndex, nextFreeIndex, nodeType, nodeType.getName(), x, y);
    }

    public void addFunctionNode(int functionIndexOrigin, String functionName, int x, int y) {
        int nextFreeIndex = this.getNodeControl().getNextFreeNodeIndex(this.functionIndex);
        this.getNodeControl().addFunctionNode(functionIndexOrigin, this.getFunctionIndex(), nextFreeIndex, functionName);

        this.addGraphicNode(this.functionIndex, nextFreeIndex, null, this.nodeControl.getSpecificNodeName(this.functionIndex, nextFreeIndex), x, y);
    }

    public void addTrackNode(int trackIndex, String trackName, int x, int y) {
        int nextFreeIndex = this.getNodeControl().getNextFreeNodeIndex(this.functionIndex);
        this.getNodeControl().addTrackNode(trackIndex, nextFreeIndex, trackName);

        this.addGraphicNode(this.functionIndex, nextFreeIndex, null, trackName, x, y);
    }

    public void addLayerNode(SerializableFunction<Object, Integer> setMaskFunction, SerializableFunction<Color, Integer> setColorFunction, String layerName, int x, int y) {
        int nextFreeIndex = this.getNodeControl().getNextFreeNodeIndex(this.functionIndex);
        this.getNodeControl().addLayerNode(nextFreeIndex, setMaskFunction, setColorFunction, layerName);

        this.addGraphicNode(this.functionIndex, nextFreeIndex, null, layerName, x, y);
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
                this.addGraphicNode(this.functionIndex, nodeIndex, nodeType, nodeName, position.x, position.y);
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

    public NodeControl getNodeControl() {
        return this.nodeControl;
    }

    public int getFunctionIndex() {
        return this.functionIndex;
    }

    public ArrayList<GraphicNode> getGraphicNodes() {
        return this.graphicNodes;
    }

    public Color getJointTypeColor(boolean input, int nodeIndex, int jointIndex) {
        return null;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension size = this.getSize();
        this.zoomOutButton.setLocation(size.width - 50, size.height - 50);
        this.zoomInButton.setLocation(size.width - 100, size.height - 50);
        this.zoomLabel.setLocation(size.width - 150, size.height - 50);
        this.zoomLabel.setText("x" + this.zoomFactor);
    }

    public void moveEverything(Point relativeMovement) {
        for(GraphicNode graphicNode : this.graphicNodes) {
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
                    this.findGraphicNode(outputCoordinates.getX(), outputCoordinates.getY()).getOutputJoint(outputCoordinates.getZ());
            GraphicJoint currentInputGraphicJoint =
                    this.findGraphicNode(inputCoordinates.getX(), inputCoordinates.getY()).getInputJoint(inputCoordinates.getZ());

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

    private GraphicNode findGraphicNode(int connectionX, int connectionY) {
        for(GraphicNode graphicNode : graphicNodes) {
            if(graphicNode.getIndexes().x == connectionX && graphicNode.getIndexes().y == connectionY) {
                return graphicNode;
            }
        }
        return null;
    }

    public void addGraphicNode(int functionIndex, int nodeIndex, NodeType nodeType, String nodeName, int x, int y) {

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
        newGraphicNode.setTotalLocation(x, y, this.zoomFactor);

        this.graphicNodes.add(newGraphicNode);
        this.repaint();
    }

    public void onOutputNodeReleased(int nodeIndex, int outputJointIndex) {
        this.getNodeControl().updateOutputJointReleased(this.functionIndex, nodeIndex, outputJointIndex);
    }

    public void onInputNodeHovered(int nodeIndex, int inputJointIndex) {
        this.getNodeControl().updateInputJointHovered(this.functionIndex, nodeIndex, inputJointIndex);
    }

    public void onInputConnectionDelete(int nodeIndex, int inputJointIndex) {
        this.getNodeControl().deleteJointConnection(this.functionIndex, nodeIndex, inputJointIndex);
        this.repaint();
    }

    public void onNodeDelete(int nodeIndex) throws CannotDeleteNodeException {
        this.getNodeControl().deleteNode(this.functionIndex, nodeIndex);
        GraphicNode graphicNode = this.findGraphicNode(this.functionIndex, nodeIndex);
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
}
