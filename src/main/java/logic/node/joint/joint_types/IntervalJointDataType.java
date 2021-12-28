package logic.node.joint.joint_types;

import logic.node.joint.own_types.Interval;

import java.io.Serializable;

public class IntervalJointDataType implements JointDataType, Serializable {

    private Interval data;

    public IntervalJointDataType(Interval data) {
        this.data = data;
    }
    public IntervalJointDataType() {
        this.data = (Interval) this.getDefaultData();
    }
    @Override
    public Object getDefaultData() {
        return new Interval(0, 1);
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public void setData(Object data) {
        this.data = (Interval) data;
    }
}
