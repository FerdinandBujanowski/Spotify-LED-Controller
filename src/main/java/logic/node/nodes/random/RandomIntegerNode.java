package logic.node.nodes.random;

import control.type_enums.JointType;
import control.type_enums.NodeType;
import logic.node.LogicNode;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.IntegerJointDataType;
import logic.node.joint.joint_types.IntervalJointDataType;
import logic.node.joint.joint_types.JointDataType;
import logic.node.joint.own_types.Interval;

import java.util.Random;

public class RandomIntegerNode extends LogicNode {

    public RandomIntegerNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new IntervalJointDataType(), "Interval")
                },
                new OutputJoint[] {
                        new OutputJoint(new IntegerJointDataType(), "Output")
                },
                NodeType.RANDOM_INTEGER_NODE
        );
    }

    @Override
    public JointDataType[] function(InputJoint[] inputJoints) {
        Random random = new Random();
        Interval interval = (Interval) inputJoints[0].getJointDataType().getData();
        int end = interval.getEnd(), start = interval.getStart();
        if(end < start) end = start;
        if(end < 0) end = 0;
        if(start < 0) start = 0;
        return new JointDataType[] {
                new IntegerJointDataType(random.nextInt(end + 1 - start) + start)
        };
    }
}
