package logic.node.nodes.mask;

import control.type_enums.NodeType;
import logic.led.LogicMask;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.MaskJointDataType;

public class MaskAdditionNode extends SquareMaskNode {

    public MaskAdditionNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new MaskJointDataType(), "Mask A"),
                        new InputJoint(new MaskJointDataType(), "Mask B")
                },
                new OutputJoint[] {
                        new OutputJoint(new MaskJointDataType(), "Output")
                },
                NodeType.MASK_ADDITION_NODE
        );
    }

    @Override
    public MaskJointDataType[] function(InputJoint[] inputJoints) {
        LogicMask maskA = (LogicMask) inputJoints[0].getJointDataType().getData();
        LogicMask maskB = (LogicMask) inputJoints[1].getJointDataType().getData();

        this.logicMask.sweep();
        int degree = Math.max(maskA.getDegree(), maskB.getDegree());
        for(int x = -degree; x <= degree; x++) {
            for(int y = -degree; y <= degree; y++) {
                double intensity = maskA.getIntensityAt(x, y) + maskB.getIntensityAt(x, y);
                if(intensity > 0) {
                    this.logicMask.setIntensityAt(x, y, intensity);
                }
            }
        }
        return new MaskJointDataType[] { new MaskJointDataType(this.logicMask) };
    }
}
