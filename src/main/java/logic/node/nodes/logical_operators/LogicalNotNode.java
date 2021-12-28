package logic.node.nodes.logical_operators;

import logic.node.LogicNode;
import logic.node.joint.*;
import logic.node.joint.joint_types.*;

public class LogicalNotNode extends LogicNode {

    public LogicalNotNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[]{
                        new InputJoint(new BooleanJointDataType(), "Input Boolean"),
                },
                new OutputJoint[]{
                    new OutputJoint(new BooleanJointDataType(), "!")
                }
        );
    }

    @Override
    public BooleanJointDataType[] function(InputJoint[] inputJoints) {
        boolean a = (Boolean) inputJoints[0].getJointDataType().getData();
        return new BooleanJointDataType[] { new BooleanJointDataType(!a) };
    }
}
