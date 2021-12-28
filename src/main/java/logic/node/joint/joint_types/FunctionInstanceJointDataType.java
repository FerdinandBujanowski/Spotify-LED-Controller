package logic.node.joint.joint_types;

import logic.node.LogicNode;

public class FunctionInstanceJointDataType implements JointDataType {

    LogicNode functionInstance;

    public FunctionInstanceJointDataType(LogicNode functionInstance) {
        this.functionInstance = functionInstance;
    }
    public FunctionInstanceJointDataType() {
        this.functionInstance = (LogicNode) this.getDefaultData();
    }
    @Override
    public Object getDefaultData() {
        return null;
    }

    @Override
    public Object getData() {
        return functionInstance;
    }

    @Override
    public void setData(Object data) {
        this.functionInstance = (LogicNode) data;
    }
}
