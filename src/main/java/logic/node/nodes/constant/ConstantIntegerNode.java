package logic.node.nodes.constant;

import logic.node.LogicNode;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.IntegerJointDataType;

public class ConstantIntegerNode extends LogicNode {

    Integer value;

    public ConstantIntegerNode(int nodeIndex, Integer value) {
        super(
                nodeIndex,
                new InputJoint[] {},
                new OutputJoint[] {
                        new OutputJoint(new IntegerJointDataType(), String.valueOf(value))
                }
        );
        this.value = value;
    }

    @Override
    public IntegerJointDataType[] function(InputJoint[] inputJoints) {
        return new IntegerJointDataType[] { new IntegerJointDataType(this.value) };
    }
}
