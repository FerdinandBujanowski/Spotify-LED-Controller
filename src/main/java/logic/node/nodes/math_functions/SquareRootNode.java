package logic.node.nodes.math_functions;

import control.type_enums.NodeType;
import logic.node.LogicNode;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.IntegerJointDataType;
import logic.node.joint.joint_types.NumberJointDataType;

public class SquareRootNode extends LogicNode {

    public SquareRootNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[]{
                        new InputJoint(new NumberJointDataType(), "Number"),
                },
                new OutputJoint[]{
                        new OutputJoint(new NumberJointDataType(), "Output")
                },
                NodeType.SQRT_NODE
        );
    }

    @Override
    public NumberJointDataType[] function(InputJoint[] inputJoints) {
        double data = (Double) inputJoints[0].getJointDataType().getData();

        return new NumberJointDataType[]{
                new NumberJointDataType(Math.sqrt(data))
        };
    }
}
