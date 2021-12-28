package logic.node.nodes.compare;

import logic.node.LogicNode;
import logic.node.joint.*;
import logic.node.joint.joint_types.*;

public class EqualsNode extends LogicNode {

    public EqualsNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[]{
                        new InputJoint(new NumberJointDataType(), "Number A"),
                        new InputJoint(new NumberJointDataType(), "Number B")
                },
                new OutputJoint[]{
                        new OutputJoint(new BooleanJointDataType(), "==")
                }
        );
    }

    @Override
    public BooleanJointDataType[] function(InputJoint[] inputJoints) {
        double a = (Double) inputJoints[0].getJointDataType().getData();
        double b = (Double) inputJoints[1].getJointDataType().getData();
        return new BooleanJointDataType[] { new BooleanJointDataType(a == b) };
    }
}
