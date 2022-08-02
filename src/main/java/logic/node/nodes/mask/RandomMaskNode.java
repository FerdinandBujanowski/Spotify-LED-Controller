package logic.node.nodes.mask;

import control.type_enums.NodeType;
import logic.led.LogicMask;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.IntegerJointDataType;
import logic.node.joint.joint_types.JointDataType;
import logic.node.joint.joint_types.MaskJointDataType;

import java.util.Random;

public class RandomMaskNode extends SquareMaskNode {

    private final Random random;

    public RandomMaskNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new IntegerJointDataType(), "Degree"),
                        new InputJoint(new MaskJointDataType(), "Input (Digital)")
                },
                new OutputJoint[] {
                        new OutputJoint(new MaskJointDataType(), "Output")
                },
                NodeType.RANDOM_MASK_NODE
        );
        this.random = new Random();
    }

    @Override
    public MaskJointDataType[] function(InputJoint[] inputJoints) {
        this.logicMask.sweep();

        int degree = (Integer) inputJoints[0].getJointDataType().getData();
        LogicMask inputMask = (LogicMask) inputJoints[1].getJointDataType().getData();
        for(int x = -degree; x <= degree; x++) {
            for(int y = -degree; y <= degree; y++) {
                if(inputMask.isEmpty() || inputMask.getIntensityAt(x, y) > 0) {
                    this.logicMask.setIntensityAt(x, y, this.random.nextDouble());
                }
            }
        }
        this.logicMask.cleanUp();

        return new MaskJointDataType[] { new MaskJointDataType(this.logicMask) };
    }
}
