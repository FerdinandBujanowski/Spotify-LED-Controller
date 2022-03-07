package logic.node.nodes.mask;

import control.type_enums.NodeType;
import control.type_enums.PixelAlgorithmType;
import logic.led.LogicMask;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.MaskJointDataType;
import logic.node.joint.joint_types.NumberJointDataType;

public class ScaleMaskNode extends MaskNode {

    private final PixelAlgorithmType pixelAlgorithmType;

    public ScaleMaskNode(int nodeIndex, PixelAlgorithmType pixelAlgorithmType) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new MaskJointDataType(), "Mask"),
                        new InputJoint(new NumberJointDataType(), "Factor X"),
                        new InputJoint(new NumberJointDataType(), "Factor Y")
                },
                new OutputJoint[] {
                        new OutputJoint(
                                new MaskJointDataType(),
                                "Output [" + pixelAlgorithmType.getName().toUpperCase().substring(0, 3) + "]"
                        )
                },
                NodeType.SCALE_MASK_NODE,
                new Object[] { pixelAlgorithmType }
        );
        this.pixelAlgorithmType = pixelAlgorithmType;
    }

    @Override
    public MaskJointDataType[] function(InputJoint[] inputJoints) {
        LogicMask logicMask = (LogicMask) inputJoints[0].getJointDataType().getData();
        double scaleX = (Double) inputJoints[1].getJointDataType().getData();
        double scaleY = (Double) inputJoints[2].getJointDataType().getData();

        if(this.pixelAlgorithmType == PixelAlgorithmType.CLOSEST_NEIGHBOR) {

            return new MaskJointDataType[] {
                    new MaskJointDataType(LogicMask.getScaledMask_Closest(logicMask, scaleX, scaleY))
            };
        } else if(this.pixelAlgorithmType == PixelAlgorithmType.LINEAR_INTERPOLATION) {
            return new MaskJointDataType[] {
                    new MaskJointDataType(LogicMask.getScaledMask_Linear(logicMask, scaleX, scaleY))
            };
        } else return new MaskJointDataType[0];
    }
}
