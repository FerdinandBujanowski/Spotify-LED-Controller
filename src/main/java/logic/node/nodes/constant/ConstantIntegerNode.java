package logic.node.nodes.constant;

import control.type_enums.NodeType;
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
                },
                NodeType.CONSTANT_INTEGER_NODE,
                new Object[] { value }
        );
        this.value = value;
    }

    @Override
    public IntegerJointDataType[] function(InputJoint[] inputJoints) {
        return new IntegerJointDataType[] { new IntegerJointDataType(this.value) };
    }
}
