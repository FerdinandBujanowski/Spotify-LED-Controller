package logic.node.joint;

import logic.node.LogicNode;
import logic.node.joint.joint_types.JointDataType;

import java.io.Serializable;
import java.util.ArrayList;

public class OutputJoint implements Serializable {

    private LogicNode parentNode;
    private int index;

    private ArrayList<InputJoint> connectedInputJoints;

    private JointDataType jointDataType;
    String name;

    public OutputJoint(JointDataType jointDataType, String name) {
        this.jointDataType = jointDataType;
        this.name = name;
        this.connectedInputJoints = new ArrayList<>();
    }
    public LogicNode getParentNode() {
        return this.parentNode;
    }
    public void setParentNode(LogicNode parentNode, int index) {
        this.parentNode = parentNode;
        this.index = index;
    }

    public ArrayList<InputJoint> getConnectedInputJoints() {
        return this.connectedInputJoints;
    }

    public JointDataType getJointDataType() {
        return this.jointDataType;
    }

    public String getName() {
        return this.name;
    }
}