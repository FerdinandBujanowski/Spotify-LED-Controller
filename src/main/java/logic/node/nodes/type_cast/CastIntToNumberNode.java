package logic.node.nodes.type_cast;

import control.type_enums.NodeType;
import logic.node.LogicNode;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.IntegerJointDataType;
import logic.node.joint.joint_types.NumberJointDataType;

public class CastIntToNumberNode extends LogicNode {

    public CastIntToNumberNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new IntegerJointDataType(), "Integer")
                },
                new OutputJoint[] {
                        new OutputJoint(new NumberJointDataType(), "Number")
                },
                NodeType.INT_TO_NUMBER_NODE
        );
    }

    @Override
    public NumberJointDataType[] function(InputJoint[] inputJoints) {
        int intValue = (Integer) inputJoints[0].getJointDataType().getData();
        return new NumberJointDataType[] {
                new NumberJointDataType(intValue)
        };
    }
}
