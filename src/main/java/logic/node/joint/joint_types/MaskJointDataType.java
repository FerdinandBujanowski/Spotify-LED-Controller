package logic.node.joint.joint_types;

import logic.led.LogicMask;

public class MaskJointDataType implements JointDataType {

    private LogicMask data;

    public MaskJointDataType(LogicMask data) {
        this.data = data;
    }
    public MaskJointDataType() {
        this.data = (LogicMask) this.getDefaultData();
    }
    @Override
    public Object getDefaultData() {
        return new LogicMask();
    }

    @Override
    public Object getData() {
        return this.data;
    }

    @Override
    public void setData(Object data) {
        this.data = (LogicMask) data;
    }
}
