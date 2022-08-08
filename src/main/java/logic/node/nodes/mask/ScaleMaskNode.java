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

        double oldLength = inputMask.getDegree() + 0.5;

        double lengthX = oldLength * scaleX;
        double lengthY = oldLength * scaleY;

        switch(this.pixelAlgorithmType) {
            case CLOSEST_NEIGHBOR -> {
                int degreeX = (int)Math.round(lengthX - 0.5);
                int degreeY = (int)Math.round(lengthY - 0.5);

                for(int x = -degreeX; x <= degreeX; x++) {
                    for(int y = -degreeY; y <= degreeY; y++) {
                        int oldX = (int)Math.round(x / scaleX);
                        int oldY = (int)Math.round(y / scaleY);
                        this.logicMask.setIntensityAt(x, y, inputMask.getIntensityAt(oldX, oldY));
                    }
                }
            }
            case LINEAR_INTERPOLATION -> {
                int degreeX = (int)Math.ceil(lengthX - 0.5);
                int degreeY = (int)Math.ceil(lengthX - 0.5);

                for(int x = -degreeX; x <= degreeX; x++) {
                    for(int y = -degreeY; y <= degreeY; y++) {
                        double oldX_d = x / scaleX;
                        int oldX_high = (int)Math.ceil(oldX_d);
                        int oldX_low = (int)Math.floor(oldX_d);
                        double oldY_d = y / scaleY;
                        int oldY_high = (int)Math.ceil(oldY_d);
                        int oldY_low = (int)Math.floor(oldY_d);

                        double intensityX_A = LogicMask.linearInterpolation(
                                inputMask.getIntensityAt(oldX_high, oldY_high),
                                inputMask.getIntensityAt(oldX_low, oldY_high),
                                oldX_high - oldX_d
                        );
                        double intensityX_B = LogicMask.linearInterpolation(
                                inputMask.getIntensityAt(oldX_high, oldY_low),
                                inputMask.getIntensityAt(oldX_low, oldY_low),
                                oldX_high - oldX_d
                        );
                        double finalIntensity = LogicMask.linearInterpolation(
                                intensityX_A,
                                intensityX_B,
                                oldY_high - oldY_d
                        );

                        this.logicMask.setIntensityAt(x, y, finalIntensity);
                    }
                }
            }
        }

        this.logicMask.cleanUp();

        return new MaskJointDataType[] { new MaskJointDataType(this.logicMask) };
    }
}
