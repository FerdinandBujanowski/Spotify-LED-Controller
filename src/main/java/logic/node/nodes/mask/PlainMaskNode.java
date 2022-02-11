package logic.node.nodes.mask;

import logic.mask.LogicMask;
import logic.node.LogicNode;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.IntegerJointDataType;
import logic.node.joint.joint_types.MaskJointDataType;
import logic.node.joint.joint_types.UnitNumberJointDataType;

public class PlainMaskNode extends LogicNode {

    public PlainMaskNode(int nodeIndex) {
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

    @Override
    public MaskJointDataType[] function(InputJoint[] inputJoints) {
        LogicMask logicMask = new LogicMask();
        int degree = (Integer) inputJoints[0].getJointDataType().getData();
        System.out.println(degree);
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
        int degree = logicMask.getDegree();

        Double[][] values = new Double[(degree * 2) + 1][(degree * 2) + 1];
        for(int i = 0; i < values.length; i++) {
            for(int j = 0; j < values.length; j++) {
                values[i][j] = logicMask.getIntensityAt(i - degree, j - degree);
            }
        }
        return values;
    }
}
