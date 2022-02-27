package logic.node.nodes.mask;

import logic.led.LogicMask;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.MaskJointDataType;
import logic.node.joint.joint_types.NumberJointDataType;

public class ScaleMaskNode extends MaskNode {

    public ScaleMaskNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new MaskJointDataType(), "Mask"),
                        new InputJoint(new NumberJointDataType(), "Factor X"),
                        new InputJoint(new NumberJointDataType(), "Factor Y")
                },
                new OutputJoint[] {
                        new OutputJoint(new MaskJointDataType(), "Output")
                }
        );
    }

    @Override
    public MaskJointDataType[] function(InputJoint[] inputJoints) {
        LogicMask logicMask = (LogicMask) inputJoints[0].getJointDataType().getData();
        double scaleX = (Double) inputJoints[1].getJointDataType().getData();
        double scaleY = (Double) inputJoints[2].getJointDataType().getData();

        return new MaskJointDataType[] {
                new MaskJointDataType(LogicMask.getScaledMask(logicMask, scaleX, scaleY))
        };
    }
}
