package logic.node.nodes.mask;

import control.type_enums.NodeType;
import logic.led.LogicMask;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.MaskJointDataType;

public class DigitalMaskNode extends SquareMaskNode {

    public DigitalMaskNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new MaskJointDataType(), "Input")
                },
                new OutputJoint[] {
                        new OutputJoint(new MaskJointDataType(), "Output")
                },
                NodeType.DIGITAL_MASK_NODE
        );
    }

    @Override
    public MaskJointDataType[] function(InputJoint[] inputJoints) {
        this.logicMask.sweep();

        LogicMask inputMask = (LogicMask) inputJoints[0].getJointDataType().getData();
        int degree = inputMask.getDegree();
        for(int x = -degree; x <= degree; x++) {
            for(int y = -degree; y <= degree; y++) {
                if(inputMask.getIntensityAt(x, y) > 0) {
                    this.logicMask.setIntensityAt(x, y, 1);
                }
            }
        }

        return new MaskJointDataType[] { new MaskJointDataType(this.logicMask) };
    }
}
