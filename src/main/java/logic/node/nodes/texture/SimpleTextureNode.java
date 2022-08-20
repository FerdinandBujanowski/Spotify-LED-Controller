package logic.node.nodes.texture;

import control.type_enums.NodeType;
import logic.led.LogicMask;
import logic.led.LogicTexture;
import logic.node.LogicNode;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.ColorJointDataType;
import logic.node.joint.joint_types.MaskJointDataType;
import logic.node.joint.joint_types.TextureJointDataType;

import java.awt.*;

public class SimpleTextureNode extends LogicNode {

    public LogicTexture logicTexture;

    public SimpleTextureNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new MaskJointDataType(), "Mask"),
                        new InputJoint(new ColorJointDataType(), "Color")
                },
                new OutputJoint[] {
                        new OutputJoint(new TextureJointDataType(), "Output")
                },
                NodeType.SIMPLE_TEXTURE_NODE
        );
        this.logicTexture = new LogicTexture();
    }

    public SimpleTextureNode(int nodeIndex, InputJoint[] inputJoints, OutputJoint[] outputJoints, NodeType nodeType, Object[] extraParameters) {
        super(nodeIndex, inputJoints, outputJoints, nodeType, extraParameters);
        this.logicTexture = new LogicTexture();
    }

    @Override
    public TextureJointDataType[] function(InputJoint[] inputJoints) {
        LogicMask logicMask = (LogicMask) inputJoints[0].getJointDataType().getData();
        Color color = (Color) inputJoints[1].getJointDataType().getData();
        int degree = logicMask.getDegree();
        for(int x = -degree; x <= degree; x++) {
            for(int y = -degree; y <= degree; y++) {
                this.logicTexture.setIntensityAt(x, y, logicMask.getIntensityAt(x, y), color);
            }
        }
        logicTexture.getLogicMask().cleanUp();
        return new TextureJointDataType[] {
                new TextureJointDataType(this.logicTexture)
        };
    }

    @Override
    public Double[][] getMaskValues(Integer nullInteger) {
        LogicTexture logicTexture = (LogicTexture) this.getOutputJoints()[0].getJointDataType().getData();
        return logicTexture.getLogicMask().getValues();
    }

    @Override
    public Color[][] getTextureColorValues(Integer nullInteger) {
        LogicTexture logicTexture = (LogicTexture) this.getOutputJoints()[0].getJointDataType().getData();
        return logicTexture.getColorValues();
    }
}
