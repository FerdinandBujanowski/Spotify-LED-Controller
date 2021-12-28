package logic.node.joint.joint_types;

import java.io.Serializable;

public class NumberJointDataType implements JointDataType, Serializable {

    private Double data;

    public NumberJointDataType(double data) {
        this.data = data;
    }
    public NumberJointDataType() {
        this.data = (double) getDefaultData();
    }

    @Override
    public Object getDefaultData() {
        return 0.d;
    }

    @Override
    public Double getData() {
        return this.data;
    }

    @Override
    public void setData(Object data) {
        this.data = (Double) data;
    }
}
