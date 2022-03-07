package logic.node.nodes.math_functions;

import control.type_enums.NodeType;
import logic.node.LogicNode;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.NumberJointDataType;
import logic.node.joint.joint_types.UnitNumberJointDataType;

public class SinNode extends LogicNode {

    public SinNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new NumberJointDataType(), "Input")
                },
                new OutputJoint[] {
                        new OutputJoint(new NumberJointDataType(), "sin(x)")
                },
                NodeType.SIN_NODE
        );
    }

    @Override
    public NumberJointDataType[] function(InputJoint[] inputJoints) {
        double data = (Double) inputJoints[0].getJointDataType().getData();
        return new NumberJointDataType[] { new NumberJointDataType(Math.sin(data)) };
    }
}
