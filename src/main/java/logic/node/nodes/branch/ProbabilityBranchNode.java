package logic.node.nodes.branch;

import control.type_enums.JointType;
import control.type_enums.NodeType;
import logic.node.LogicNode;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.BooleanJointDataType;
import logic.node.joint.joint_types.JointDataType;
import logic.node.joint.joint_types.UnitNumberJointDataType;

import java.util.Random;

public class ProbabilityBranchNode extends LogicNode {

    public ProbabilityBranchNode(int nodeIndex, JointType jointType) {
        super(
                nodeIndex,
                new InputJoint[]{
                        new InputJoint(new UnitNumberJointDataType(), "Probability"),
                        new InputJoint(JointType.getCopyOfDataTypeByJointType(jointType), "Input A"),
                        new InputJoint(JointType.getCopyOfDataTypeByJointType(jointType), "Input B"),
                },
                new OutputJoint[]{
                        new OutputJoint(JointType.getCopyOfDataTypeByJointType(jointType), "Output")
                },
                NodeType.PROBABILITY_BRANCH_NODE,
                new Object[] { jointType }
        );
    }

    @Override
    public JointDataType[] function(InputJoint[] inputJoints) {
        Random random = new Random();
        if(random.nextDouble() < (Double)inputJoints[0].getJointDataType().getData()) {
            return new JointDataType[] { inputJoints[1].getJointDataType() };
        } else {
            return new JointDataType[] { inputJoints[2].getJointDataType() };
        }
    }
}
