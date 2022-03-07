package logic.node.nodes.interval;

import control.type_enums.NodeType;
import logic.node.LogicNode;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.IntegerJointDataType;
import logic.node.joint.joint_types.IntervalJointDataType;
import logic.node.joint.own_types.Interval;

public class CreateIntervalNode extends LogicNode {

    public CreateIntervalNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new IntegerJointDataType(), "Start (incl.)"),
                        new InputJoint(new IntegerJointDataType(), "End (excl.)")
                },
                new OutputJoint[] {
                        new OutputJoint(new IntervalJointDataType(), "Interval")
                },
                NodeType.CREATE_INTERVAL_NODE
        );
    }

    @Override
    public IntervalJointDataType[] function(InputJoint[] inputJoints) {
        Interval data = new Interval(
                (Integer) inputJoints[0].getJointDataType().getData(),
                (Integer) inputJoints[1].getJointDataType().getData()
                );
        return new IntervalJointDataType[] { new IntervalJointDataType(data) };
    }
}
