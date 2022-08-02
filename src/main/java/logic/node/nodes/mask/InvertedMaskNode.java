package logic.node.nodes.mask;

import control.type_enums.NodeType;
import logic.led.LogicMask;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.IntegerJointDataType;
import logic.node.joint.joint_types.MaskJointDataType;

public class InvertedMaskNode extends SquareMaskNode {

    public InvertedMaskNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new MaskJointDataType(), "Mask"),
                        new InputJoint(new IntegerJointDataType(), "Degree")
                },
                new OutputJoint[] {
                        new OutputJoint(new MaskJointDataType(), "Output")
                },
                NodeType.INVERTED_MASK_NODE
        );
    }

    @Override
    public MaskJointDataType[] function(InputJoint[] inputJoints) {
        this.logicMask.sweep();

        LogicMask inputMask = (LogicMask) inputJoints[0].getJointDataType().getData();
        int degree = (Integer) inputJoints[1].getJointDataType().getData();

        int newDegree = (Math.max(degree, inputMask.getDegree()));

        for(int i = -newDegree; i <= newDegree; i++) {
            for(int j = -newDegree; j <= newDegree; j++) {
                this.logicMask.setIntensityAt(i, j, 1.0 - inputMask.getIntensityAt(i, j));
            }
        }
        this.logicMask.cleanUp();

        return new MaskJointDataType[] { new MaskJointDataType(this.logicMask) };
    }
}
