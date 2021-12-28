package logic.node.nodes.math_functions;

import logic.node.LogicNode;
import logic.node.joint.*;
import logic.node.joint.joint_types.*;

public class LerpNode extends LogicNode {

    public LerpNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new NumberJointDataType(), "Number A"),
                        new InputJoint(new NumberJointDataType(), "Number B"),
                        new InputJoint(new UnitNumberJointDataType(), "Percentage")
                },
                new OutputJoint[] {
                        new OutputJoint(new NumberJointDataType(), "Output")
                }
        );
    }

    @Override
    public NumberJointDataType[] function(InputJoint[] inputJoints) {
        double a = (Double) inputJoints[0].getJointDataType().getData();
        double b = (Double) inputJoints[1].getJointDataType().getData();
        double percentage = (Double) inputJoints[2].getJointDataType().getData();

        return new NumberJointDataType[] { new NumberJointDataType(a * percentage + b * (1 - percentage)) };
    }
}
