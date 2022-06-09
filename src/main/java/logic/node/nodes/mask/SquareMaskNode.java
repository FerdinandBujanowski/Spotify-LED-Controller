package logic.node.nodes.mask;

import control.type_enums.NodeType;
import logic.led.LogicMask;
import logic.node.LogicNode;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.IntegerJointDataType;
import logic.node.joint.joint_types.MaskJointDataType;
import logic.node.joint.joint_types.UnitNumberJointDataType;

public class SquareMaskNode extends LogicNode {

    public SquareMaskNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new IntegerJointDataType(), "Degree"),
                        new InputJoint(new UnitNumberJointDataType(), "Intensity")
                },
                new OutputJoint[] {
                        new OutputJoint(new MaskJointDataType(), "Output")
                },
                NodeType.SQUARE_MASK_NODE
        );
    }

    public SquareMaskNode(int nodeIndex, InputJoint[] inputJoints, OutputJoint[] outputJoints, NodeType nodeType, Object[] extraParameters) {
        super(nodeIndex, inputJoints, outputJoints, nodeType, extraParameters);
    }

    public SquareMaskNode(int nodeIndex, InputJoint[] inputJoints, OutputJoint[] outputJoints, NodeType nodeType) {
        super(nodeIndex, inputJoints, outputJoints, nodeType);
    }

    @Override
    public MaskJointDataType[] function(InputJoint[] inputJoints) {
        int degree = (Integer) inputJoints[0].getJointDataType().getData();
        double intensity = (Double) inputJoints[1].getJointDataType().getData();

        LogicMask logicMask = LogicMask.getSquareMask(degree, intensity);
        return new MaskJointDataType[] { new MaskJointDataType(logicMask) };
    }

    @Override
    public Double[][] getMaskValues(Integer nullInteger) {
        LogicMask logicMask = (LogicMask) this.getOutputJoints()[0].getJointDataType().getData();
        return logicMask.getValues();
    }
}
