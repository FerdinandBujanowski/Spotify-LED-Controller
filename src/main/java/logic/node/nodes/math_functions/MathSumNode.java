package logic.node.nodes.math_functions;

import logic.node.LogicNode;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.*;
import logic.node.joint.own_types.Interval;

public class MathSumNode extends LogicNode {

    public MathSumNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new FunctionInstanceJointDataType(), "Function"),
                        new InputJoint(new IntervalJointDataType(), "Interval")
                },
                new OutputJoint[] {
                        new OutputJoint(new NumberJointDataType(), "Result")
                }
        );
    }

    @Override
    public NumberJointDataType[] function(InputJoint[] inputJoints) {
        LogicNode logicFunction = (LogicNode) inputJoints[0].getJointDataType().getData();
        if(logicFunction == null || logicFunction.getInputJoints().length < 1 || logicFunction.getOutputJoints().length < 1) {
            return new NumberJointDataType[] {new NumberJointDataType(0) };
        }
        Interval interval = (Interval) inputJoints[1].getJointDataType().getData();
        double sum = 0;

        for(int i = interval.getStart(); i < interval.getEnd(); i++) {
            sum += (Double) logicFunction.function(
                    new InputJoint[] {new InputJoint(new NumberJointDataType(i), "") }
            )[0].getData();
        }
        return new NumberJointDataType[] { new NumberJointDataType(sum) };
    }
}
