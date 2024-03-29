package logic.node.nodes.constant;

import control.type_enums.NodeType;
import logic.node.LogicNode;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.UnitNumberJointDataType;

public class ConstantUnitNumberNode extends LogicNode {

    private double value;

    public ConstantUnitNumberNode(int nodeIndex, Double value) {
        super(
                nodeIndex,
                new InputJoint[] {},
                new OutputJoint[] {
                        new OutputJoint(new UnitNumberJointDataType(), String.valueOf(value))
                },
                NodeType.CONSTANT_UNIT_NUMBER_NODE,
                new Object[] { value }
        );
        this.value = value;
    }

    @Override
    public UnitNumberJointDataType[] function(InputJoint[] inputJoints) {
        return new UnitNumberJointDataType[] { new UnitNumberJointDataType(this.value ) };
    }
}
