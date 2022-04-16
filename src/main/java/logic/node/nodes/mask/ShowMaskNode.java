package logic.node.nodes.mask;

import control.type_enums.NodeType;
import logic.led.LogicMask;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.MaskJointDataType;

public class ShowMaskNode extends SquareMaskNode {

    public ShowMaskNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new MaskJointDataType(), "Input")
                },
                new OutputJoint[] {},
                NodeType.SHOW_MASK_NODE
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
