package logic.node.nodes.mask;

import control.type_enums.BlendType;
import control.type_enums.NodeType;
import logic.led.LogicMask;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.IntegerJointDataType;
import logic.node.joint.joint_types.MaskJointDataType;
import logic.node.joint.joint_types.UnitNumberJointDataType;

public class BlendMaskNode extends SquareMaskNode {

    private final BlendType blendType;

    public BlendMaskNode(int nodeIndex, BlendType blendType) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new IntegerJointDataType(), "Degree"),
                        new InputJoint(new IntegerJointDataType(), "Iteration"),
                        new InputJoint(new UnitNumberJointDataType(), "Alteration")
                },
                new OutputJoint[] {
                        new OutputJoint(
                                new MaskJointDataType(),
                                "Output [" + blendType.name().toUpperCase().substring(0, 3) + "]"
                        )
                },
                NodeType.BLEND_MASK_NODE,
                new Object[] { blendType }
        );
        this.blendType = blendType;
    }

    @Override
    public MaskJointDataType[] function(InputJoint[] inputJoints) {
        int degree = (int) inputJoints[0].getJointDataType().getData();
        int iteration = (int) inputJoints[1].getJointDataType().getData();
        double alteration = (double) inputJoints[2].getJointDataType().getData();

        LogicMask logicMask = LogicMask.getBlendMask(degree, iteration, alteration, this.blendType);
        return new MaskJointDataType[] { new MaskJointDataType(logicMask) };
    }
}
