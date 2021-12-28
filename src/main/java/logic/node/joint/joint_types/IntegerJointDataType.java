package logic.node.joint.joint_types;
import java.io.Serializable;

public class IntegerJointDataType implements JointDataType, Serializable {

    private Integer data;

    public IntegerJointDataType(Integer data) {
        this.data = data;
    }

    public IntegerJointDataType() {
        this.data = (Integer) this.getDefaultData();
    }

    @Override
    public Object getDefaultData() {
        return 0;
    }

    @Override
    public Object getData() {
        return this.data;
    }

    @Override
    public void setData(Object data) {
        this.data = (Integer) data;
    }
}
