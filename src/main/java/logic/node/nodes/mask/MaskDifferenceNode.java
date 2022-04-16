package logic.node.nodes.mask;

import control.type_enums.NodeType;
import logic.led.LogicMask;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.MaskJointDataType;

public class MaskDifferenceNode extends SquareMaskNode {

    public MaskDifferenceNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new MaskJointDataType(), "Mask A"),
                        new InputJoint(new MaskJointDataType(), "Mask B")
                },
                new OutputJoint[] {
                        new OutputJoint(new MaskJointDataType(), "Output")
                },
                NodeType.MASK_DIFFERENCE_NODE
        );
    }

    @Override
    public MaskJointDataType[] function(InputJoint[] inputJoints) {
        LogicMask maskA = (LogicMask) inputJoints[0].getJointDataType().getData();
        LogicMask maskB = (LogicMask) inputJoints[1].getJointDataType().getData();
        return new MaskJointDataType[] { new MaskJointDataType(LogicMask.getJoinedMask_Difference(maskA, maskB)) };
    }
}
