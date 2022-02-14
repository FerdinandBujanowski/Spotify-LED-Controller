package logic.node.nodes.mask;

import logic.mask.LogicMask;
import logic.node.LogicNode;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.IntegerJointDataType;
import logic.node.joint.joint_types.MaskJointDataType;
import logic.node.joint.joint_types.UnitNumberJointDataType;

public class MaskNode extends LogicNode {

    public MaskNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new IntegerJointDataType(), "Degree"),
                        new InputJoint(new UnitNumberJointDataType(), "Intensity")
                },
                new OutputJoint[] {
                        new OutputJoint(new MaskJointDataType(), "Output")
                }
        );
    }

    public MaskNode(int nodeIndex, InputJoint[] inputJoints, OutputJoint[] outputJoints) {
        super(nodeIndex, inputJoints, outputJoints);
    }

    @Override
    public MaskJointDataType[] function(InputJoint[] inputJoints) {
        LogicMask logicMask = new LogicMask();
        int degree = (Integer) inputJoints[0].getJointDataType().getData();
        double intensity = (Double) inputJoints[1].getJointDataType().getData();

        for(int i = -degree; i <= degree; i++) {
            for(int j = -degree; j <= degree; j++) {
                logicMask.setIntensityAt(i, j, intensity);
            }
        }

        logicMask.cleanUp();
        return new MaskJointDataType[] { new MaskJointDataType(logicMask) };
    }

    @Override
    public Double[][] getMaskValues(Integer nullInteger) {
        LogicMask logicMask = (LogicMask) this.getOutputJoints()[0].getJointDataType().getData();
        return logicMask.getValues();
    }
}
