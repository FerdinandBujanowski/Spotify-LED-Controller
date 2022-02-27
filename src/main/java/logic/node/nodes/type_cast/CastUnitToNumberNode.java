package logic.node.nodes.type_cast;

import logic.node.LogicNode;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.NumberJointDataType;
import logic.node.joint.joint_types.UnitNumberJointDataType;

public class CastUnitToNumberNode extends LogicNode {

    public CastUnitToNumberNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new UnitNumberJointDataType(), "Unit Number")
                },
                new OutputJoint[] {
                        new OutputJoint(new NumberJointDataType(), "Number")
                }
        );
    }

    @Override
    public NumberJointDataType[] function(InputJoint[] inputJoints) {
        return new NumberJointDataType[] {
                new NumberJointDataType((Double)inputJoints[0].getJointDataType().getData())
        };
    }
}
