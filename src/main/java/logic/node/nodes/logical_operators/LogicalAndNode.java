package logic.node.nodes.logical_operators;

import logic.node.LogicNode;
import logic.node.joint.*;
import logic.node.joint.joint_types.*;

public class LogicalAndNode extends LogicNode {

    public LogicalAndNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[]{
                        new InputJoint(new BooleanJointDataType(), "Boolean A"),
                        new InputJoint(new BooleanJointDataType(), "Boolean B")
                },
                new OutputJoint[]{
                        new OutputJoint(new BooleanJointDataType(), "&&")
                }
        );
    }

    @Override
    public BooleanJointDataType[] function(InputJoint[] inputJoints) {
        boolean a = (Boolean) inputJoints[0].getJointDataType().getData();
        boolean b = (Boolean) inputJoints[1].getJointDataType().getData();
        return new BooleanJointDataType[] { new BooleanJointDataType(a && b) };
    }
}
