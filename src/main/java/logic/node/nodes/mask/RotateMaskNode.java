package logic.node.nodes.mask;

import control.type_enums.NodeType;
import control.type_enums.PixelAlgorithmType;
import logic.led.LogicMask;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.MaskJointDataType;
import logic.node.joint.joint_types.NumberJointDataType;

public class RotateMaskNode extends SquareMaskNode {

    private final PixelAlgorithmType pixelAlgorithmType;

    public RotateMaskNode(int nodeIndex, PixelAlgorithmType pixelAlgorithmType) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new MaskJointDataType(), "Mask"),
                        new InputJoint(new NumberJointDataType(), "Radians")
                },
                new OutputJoint[] {
                        new OutputJoint(
                                new MaskJointDataType(),
                                "Output [" + pixelAlgorithmType.getName().toUpperCase().substring(0, 3) + "]"
                        )
                },
                NodeType.ROTATE_MASK_NODE,
                new Object[] { pixelAlgorithmType }
        );
        this.pixelAlgorithmType = pixelAlgorithmType;
    }

    @Override
    public MaskJointDataType[] function(InputJoint[] inputJoints) {
        //TODO : zwei verschiedene Algorithmen
        LogicMask logicMask = (LogicMask) inputJoints[0].getJointDataType().getData();
        double radians = (Double) inputJoints[1].getJointDataType().getData();
        return new MaskJointDataType[] {
                new MaskJointDataType(LogicMask.getRotatedMask_Closest(logicMask, radians))
        };
    }
}
