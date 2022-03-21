package gui.main_panels.function_panels;

import control.node.NodeConnection;
import control.node.NodeControl;
import control.type_enums.JointType;
import control.type_enums.NodeType;
import gui.main_panels.ParentNodePanel;

import java.awt.*;
import java.util.ArrayList;

public class FunctionEditWindow extends ParentNodePanel {

    public FunctionEditWindow(NodeControl nodeControl, int functionIndex) {
        super(nodeControl, functionIndex, Color.DARK_GRAY);
        nodeControl.addFunctionGraphicUnit(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        ArrayList<NodeConnection> nodeConnections = this.getNodeControl().getNodeConnections(this.getFunctionIndex());
        super.paintNodeConnections(g, nodeConnections);

    }

    @Override
    public Color getJointTypeColor(boolean input, int nodeIndex, int jointIndex) {
        JointType jointType = this.getNodeControl().getJointType(input, this.getFunctionIndex(), nodeIndex, jointIndex);
        if(jointType == null) {
            return Color.WHITE;
        } else {
            return jointType.getColor();
        }
    }
}