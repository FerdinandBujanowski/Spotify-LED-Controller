package logic.node.joint.joint_types;

import java.awt.*;
import java.io.Serializable;

public class ColorJointDataType implements JointDataType, Serializable {

    Color data;

    public ColorJointDataType(Color data) {
        this.data = data;
    }
    public ColorJointDataType() {
        this.data = (Color) this.getDefaultData();
    }

    @Override
    public Object getDefaultData() {
        return Color.BLACK;
    }

    @Override
    public Object getData() {
        return this.data;
    }

    @Override
    public void setData(Object data) {
        this.data = (Color) data;
    }
}
