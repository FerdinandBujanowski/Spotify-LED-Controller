package logic.node.nodes.math_functions;

import control.type_enums.NodeType;
import logic.node.LogicNode;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.NumberJointDataType;

public class PiNode extends LogicNode {

    public PiNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {},
                new OutputJoint[] {
                        new OutputJoint(new NumberJointDataType(), "3.14..")
                },
                NodeType.PI_NODE
        );
    }

    @Override
    public NumberJointDataType[] function(InputJoint[] inputJoints) {
        return new NumberJointDataType[] {new NumberJointDataType(Math.PI) };
    }
}
