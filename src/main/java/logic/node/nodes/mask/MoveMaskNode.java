package logic.node.nodes.mask;

import logic.led.LogicMask;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.IntegerJointDataType;
import logic.node.joint.joint_types.MaskJointDataType;

import java.awt.*;

public class MoveMaskNode extends MaskNode {

    public MoveMaskNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new MaskJointDataType(), "Mask"),
                        new InputJoint(new IntegerJointDataType(), "Move X"),
                        new InputJoint(new IntegerJointDataType(), "Move Y")
                },
                new OutputJoint[] {
                        new OutputJoint(new MaskJointDataType(), "Output")
                }
        );
    }

    @Override
    public MaskJointDataType[] function(InputJoint[] inputJoints) {
        LogicMask mask = (LogicMask) inputJoints[0].getJointDataType().getData();
        Point movement = new Point(
                (Integer) inputJoints[1].getJointDataType().getData(),
                (Integer) inputJoints[2].getJointDataType().getData()
        );
        return new MaskJointDataType[] { new MaskJointDataType(LogicMask.getMovedMask(mask, movement)) };
    }
}
