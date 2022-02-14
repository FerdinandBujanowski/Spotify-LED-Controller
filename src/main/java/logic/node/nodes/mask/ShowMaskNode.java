package logic.node.nodes.mask;

import logic.mask.LogicMask;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.JointDataType;
import logic.node.joint.joint_types.MaskJointDataType;

public class ShowMaskNode extends MaskNode {

    public ShowMaskNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new MaskJointDataType(), "Input")
                },
                new OutputJoint[] {}
        );
    }

    @Override
    public Double[][] getMaskValues(Integer nullInteger) {
        LogicMask logicMask = (LogicMask) this.getInputJoints()[0].getJointDataType().getData();
        return logicMask.getValues();
    }

    @Override
    public MaskJointDataType[] function(InputJoint[] inputJoints) {
        return new MaskJointDataType[] {};
    }
}
