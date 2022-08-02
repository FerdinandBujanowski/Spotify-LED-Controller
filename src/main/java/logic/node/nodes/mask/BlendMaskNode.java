package logic.node.nodes.mask;

import control.type_enums.AxisType;
import control.type_enums.NodeType;
import logic.led.LogicMask;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.IntegerJointDataType;
import logic.node.joint.joint_types.MaskJointDataType;
import logic.node.joint.joint_types.UnitNumberJointDataType;

public class BlendMaskNode extends SquareMaskNode {

    private final AxisType axisType;

    public BlendMaskNode(int nodeIndex, AxisType axisType) {
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
                                "Output [" + axisType.name().toUpperCase().substring(0, 3) + "]"
                        )
                },
                NodeType.BLEND_MASK_NODE,
                new Object[] {axisType}
        );
        this.axisType = axisType;
    }

    @Override
    public MaskJointDataType[] function(InputJoint[] inputJoints) {
        this.logicMask.sweep();

        int degree = (int) inputJoints[0].getJointDataType().getData();
        int iteration = (int) inputJoints[1].getJointDataType().getData();
        double alteration = (double) inputJoints[2].getJointDataType().getData();

        LogicMask.blendMask(this.logicMask, degree, iteration, alteration, this.axisType);
        this.logicMask.cleanUp();

        return new MaskJointDataType[] { new MaskJointDataType(this.logicMask) };
    }
}
