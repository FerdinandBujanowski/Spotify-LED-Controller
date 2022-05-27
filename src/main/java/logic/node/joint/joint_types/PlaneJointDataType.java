package logic.node.joint.joint_types;

import logic.led.LogicPlane;

import java.io.Serializable;

public class PlaneJointDataType implements JointDataType, Serializable {

    private LogicPlane data;

    public PlaneJointDataType(LogicPlane data) {
        this.data = data;
    }

    public PlaneJointDataType() {
        this.data = (LogicPlane) this.getDefaultData();
    }

    @Override
    public Object getDefaultData() {
        return new LogicPlane();
    }

    @Override
    public Object getData() {
        return this.data;
    }

    @Override
    public void setData(Object data) {
        this.data = (LogicPlane) data;
    }
}
