package logic.node.nodes.constant;

import control.save.JsonWriter;
import control.type_enums.NodeType;
import logic.led.LogicMask;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.MaskJointDataType;
import logic.node.nodes.mask.SquareMaskNode;

public class ConstantMaskNode extends SquareMaskNode {

    public ConstantMaskNode(int nodeIndex, String jsonPath) {
        super(
                nodeIndex,
                new InputJoint[] {},
                new OutputJoint[] {
                        new OutputJoint(new MaskJointDataType(), "Output")
                },
                NodeType.CONSTANT_MASK_NODE,
                new Object[] { jsonPath }
        );

        this.logicMask = !jsonPath.equals("") ? JsonWriter.getMaskFromFile(jsonPath) : new LogicMask();
        this.getOutputJoints()[0].getJointDataType().setData(this.logicMask);
    }

    @Override
    public MaskJointDataType[] function(InputJoint[] inputJoints) {
        return new MaskJointDataType[] { new MaskJointDataType(this.logicMask) };
    }
}
