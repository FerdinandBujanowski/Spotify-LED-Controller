package logic.node.nodes.mask;

import control.type_enums.NodeType;
import control.type_enums.PixelAlgorithmType;
import logic.led.LogicMask;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.MaskJointDataType;
import logic.node.joint.joint_types.NumberJointDataType;

public class ScaleMaskNode extends SquareMaskNode {

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
        this.logicMask.sweep();

        LogicMask inputMask = (LogicMask) inputJoints[0].getJointDataType().getData();
        double scaleX = (Double) inputJoints[1].getJointDataType().getData();
        double scaleY = (Double) inputJoints[2].getJointDataType().getData();
        if(scaleX == 0) scaleX = 1.d;
        if(scaleY == 0) scaleY = 1.d;

        switch(this.pixelAlgorithmType) {
            case CLOSEST_NEIGHBOR -> {
                int oldLength = (inputMask.getDegree() * 2) + 1;

                int lengthX = (int)Math.round(oldLength * scaleX);
                int degreeX = (lengthX - 1) / 2;
                int lengthY = (int)Math.round(oldLength * scaleY);
                int degreeY = (lengthY - 1) / 2;
                for(int x = -degreeX; x <= degreeX; x++) {
                    for(int y = -degreeY; y <= degreeY; y++) {
                        int oldX = (int)Math.round(x / scaleX);
                        int oldY = (int)Math.round(y / scaleY);
                        this.logicMask.setIntensityAt(x, y, inputMask.getIntensityAt(oldX, oldY));
                    }
                }
            }
            case LINEAR_INTERPOLATION -> {

            }
        }

        this.logicMask.cleanUp();

        return new MaskJointDataType[] { new MaskJointDataType(this.logicMask) };
    }
}
