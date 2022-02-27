package logic.node.nodes.mask;

import logic.led.LogicMask;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.MaskJointDataType;
import logic.node.joint.joint_types.UnitNumberJointDataType;

public class MultiplyMaskWithUnitNode extends MaskNode {

    public MultiplyMaskWithUnitNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new MaskJointDataType(), "Mask"),
                        new InputJoint(new UnitNumberJointDataType(), "Scalar")
                },
                new OutputJoint[] {
                        new OutputJoint(new MaskJointDataType(), "Output")
                }
        );
    }

    @Override
    public MaskJointDataType[] function(InputJoint[] inputJoints) {
        LogicMask logicMask = (LogicMask) inputJoints[0].getJointDataType().getData();
        double scalar = (Double) inputJoints[1].getJointDataType().getData();
        return new MaskJointDataType[] {
                new MaskJointDataType(LogicMask.getMultipliedMask(logicMask, scalar))
        };
    }
}
