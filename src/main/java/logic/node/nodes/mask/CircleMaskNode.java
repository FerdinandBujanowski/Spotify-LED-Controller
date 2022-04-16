package logic.node.nodes.mask;

import control.type_enums.NodeType;
import logic.led.LogicMask;
import logic.node.LogicNode;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.IntegerJointDataType;
import logic.node.joint.joint_types.MaskJointDataType;
import logic.node.joint.joint_types.UnitNumberJointDataType;

public class CircleMaskNode extends SquareMaskNode {

    public CircleMaskNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new IntegerJointDataType(), "Degree"),
                        new InputJoint(new UnitNumberJointDataType(), "Intensity")
                },
                new OutputJoint[] {
                        new OutputJoint(new MaskJointDataType(), "Output")
                },
                NodeType.CIRCLE_MASK_NODE
        );
    }

    @Override
    public MaskJointDataType[] function(InputJoint[] inputJoints) {
        LogicMask logicMask = new LogicMask();
        int radius = (Integer) inputJoints[0].getJointDataType().getData();
        double intensity = (Double) inputJoints[1].getJointDataType().getData();

        for(int i = -radius; i <= radius; i++) {
            for(int j = -radius; j <= radius; j++) {
                double hypothenuse = Math.sqrt(Math.pow(i, 2) + Math.pow(j, 2));
                if(hypothenuse <= radius) {
                    logicMask.setIntensityAt(i, j, intensity);
                } else {
                    logicMask.setIntensityAt(i, j, 0.0);
                }
            }
        }
        logicMask.cleanUp();
        return new MaskJointDataType[] { new MaskJointDataType(logicMask) };
    }
}
