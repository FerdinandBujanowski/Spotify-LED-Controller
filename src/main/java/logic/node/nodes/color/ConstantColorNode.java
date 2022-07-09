package logic.node.nodes.color;

import control.type_enums.JointType;
import control.type_enums.NodeType;
import logic.led.LogicMask;
import logic.led.LogicTexture;
import logic.node.LogicNode;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.ColorJointDataType;

import java.awt.*;

public class ConstantColorNode extends LogicNode {

    public Color color;

    public ConstantColorNode(int nodeIndex, Color color) {
        super(
                nodeIndex,
                new InputJoint[0],
                new OutputJoint[] {
                        new OutputJoint(
                                new ColorJointDataType(),
                                "[" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + "]"
                        )
                },
                NodeType.CONSTANT_COLOR_NODE,
                new Object[] { color }
        );
        this.color = color;
        this.getOutputJoints()[0].getJointDataType().setData(this.color);
    }

    public ConstantColorNode(int nodeIndex, InputJoint[] inputJoints, OutputJoint[] outputJoints, NodeType nodeType) {
        super(nodeIndex, inputJoints, outputJoints, nodeType);
    }

    @Override
    public ColorJointDataType[] function(InputJoint[] inputJoints) {
        return new ColorJointDataType[] { new ColorJointDataType(this.color) };
    }

    @Override
    public Double[][] getMaskValues(Integer nullInteger) {
        LogicMask logicMask = new LogicMask();
        logicMask.setIntensityAt(0, 0, 1);
        return logicMask.getValues();
    }

    @Override
    public Color[][] getTextureColorValues(Integer nullInteger) {
        LogicTexture logicTexture = new LogicTexture();
        logicTexture.setIntensityAt(0, 0, 1, (Color) this.getOutputJoints()[0].getJointDataType().getData());
        return logicTexture.getColorValues();
    }
}
