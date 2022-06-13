package logic.node.nodes.math_functions;

import control.type_enums.NodeType;
import logic.node.LogicNode;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.IntegerJointDataType;
import logic.node.joint.joint_types.JointDataType;

public class ModuloNode extends LogicNode {

    public ModuloNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new IntegerJointDataType(), "Number"),
                        new InputJoint(new IntegerJointDataType(), "Divisor")
                },
                new OutputJoint[] {
                        new OutputJoint(new IntegerJointDataType(), "Output")
                },
                NodeType.MODULO_NODE
        );
    }

    @Override
    public JointDataType[] function(InputJoint[] inputJoints) {
        int data = (Integer) inputJoints[0].getJointDataType().getData();
        int divisor = (Integer) inputJoints[1].getJointDataType().getData();

        int output = divisor == 0 ? data : data % divisor;
        return new IntegerJointDataType[] {
                new IntegerJointDataType(output)
        };
    }
}
