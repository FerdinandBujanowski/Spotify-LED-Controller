package logic.node.nodes.mask;

import control.type_enums.AxisType;
import control.type_enums.NodeType;
import logic.led.LogicMask;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.MaskJointDataType;

public class MirrorMaskNode extends SquareMaskNode {

    private final AxisType axisType;

    public MirrorMaskNode(int nodeIndex, AxisType axisType) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new MaskJointDataType(), "Mask")
                },
                new OutputJoint[] {
                        new OutputJoint(new MaskJointDataType(), "Output [" + axisType.name().substring(0, 3).toUpperCase() + "]")
                },
                NodeType.MIRROR_MASK_NODE,
                new Object[] { axisType }
        );

        this.axisType = axisType;
    }

    @Override
    public MaskJointDataType[] function(InputJoint[] inputJoints) {
        this.logicMask.sweep();

        LogicMask inputMask = (LogicMask) inputJoints[0].getJointDataType().getData();
        int newX = 1, newY = 1;
        switch(this.axisType) {
            case HORIZONTAL -> {
                newY = -1;
            }
            case VERTICAL -> {
                newX = -1;
            }
            case CIRCULAR -> {
                newX = -1;
                newY = -1;
            }
        }
        int degree = inputMask.getDegree();
        for(int x = -degree; x <= degree; x++) {
            for(int y = -degree; y <= degree; y++) {
                this.logicMask.setIntensityAt(x, y, inputMask.getIntensityAt(newX * x, newY * y));
            }
        }
        this.logicMask.cleanUp();

        return new MaskJointDataType[] { new MaskJointDataType(this.logicMask) };
    }
}
