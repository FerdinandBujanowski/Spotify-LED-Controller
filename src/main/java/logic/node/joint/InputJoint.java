package logic.node.joint;

import control.exceptions.JointConnectionFailedException;
import control.node.NodeConnection;
import control.node.ThreeCoordinatePoint;
import logic.node.LogicNode;
import logic.node.joint.joint_types.JointDataType;

import java.io.Serializable;

public class InputJoint implements Serializable {

    private LogicNode parentNode;
    private int index;

    private OutputJoint connectedOutputJoint;

    private JointDataType jointDataType;
    private String name;

    public InputJoint(JointDataType jointDataType, String name) {
        this.jointDataType = jointDataType;
        this.name = name;
    }

    public LogicNode getParentNode() {
        return this.parentNode;
    }
    public void setParentNode(LogicNode parentNode, int index) {
        this.parentNode = parentNode;
        this.index = index;
    }

    public void tryJointConnection(OutputJoint outputJoint, NodeConnection nodeConnection) throws JointConnectionFailedException {
        if(
                this.jointDataType.getClass() == outputJoint.getJointDataType().getClass()
                && this.connectedOutputJoint == null
                && !this.parentNode.equals(outputJoint.getParentNode())
        ) {
            this.connectedOutputJoint = outputJoint;
            this.connectedOutputJoint.getConnectedInputJoints().add(this);
            this.connectedOutputJoint.getParentNode().onInputChangeEvent();
        } else {
            if(this.jointDataType.getClass() != outputJoint.getJointDataType().getClass()) {
                throw new JointConnectionFailedException("Joint Connection failed! Joint data types don't match!", nodeConnection);
            } else if(this.connectedOutputJoint != null) {
                throw new JointConnectionFailedException("Joint Connection failed! Input joint already connected!", nodeConnection);
            } else {
                throw new JointConnectionFailedException("Input Joint and Output Joint belong to the same Node!", nodeConnection);
            }
        }
    }

    public void deleteJointConnection() {
        if(this.connectedOutputJoint != null) {
            this.connectedOutputJoint.getConnectedInputJoints().remove(this);
            this.connectedOutputJoint = null;
            this.getParentNode().onInputChangeEvent();
        }
    }

    public OutputJoint getConnectedOutputJoint() {
        return this.connectedOutputJoint;
    }
    public JointDataType getJointDataType() {
        return this.jointDataType;
    }

    public String getName() {
        return this.name;
    }
}