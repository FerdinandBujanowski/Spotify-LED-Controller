package logic.node.nodes.mask;

import control.type_enums.NodeType;
import logic.led.LogicMask;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.MaskJointDataType;

public class MaskSubtractionNode extends SquareMaskNode {

    public MaskSubtractionNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new MaskJointDataType(), "Mask A"),
                        new InputJoint(new MaskJointDataType(), "Mask B")
                },
                new OutputJoint[] {
                        new OutputJoint(new MaskJointDataType(), "Output")
                },
                NodeType.MASK_SUBTRACTION_NODE
        );
    }

    @Override
    public MaskJointDataType[] function(InputJoint[] inputJoints) {
        this.logicMask.sweep();

        LogicMask maskA = (LogicMask) inputJoints[0].getJointDataType().getData();
        LogicMask maskB = (LogicMask) inputJoints[1].getJointDataType().getData();
        int degree = Math.max(maskA.getDegree(), maskB.getDegree());

        for(int x = -degree; x <= degree; x++) {
            for(int y = -degree; y <= degree; y++) {
                double intensity = maskA.getIntensityAt(x, y) - maskB.getIntensityAt(x, y);
                if(intensity > 0) {
                    this.logicMask.setIntensityAt(x, y, intensity);
                }
            }
        }
        return new MaskJointDataType[] { new MaskJointDataType(this.logicMask) };
    }
}