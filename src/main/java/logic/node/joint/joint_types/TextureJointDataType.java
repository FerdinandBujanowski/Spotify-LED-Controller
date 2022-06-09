package logic.node.joint.joint_types;

import logic.led.LogicTexture;

import java.io.Serializable;

public class TextureJointDataType implements JointDataType, Serializable {

    private LogicTexture data;

    public TextureJointDataType(LogicTexture data) {
        this.data = data;
    }

    public TextureJointDataType() {
        this.data = (LogicTexture) this.getDefaultData();
    }

    @Override
    public Object getDefaultData() {
        return new LogicTexture();
    }

    @Override
    public Object getData() {
        return this.data;
    }

    @Override
    public void setData(Object data) {
        this.data = (LogicTexture) data;
    }
}
