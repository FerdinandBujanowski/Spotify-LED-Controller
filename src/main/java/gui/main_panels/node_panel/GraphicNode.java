package gui.main_panels.node_panel;

import control.exceptions.FunctionNodeInUseException;
import control.exceptions.JointConnectionFailedException;
import control.node.NodeControl;
import control.exceptions.CannotDeleteNodeException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class GraphicNode extends JPanel {

    private final Point indexes;

    private final int numberInputJoints, numberOutputJoints;

    private boolean currentlyMoving;
    private Point lastClickedAt;

    private final GraphicJoint[] graphicInputJoints, graphicOutputJoints;

    private final JLabel topPicLabel, bottomPicLabel;
    private final JLabel[] centerPicLabels;

    private JLabel nameLabel;
    private JLabel[] inputNameLabels, outputNameLabels;

    private MaskPanel maskPanel;

    public GraphicNode(Point indexes, ParentNodePanel parentNodePanel, String nodeName, String[] inputJointNames, String[] outputJointNames, MaskPanel maskPanel) {
        super(null);
        this.indexes = indexes;

        this.numberInputJoints = inputJointNames.length;
        this.numberOutputJoints = outputJointNames.length;
        int maxJoints = Math.max(this.numberInputJoints, this.numberOutputJoints);

        this.topPicLabel = new JLabel();
        try {
            this.topPicLabel.setIcon(new ImageIcon(ImageIO.read(new File("images\\node_top.png"))));
            this.add(this.topPicLabel);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(maskPanel != null) maxJoints += 3;
        this.centerPicLabels = new JLabel[maxJoints];
        for (int i = 0; i < maxJoints; i++) {
            try {
                this.centerPicLabels[i] = new JLabel(new ImageIcon(ImageIO.read(new File("images\\node_center.png"))));
                this.add(this.centerPicLabels[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.bottomPicLabel = new JLabel();
        try {
            this.bottomPicLabel.setIcon(new ImageIcon(ImageIO.read(new File("images\\node_bottom.png"))));
            this.add(this.bottomPicLabel);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.nameLabel = new JLabel(nodeName);
        this.nameLabel.setForeground(Color.WHITE);
        this.add(this.nameLabel);

        this.inputNameLabels = new JLabel[this.numberInputJoints];
        for (int i = 0; i < this.numberInputJoints; i++) {
            this.inputNameLabels[i] = new JLabel(inputJointNames[i]);
            this.inputNameLabels[i].setForeground(Color.WHITE);
            this.add(this.inputNameLabels[i]);
        }

        this.outputNameLabels = new JLabel[this.numberOutputJoints];
        for (int i = 0; i < this.numberOutputJoints; i++) {
            this.outputNameLabels[i] = new JLabel(outputJointNames[i], SwingConstants.RIGHT);
            this.outputNameLabels[i].setForeground(Color.WHITE);
            this.add(this.outputNameLabels[i]);
        }

        this.currentlyMoving = false;
        this.lastClickedAt = new Point(0, 0);

        this.graphicInputJoints = new GraphicJoint[this.numberInputJoints];
        for (int i = 0; i < graphicInputJoints.length; i++) {
            int currentNumber = i;
            this.graphicInputJoints[i] = new GraphicJoint(parentNodePanel.getJointTypeColor(true, this.indexes.y, currentNumber));
            int finalI = i;
            this.graphicInputJoints[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    switch(e.getButton()) {
                        case MouseEvent.BUTTON1 -> {
                            if (e.getClickCount() >= 2) {
                                try {
                                    parentNodePanel.onInputConnectionDelete(indexes.y, currentNumber);
                                } catch (FunctionNodeInUseException ex) {
                                    JOptionPane.showMessageDialog(parentNodePanel, ex.getMessage(), "Connection delete failed!", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                        case MouseEvent.BUTTON3 -> {
                            parentNodePanel.handleInputJointClicked(new Point(indexes.y, finalI));
                        }
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    parentNodePanel.onInputNodeHovered(indexes.y, currentNumber);
                    parentNodePanel.repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    parentNodePanel.onInputNodeHovered(-1, -1);
                    parentNodePanel.repaint();
                }
            });
            parentNodePanel.add(this.graphicInputJoints[i]);
        }
        this.graphicOutputJoints = new GraphicJoint[this.numberOutputJoints];
        for (int i = 0; i < graphicOutputJoints.length; i++) {
            int currentNumber = i;
            this.graphicOutputJoints[i] = new GraphicJoint(parentNodePanel.getJointTypeColor(false, this.indexes.y, currentNumber));
            this.graphicOutputJoints[i].addMouseListener(new MouseAdapter() {

                @Override
                public void mouseReleased(MouseEvent e) {
                    try {
                        parentNodePanel.onOutputNodeReleased(indexes.y, currentNumber);
                    } catch (FunctionNodeInUseException | JointConnectionFailedException ex) {
                        JOptionPane.showMessageDialog(parentNodePanel, ex.getMessage(), "Connection failed!", JOptionPane.ERROR_MESSAGE);
                    }
                    parentNodePanel.repaint();
                }
            });
            parentNodePanel.add(this.graphicOutputJoints[i]);
        }

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                currentlyMoving = true;
                lastClickedAt.x = e.getX();
                lastClickedAt.y = e.getY();
                parentNodePanel.onSelectionEvent(indexes.y);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                currentlyMoving = false;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    JPopupMenu rightClickPopupMenu = new JPopupMenu();
                    JMenuItem deleteNodeItem = new JMenuItem("Delete Node");
                    rightClickPopupMenu.add(deleteNodeItem);
                    rightClickPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                    deleteNodeItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            try {
                                parentNodePanel.onNodeDelete(indexes.y);
                            } catch (CannotDeleteNodeException | FunctionNodeInUseException ex) {
                                JOptionPane.showMessageDialog(parentNodePanel, ex.getMessage(), "Delete failed!", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    });
                }
                if (e.getButton() == MouseEvent.BUTTON1) {
                    parentNodePanel.onSelectionEvent(indexes.y);
                }
            }
        });
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (currentlyMoving) {
                    Point mousePosition = MouseInfo.getPointerInfo().getLocation();
                    Point parentLocation = parentNodePanel.getWindowLocation();
                    int newX = mousePosition.x - parentLocation.x - lastClickedAt.x;
                    int newY = mousePosition.y - parentLocation.y - lastClickedAt.y;

                    double zoomFactor = parentNodePanel.getZoomFactor();
                    setTotalLocation((int) Math.round(newX / zoomFactor), (int) Math.round((newY / zoomFactor)), zoomFactor);
                    parentNodePanel.repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                parentNodePanel.onMouseMoved(new Point(e.getX() + getX(), e.getY() + getY()));
            }
        });

        this.maskPanel = maskPanel;
        if (this.maskPanel != null) {
            parentNodePanel.add(this.maskPanel);
            this.maskPanel.setBounds(50, 50, 100, 100);
        }
    }

    public MaskPanel getMaskPanel() {
        return this.maskPanel;
    }

    public void setLastClickedAt(Point lastClickedAt) {
        this.lastClickedAt = lastClickedAt;
    }

    public void setCurrentlyMoving(boolean currentlyMoving) {
        this.currentlyMoving = currentlyMoving;
    }

    public Point getIndexes() {
        return this.indexes;
    }

    @Override
    public void paintComponent(Graphics g) {
    }

    public void setTotalLocation(int x, int y, double zoomFactor) {
        this.setLocation((int) Math.round(x * zoomFactor), (int) Math.round(y * zoomFactor));

        if (this.maskPanel != null) {
            this.maskPanel.setLocation(
                    (int) Math.round(this.getX() + (0.25 * this.getWidth())),
                    this.getY() + this.getHeight() - (int)(3.5 * NodeControl.NODE_CENTER_HEIGHT * zoomFactor)
            );
        }

        int maxJoints = Math.max(this.numberInputJoints, this.numberOutputJoints);
        int halfJointWidth = (int) Math.round((NodeControl.JOINT_WIDTH / 2.d) * zoomFactor);

        if (this.graphicInputJoints.length > 0) {
            int inputJointInterval = (int) Math.round(NodeControl.NODE_CENTER_HEIGHT * zoomFactor * maxJoints) / this.graphicInputJoints.length;
            for (int i = 0; i < this.graphicInputJoints.length; i++) {
                this.graphicInputJoints[i].setLocation(
                        this.getX() - halfJointWidth,
                        (int) Math.round((this.getY() + (NodeControl.NODE_TOP_HEIGHT * zoomFactor) - halfJointWidth + (0.5 * inputJointInterval) + (i * inputJointInterval)))
                );
                this.graphicInputJoints[i].setSize((int) Math.round(NodeControl.JOINT_WIDTH * zoomFactor), (int) Math.round(NodeControl.JOINT_WIDTH * zoomFactor));
            }
        }
        if (this.graphicOutputJoints.length > 0) {
            int outputJointInterval = (int) Math.round(NodeControl.NODE_CENTER_HEIGHT * zoomFactor * maxJoints) / this.graphicOutputJoints.length;
            for (int i = 0; i < this.graphicOutputJoints.length; i++) {
                this.graphicOutputJoints[i].setLocation(
                        this.getX() + this.getWidth() - halfJointWidth,
                        (int) Math.round((this.getY() + (NodeControl.NODE_TOP_HEIGHT * zoomFactor) - halfJointWidth + (0.5 * outputJointInterval) + (i * outputJointInterval)))
                );
                this.graphicOutputJoints[i].setSize((int) Math.round(NodeControl.JOINT_WIDTH * zoomFactor), (int) Math.round(NodeControl.JOINT_WIDTH * zoomFactor));
            }
        }
    }

    public void setTotalSize(double zoomFactor) {
        //OWN SIZE
        int maxJoints = Math.max(this.numberInputJoints, this.numberOutputJoints);

        int height = NodeControl.NODE_TOP_HEIGHT
                + (maxJoints + (this.maskPanel != null ? 3 : 0)) * NodeControl.NODE_CENTER_HEIGHT
                + NodeControl.NODE_BOTTOM_HEIGHT;
        this.setSize(new Dimension((int) Math.round(200 * zoomFactor), (int) Math.round(height * zoomFactor)));


        //IMAGES
        try {
            this.topPicLabel.setIcon(new ImageIcon(this.zoomImage(ImageIO.read(new File("images\\node_top.png")), zoomFactor)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.topPicLabel.setSize(
                (int) Math.round(200 * zoomFactor),
                (int) Math.round(NodeControl.NODE_TOP_HEIGHT * zoomFactor)
        );
        this.topPicLabel.setLocation(0, 0);

        for (int i = 0; i < this.centerPicLabels.length; i++) {
            try {
                this.centerPicLabels[i].setIcon(new ImageIcon(this.zoomImage(ImageIO.read(new File("images\\node_center.png")), zoomFactor)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.centerPicLabels[i].setSize(
                    (int) Math.round(200 * zoomFactor),
                    (int) Math.round(NodeControl.NODE_CENTER_HEIGHT * zoomFactor)
            );
            if(i == 0) {
                this.centerPicLabels[i].setLocation(0, (int)Math.round((NodeControl.NODE_TOP_HEIGHT) * zoomFactor));
            } else {
                this.centerPicLabels[i].setLocation(0, (int)Math.round(centerPicLabels[i - 1].getY() + (zoomFactor * NodeControl.NODE_CENTER_HEIGHT)));
            }
        }
        try {
            this.bottomPicLabel.setIcon(new ImageIcon(this.zoomImage(ImageIO.read(new File("images\\node_bottom.png")), zoomFactor)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.bottomPicLabel.setSize(
                (int) Math.round(200 * zoomFactor),
                (int) Math.round(NodeControl.NODE_BOTTOM_HEIGHT * zoomFactor)
        );
        this.bottomPicLabel.setLocation(
                0, (int) Math.round(this.centerPicLabels[this.centerPicLabels.length - 1].getY() + (zoomFactor * NodeControl.NODE_CENTER_HEIGHT))
        );

        //FONTS
        this.nameLabel.setFont(new Font("Times New Roman", Font.PLAIN, (int) Math.round(18.0 * zoomFactor)));
        this.nameLabel.setSize((int) Math.round(200.0 * zoomFactor), (int) Math.round(NodeControl.NODE_TOP_HEIGHT * zoomFactor));
        this.nameLabel.setLocation((int)Math.round(15.0 * zoomFactor), (int)Math.round(3.0 * zoomFactor));
        this.setComponentZOrder(this.nameLabel, 0);

        if (this.graphicInputJoints.length > 0) {
            int inputJointInterval = (int) Math.round(NodeControl.NODE_CENTER_HEIGHT * maxJoints * zoomFactor) / this.graphicInputJoints.length;
            for (int i = 0; i < this.graphicInputJoints.length; i++) {
                this.inputNameLabels[i].setFont(new Font("Times New Roman", Font.PLAIN, (int) Math.round(15.0 * zoomFactor)));
                inputNameLabels[i].setSize((int) Math.round(200.0 * zoomFactor), (int) Math.round(NodeControl.NODE_CENTER_HEIGHT * zoomFactor));
                inputNameLabels[i].setLocation(
                        (int) Math.round(15.0 * zoomFactor),
                        (int) Math.round(NodeControl.NODE_TOP_HEIGHT * zoomFactor - (0.5 * NodeControl.NODE_CENTER_HEIGHT * zoomFactor) + (inputJointInterval * (i + 0.5)))
                );
                this.setComponentZOrder(inputNameLabels[i], 0);
            }
        }

        if (this.graphicOutputJoints.length > 0) {
            int outputJointInterval = (int) Math.round(NodeControl.NODE_CENTER_HEIGHT * maxJoints * zoomFactor) / this.graphicOutputJoints.length;
            for (int i = 0; i < this.graphicOutputJoints.length; i++) {
                this.outputNameLabels[i].setFont(new Font("Times New Roman", Font.PLAIN, (int) Math.round(15.0 * zoomFactor)));
                this.outputNameLabels[i].setSize((int) Math.round(200.0 * zoomFactor), (int) Math.round(NodeControl.NODE_CENTER_HEIGHT * zoomFactor));
                this.outputNameLabels[i].setLocation(
                        -(int) Math.round(15.0 * zoomFactor),
                        (int) Math.round(NodeControl.NODE_TOP_HEIGHT * zoomFactor - (0.5 * NodeControl.NODE_CENTER_HEIGHT * zoomFactor) + (outputJointInterval * (i + 0.5)))
                );
                this.setComponentZOrder(this.outputNameLabels[i], 0);
            }
        }

        if(this.maskPanel != null) {
            this.maskPanel.setSize(
                    (int) Math.round(this.getWidth() / 2.2),
                    (int) Math.round(this.getWidth() / 2.2)
            );
        }
    }

    public void onJointClickedRequest() {

    }

    public GraphicJoint getOutputJoint(int jointIndex) {
        return this.graphicOutputJoints[jointIndex];
    }

    public GraphicJoint getInputJoint(int jointIndex) {
        return this.graphicInputJoints[jointIndex];
    }

    public GraphicJoint[] getGraphicInputJoints() {
        return this.graphicInputJoints;
    }

    public GraphicJoint[] getGraphicOutputJoints() {
        return this.graphicOutputJoints;
    }

    public BufferedImage zoomImage(BufferedImage image, double zoom) {
        BufferedImage returnValue = new BufferedImage(
                (int) Math.round(image.getWidth() * zoom),
                (int) Math.round(image.getHeight() * zoom),
                image.getType()
        );
        Graphics2D g = returnValue.createGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g.drawImage(
                image, 0, 0,
                (int) Math.round(image.getWidth() * zoom),
                (int) Math.round(image.getHeight() * zoom),
                null
        );
        g.dispose();
        return returnValue;
    }
}