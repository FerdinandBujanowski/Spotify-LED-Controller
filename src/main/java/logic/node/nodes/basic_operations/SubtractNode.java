package logic.node.nodes.basic_operations;

import control.type_enums.NodeType;
import logic.node.LogicNode;
import logic.node.joint.*;
import logic.node.joint.joint_types.NumberJointDataType;

public class SubtractNode extends LogicNode {

    public SubtractNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new NumberJointDataType(), "Number A"),
                        new InputJoint(new NumberJointDataType(), "Number B")
                },
                new OutputJoint[] {
                        new OutputJoint(new NumberJointDataType(), "Difference")
                },
                NodeType.SUBTRACT_NODE
        );
    }

    @Override
    public NumberJointDataType[] function(InputJoint[] inputJoints) {
        double difference =
                (Double) inputJoints[0].getJointDataType().getData()
                - (Double) inputJoints[1].getJointDataType().getData();
        return new NumberJointDataType[] { new NumberJointDataType(difference) };
    }
}
