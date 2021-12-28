package logic.node.nodes.branch;

import control.type_enums.JointType;
import logic.node.LogicNode;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.BooleanJointDataType;
import logic.node.joint.joint_types.JointDataType;

public class ConditionalBranchNode extends LogicNode {

    public ConditionalBranchNode(int nodeIndex, JointType jointType) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new BooleanJointDataType(), "Condition"),
                        new InputJoint(JointType.getCopyOfDataTypeByJointType(jointType), "Input A"),
                        new InputJoint(JointType.getCopyOfDataTypeByJointType(jointType), "Input B"),
                },
                new OutputJoint[] {
                        new OutputJoint(JointType.getCopyOfDataTypeByJointType(jointType), "Output")
                }
        );
    }

    @Override
    public JointDataType[] function(InputJoint[] inputJoints) {
        if((boolean)inputJoints[0].getJointDataType().getData()) {
            return new JointDataType[] { inputJoints[1].getJointDataType() };
        } else {
            return new JointDataType[] { inputJoints[2].getJointDataType() };
        }
    }
}
