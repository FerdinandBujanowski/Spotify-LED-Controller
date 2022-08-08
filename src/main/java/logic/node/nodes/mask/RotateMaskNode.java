package logic.node.nodes.mask;

import control.type_enums.NodeType;
import control.type_enums.PixelAlgorithmType;
import logic.led.LogicMask;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.MaskJointDataType;
import logic.node.joint.joint_types.NumberJointDataType;

import java.awt.*;

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
        this.logicMask.sweep();
        //TODO : zwei verschiedene Algorithmen
        LogicMask inputMask = (LogicMask) inputJoints[0].getJointDataType().getData();
        double radians = (Double) inputJoints[1].getJointDataType().getData();
        int degree = inputMask.getDegree();
        
        double[] rotatedDegreeCoordinates = LogicMask.rotateCoordinates(degree, degree, radians);
        int rotatedDegree = (int)Math.round(
                Math.max(
                        Math.abs(rotatedDegreeCoordinates[0]),
                        Math.abs(rotatedDegreeCoordinates[1])
                )
        );

        switch(this.pixelAlgorithmType) {
            case CLOSEST_NEIGHBOR -> {
                for(int x = -rotatedDegree; x <= rotatedDegree; x++) {
                    for(int y = -rotatedDegree; y <= rotatedDegree; y++) {
                        double[] rotatedCoordinates = LogicMask.rotateCoordinates(x, y, -radians);
                        Point closestOldCoordinates = new Point(
                                (int)Math.round(rotatedCoordinates[0]),
                                (int)Math.round(rotatedCoordinates[1])
                        );
                        this.logicMask.setIntensityAt(x, y, inputMask.getIntensityAt(closestOldCoordinates.x, closestOldCoordinates.y));
                    }
                }
            }
            case LINEAR_INTERPOLATION -> {
                for(int x = -rotatedDegree; x <= rotatedDegree; x++) {
                    for(int y = -rotatedDegree; y <= rotatedDegree; y++) {
                        double[] rotatedCoordinates = LogicMask.rotateCoordinates(x, y, -radians);

                        int oldX_high = (int)Math.ceil(rotatedCoordinates[0]);
                        int oldX_low = (int)Math.floor(rotatedCoordinates[0]);
                        int oldY_high = (int)Math.ceil(rotatedCoordinates[1]);
                        int oldY_low = (int)Math.floor(rotatedCoordinates[1]);

                        double intensityX_A = LogicMask.linearInterpolation(
                                inputMask.getIntensityAt(oldX_high, oldY_high),
                                inputMask.getIntensityAt(oldX_low, oldY_high),
                                oldX_high - rotatedCoordinates[0]
                        );
                        double intensityX_B = LogicMask.linearInterpolation(
                                inputMask.getIntensityAt(oldX_high, oldY_low),
                                inputMask.getIntensityAt(oldX_low, oldY_low),
                                oldX_high - rotatedCoordinates[0]
                        );
                        double finalIntensity = LogicMask.linearInterpolation(
                                intensityX_A,
                                intensityX_B,
                                oldY_high - rotatedCoordinates[1]
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
