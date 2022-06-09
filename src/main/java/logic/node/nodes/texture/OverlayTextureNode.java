package logic.node.nodes.texture;

import control.type_enums.MixingAlgorithmType;
import control.type_enums.NodeType;
import logic.led.LogicTexture;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.TextureJointDataType;

public class OverlayTextureNode extends SimpleTextureNode {

    private final MixingAlgorithmType mixingAlgorithmType;

    public OverlayTextureNode(int nodeIndex, MixingAlgorithmType mixingAlgorithmType) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new TextureJointDataType(), "Over"),
                        new InputJoint(new TextureJointDataType(), "Under")
                },
                new OutputJoint[] {
                        new OutputJoint(
                                new TextureJointDataType(),
                                "Output [" + mixingAlgorithmType.getName().substring(0, 3).toUpperCase() + "]"
                        )
                },
                NodeType.OVERLAY_TEXTURE_NODE,
                new Object[] { mixingAlgorithmType }
        );
        this.mixingAlgorithmType = mixingAlgorithmType;
    }

    @Override
    public TextureJointDataType[] function(InputJoint[] inputJoints) {
        LogicTexture textureOver = (LogicTexture) inputJoints[0].getJointDataType().getData();
        LogicTexture textureUnder = (LogicTexture) inputJoints[1].getJointDataType().getData();

        switch(this.mixingAlgorithmType) {
            case OVERLAY -> {
                return new TextureJointDataType[] {
                        new TextureJointDataType(LogicTexture.getTextureOverlay(textureOver, textureUnder))
                };
            }
            case ADDITIVE -> {
                return new TextureJointDataType[] {
                        new TextureJointDataType(LogicTexture.getTextureAdditive(textureOver, textureUnder))
                };
            }
            default -> {
                return new TextureJointDataType[] { new TextureJointDataType() };
            }
        }
    }
}
