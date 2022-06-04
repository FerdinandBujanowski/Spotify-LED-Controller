package logic.node.nodes.update;

import control.type_enums.JointType;
import control.type_enums.NodeType;
import control.type_enums.UpdateType;
import logic.node.LogicNode;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.BooleanJointDataType;
import logic.node.joint.joint_types.JointDataType;

public class UpdateNode extends LogicNode {

    private boolean toggleBoolean;
    private JointDataType oldData;
    private UpdateType updateType;

    public UpdateNode(int nodeIndex, JointType jointType, UpdateType updateType) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(
                                new BooleanJointDataType(),
                                "Toggle [" + updateType.getCode() + "]"
                        ),
                        new InputJoint(JointType.getCopyOfDataTypeByJointType(jointType), "Data")
                },
                new OutputJoint[] {
                        new OutputJoint(JointType.getCopyOfDataTypeByJointType(jointType), "Data")
                },
                NodeType.UPDATE_NODE,
                new Object[] { jointType, updateType }
        );
        this.toggleBoolean = false;
        this.oldData = JointType.getCopyOfDataTypeByJointType(jointType);
        this.updateType = updateType;
    }

    @Override
    public JointDataType[] function(InputJoint[] inputJoints) {
        boolean newBoolean = (Boolean) inputJoints[0].getJointDataType().getData();
        switch(this.updateType) {
            case FLIP_ALWAYS -> {
                if((!this.toggleBoolean && newBoolean) || (this.toggleBoolean && !newBoolean)) {
                    this.oldData.setData(inputJoints[1].getJointDataType().getData());
                }
            }
            case WHILE_TRUE -> {
                if(newBoolean) {
                    this.oldData.setData(inputJoints[1].getJointDataType().getData());
                }
            }
            case FLIP_TRUE_FALSE -> {
                if(!this.toggleBoolean && newBoolean) {
                    this.oldData.setData(inputJoints[1].getJointDataType().getData());
                }
            }
        }
        if(this.toggleBoolean != newBoolean) this.toggleBoolean = newBoolean;

        return new JointDataType[] { this.oldData };
    }
}
