package gui.main_panels.node_panel;

import control.node.NodeConnection;
import control.node.NodeRequestAcceptor;
import control.save.JsonWriter;
import control.type_enums.JointType;

import java.awt.*;
import java.util.ArrayList;

public class NodeEditWindow extends ParentNodePanel {

    public NodeEditWindow(NodeRequestAcceptor nodeControl) {
        super(nodeControl, -1);
        nodeControl.setNodeGraphicUnit(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        ArrayList<NodeConnection> nodeConnections = this.getNodeControl().getNodeConnections(-1);
        super.paintNodeConnections(g, nodeConnections);

    }

    @Override
    public Color getJointTypeColor(boolean input, int nodeIndex, int jointIndex) {
        JointType jointType = this.getNodeControl().getJointType(input, -1, nodeIndex, jointIndex);
        if(jointType == null) {
            return Color.WHITE;
        } else {
            return jointType.getColor();
        }
    }
}
