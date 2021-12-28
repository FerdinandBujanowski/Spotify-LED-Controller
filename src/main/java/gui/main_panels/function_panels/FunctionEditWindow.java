package gui.main_panels.function_panels;

import control.*;
import control.type_enums.JointType;
import control.type_enums.NodeType;
import gui.main_panels.ParentNodePanel;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class FunctionEditWindow extends ParentNodePanel {

    public FunctionEditWindow(NodeControl nodeControl, int functionIndex) {
        super(nodeControl, functionIndex, Color.DARK_GRAY);
    }

    public void addFunction(String[] inputNames, JointType[] inputTypes, String[] outputNames, JointType[] outputTypes) {
        this.getNodeControl().addFunction(this.getFunctionIndex(), inputNames, inputTypes, outputNames, outputTypes);

        int[] nodeIndexes = this.getNodeControl().getNodeIndexesOfFunctionIndex(this.getFunctionIndex());
        for(int index : nodeIndexes) {
            NodeType nodeType = this.getNodeControl().getNodeType(this.getFunctionIndex(), index);
            String nodeName = nodeType == null ? this.getNodeControl().getSpecificNodeName(this.getFunctionIndex(), index) : nodeType.getName();
            super.addGraphicNode(this.getFunctionIndex(), index, nodeType, nodeName, 10, 10);
        }
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