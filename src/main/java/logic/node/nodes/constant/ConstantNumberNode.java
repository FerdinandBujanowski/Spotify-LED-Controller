package logic.node.nodes.constant;

import control.type_enums.NodeType;
import logic.node.LogicNode;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.NumberJointDataType;

public class ConstantNumberNode extends LogicNode {

    Double value;

    public ConstantNumberNode(int nodeIndex, Double value) {
        super(
                nodeIndex,
                new InputJoint[] {},
                new OutputJoint[] {
                        new OutputJoint(new NumberJointDataType(), String.valueOf(value))
                },
                NodeType.CONSTANT_NUMBER_NODE,
                new Object[] { value }
        );
        this.value = value;
    }

    @Override
    public NumberJointDataType[] function(InputJoint[] inputJoints) {
        return new NumberJointDataType[] { new NumberJointDataType(value) };
    }
}
