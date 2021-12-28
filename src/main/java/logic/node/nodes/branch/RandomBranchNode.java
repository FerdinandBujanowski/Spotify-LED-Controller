package logic.node.nodes.branch;

import control.type_enums.JointType;
import logic.node.LogicNode;
import logic.node.joint.*;
import logic.node.joint.joint_types.JointDataType;

import java.util.Random;

public class RandomBranchNode extends LogicNode {

    public RandomBranchNode(int nodeIndex, JointType jointType) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(JointType.getCopyOfDataTypeByJointType(jointType), "Input A"),
                        new InputJoint(JointType.getCopyOfDataTypeByJointType(jointType), "Input B")
                },
                new OutputJoint[] {
                        new OutputJoint(JointType.getCopyOfDataTypeByJointType(jointType), "Output")
                }
        );
    }

    @Override
    public JointDataType[] function(InputJoint[] inputJoints) {
        Random random = new Random();
        double randomDouble = random.nextDouble();
        if(randomDouble < 0.5d) {
            return new JointDataType[] { inputJoints[0].getJointDataType() };
        } else {
            return new JointDataType[] { inputJoints[1].getJointDataType() };
        }
    }
}
