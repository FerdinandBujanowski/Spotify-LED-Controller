package logic.node.nodes.mask;

import control.type_enums.NodeType;
import logic.led.LogicMask;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.MaskJointDataType;
import logic.node.joint.joint_types.UnitNumberJointDataType;

public class MultiplyMaskWithUnitNode extends SquareMaskNode {

    public MultiplyMaskWithUnitNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new MaskJointDataType(), "Mask"),
                        new InputJoint(new UnitNumberJointDataType(), "Scalar")
                },
                new OutputJoint[] {
                        new OutputJoint(new MaskJointDataType(), "Output")
                },
                NodeType.MASK_X_NUMBER_NODE
        );
    }

    @Override
    public MaskJointDataType[] function(InputJoint[] inputJoints) {
        this.logicMask.sweep();
        LogicMask inputMask = (LogicMask) inputJoints[0].getJointDataType().getData();
        double scalar = (Double) inputJoints[1].getJointDataType().getData();
        int degree = inputMask.getDegree();

        for(int x = -degree; x <= degree; x++) {
            for(int y = -degree; y <= degree; y++) {
                this.logicMask.setIntensityAt(x, y, inputMask.getIntensityAt(x, y) * scalar);
            }
        }
        this.logicMask.cleanUp();

        return new MaskJointDataType[] { new MaskJointDataType(this.logicMask) };
    }
}
