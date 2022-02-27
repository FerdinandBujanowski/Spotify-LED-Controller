package logic.node.nodes.mask;

import logic.led.LogicMask;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.MaskJointDataType;

public class MultiplyMaskWithMaskNode extends MaskNode {

    public MultiplyMaskWithMaskNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new MaskJointDataType(), "Mask A"),
                        new InputJoint(new MaskJointDataType(), "Mask B")
                },
                new OutputJoint[] {
                        new OutputJoint(new MaskJointDataType(), "Output")
                }
        );
    }

    @Override
    public MaskJointDataType[] function(InputJoint[] inputJoints) {
        LogicMask maskA = (LogicMask) inputJoints[0].getJointDataType().getData();
        LogicMask maskB = (LogicMask) inputJoints[1].getJointDataType().getData();
        return new MaskJointDataType[] {
                new MaskJointDataType(LogicMask.getJoinedMask_Multiply(maskA, maskB))
        };
    }
}
