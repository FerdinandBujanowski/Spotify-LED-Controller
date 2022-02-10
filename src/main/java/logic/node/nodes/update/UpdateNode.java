package logic.node.nodes.update;

import control.type_enums.JointType;
import logic.node.LogicNode;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.BooleanJointDataType;
import logic.node.joint.joint_types.JointDataType;


public class UpdateNode extends LogicNode {

    private boolean toggleBoolean;
    private JointDataType oldData;

    public UpdateNode(int nodeIndex, JointType jointType) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new BooleanJointDataType(), "Toggle"),
                        new InputJoint(JointType.getCopyOfDataTypeByJointType(jointType), "Data")
                },
                new OutputJoint[] {
                        new OutputJoint(JointType.getCopyOfDataTypeByJointType(jointType), "Data")
                }
        );
        this.toggleBoolean = false;
        this.oldData = JointType.getCopyOfDataTypeByJointType(jointType);
    }

    @Override
    public JointDataType[] function(InputJoint[] inputJoints) {
        boolean newBoolean = (Boolean) inputJoints[0].getJointDataType().getData();
        if((!this.toggleBoolean && newBoolean) || (this.toggleBoolean && !newBoolean)) {
            this.toggleBoolean = !this.toggleBoolean;
            this.oldData.setData(inputJoints[1].getJointDataType().getData());
        }
        return new JointDataType[] { this.oldData };
    }
}
