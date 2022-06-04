package logic.node.nodes.update;

import com.sun.source.tree.BreakTree;
import control.type_enums.JointType;
import control.type_enums.NodeType;
import control.type_enums.UpdateType;
import logic.node.LogicNode;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.BooleanJointDataType;
import logic.node.joint.joint_types.IntegerJointDataType;
import logic.node.joint.joint_types.JointDataType;

public class CountNode extends LogicNode {

    private UpdateType updateType;
    private boolean toggleBoolean;
    private boolean resetBoolean;
    private int count;

    public CountNode(int nodeIndex, UpdateType updateType) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new BooleanJointDataType(), "Toggle [" + updateType.getCode() + "]"),
                        new InputJoint(new BooleanJointDataType(), "Reset [" + UpdateType.FLIP_TRUE_FALSE.getCode() + "]")
                },
                new OutputJoint[] {
                        new OutputJoint(new IntegerJointDataType(), "Count")
                },
                NodeType.COUNT_NODE,
                new Object[] { updateType }
        );

        this.updateType = updateType;
        this.toggleBoolean = false;
        this.resetBoolean = false;
        this.count = 0;
    }

    @Override
    public JointDataType[] function(InputJoint[] inputJoints) {
        boolean newBoolean = (Boolean) inputJoints[0].getJointDataType().getData();
        boolean resetBoolean = (Boolean) inputJoints[1].getJointDataType().getData();

        if(!this.resetBoolean && resetBoolean) {
            this.count = 0;
        }
        if(this.resetBoolean != resetBoolean) {
            this.resetBoolean = resetBoolean;
        }

        if(UpdateType.update(this.toggleBoolean, newBoolean, this.updateType)) {
            this.count++;
        }
        if(this.toggleBoolean != newBoolean) this.toggleBoolean = newBoolean;

        return new JointDataType[] {
                new IntegerJointDataType(this.count)
        };
    }
}
