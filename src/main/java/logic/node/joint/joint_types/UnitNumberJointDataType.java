package logic.node.joint.joint_types;

import java.io.Serializable;

/**
 * Unit Number bezieht sich auf Zahlen, die zwischen 0 und 1 liegen
 */
public class UnitNumberJointDataType implements JointDataType, Serializable {

    private Double data;

    public UnitNumberJointDataType(Double data) {
        this.setData(data);
    }
    public UnitNumberJointDataType() {
        this.data = (double) getDefaultData();
    }


    @Override
    public Object getDefaultData() {
        return 0.d;
    }

    @Override
    public Object getData() {
        return this.data;
    }

    @Override
    public void setData(Object data) {
        if((Double)data < 0) this.data = 0.d;
        else if((Double)data > 1) this.data = 1.d;
        else this.data = (Double)data;
    }
}
