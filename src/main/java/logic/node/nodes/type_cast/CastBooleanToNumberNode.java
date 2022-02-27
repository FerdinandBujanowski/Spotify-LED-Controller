package logic.node.nodes.type_cast;

import logic.node.LogicNode;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.BooleanJointDataType;
import logic.node.joint.joint_types.NumberJointDataType;

public class CastBooleanToNumberNode extends LogicNode {

    public CastBooleanToNumberNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new BooleanJointDataType(), "Boolean")
                },
                new OutputJoint[] {
                        new OutputJoint(new NumberJointDataType(), "Number")
                }
        );
    }

    @Override
    public NumberJointDataType[] function(InputJoint[] inputJoints) {
        double value = (Boolean)inputJoints[0].getJointDataType().getData() ? 1.d : 0.d;
        return new NumberJointDataType[] {
                new NumberJointDataType(value)
        };
    }
}
