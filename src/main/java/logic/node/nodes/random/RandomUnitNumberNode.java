package logic.node.nodes.random;

import control.type_enums.NodeType;
import logic.node.LogicNode;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.JointDataType;
import logic.node.joint.joint_types.UnitNumberJointDataType;

import java.util.Random;

public class RandomUnitNumberNode extends LogicNode {

    public RandomUnitNumberNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {},
                new OutputJoint[] {
                        new OutputJoint(new UnitNumberJointDataType(), "Output")
                },
                NodeType.RANDOM_UNIT_NUMBER_NODE
        );
    }

    @Override
    public JointDataType[] function(InputJoint[] inputJoints) {
        Random random = new Random();
        return new JointDataType[] { new UnitNumberJointDataType(random.nextDouble()) };
    }
}
