package logic.node.nodes.math_functions;

import logic.node.LogicNode;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.IntegerJointDataType;
import logic.node.joint.joint_types.NumberJointDataType;

public class PowerNode extends LogicNode {

    public PowerNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new NumberJointDataType(), "Number"),
                        new InputJoint(new IntegerJointDataType(), "Power")
                },
                new OutputJoint[] {
                        new OutputJoint(new NumberJointDataType(), "Output")
                }
        );
    }

    @Override
    public NumberJointDataType[] function(InputJoint[] inputJoints) {
        double data = (Double) inputJoints[0].getJointDataType().getData();
        int power = (Integer) inputJoints[1].getJointDataType().getData();

        return new NumberJointDataType[] {
                new NumberJointDataType(Math.pow(data, power))
        };
    }
}
