package logic.node.nodes.mask;

import control.type_enums.NodeType;
import logic.led.LogicMask;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.IntegerJointDataType;
import logic.node.joint.joint_types.MaskJointDataType;

import java.awt.*;

public class MoveMaskNode extends SquareMaskNode {

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
                },
                NodeType.MOVE_MASK_NODE
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
