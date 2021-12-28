package logic.node.joint.joint_types;

import java.io.Serializable;

public class BooleanJointDataType implements JointDataType, Serializable {

    private Boolean data;

    public BooleanJointDataType(boolean data) {
        this.data = data;
    }
    public BooleanJointDataType() {
        this.data = (Boolean) this.getDefaultData();
    }

    @Override
    public Object getDefaultData() {
        return false;
    }

    @Override
    public Object getData() {
        return this.data;
    }

    @Override
    public void setData(Object data) {
        this.data = (Boolean) data;
    }
}
