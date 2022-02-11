package logic.node.nodes.mask;

import logic.mask.LogicMask;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.IntegerJointDataType;
import logic.node.joint.joint_types.MaskJointDataType;

public class InvertedMaskNode extends MaskNode {

    public InvertedMaskNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new MaskJointDataType(), "Mask"),
                        new InputJoint(new IntegerJointDataType(), "Degree")
                },
                new OutputJoint[] {
                        new OutputJoint(new MaskJointDataType(), "Output")
                }
        );
    }

    @Override
    public MaskJointDataType[] function(InputJoint[] inputJoints) {
        LogicMask maskA = (LogicMask) inputJoints[0].getJointDataType().getData();
        int degree = (Integer) inputJoints[1].getJointDataType().getData();
        return new MaskJointDataType[] { new MaskJointDataType(LogicMask.getInvertedMask(maskA, degree)) };
    }
}
