package logic.node.nodes.math_functions;

import control.type_enums.NodeType;
import logic.node.LogicNode;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.NumberJointDataType;
import logic.node.joint.joint_types.UnitNumberJointDataType;

public class CosNode extends LogicNode {

    public CosNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new NumberJointDataType(), "Input")
                },
                new OutputJoint[] {
                        new OutputJoint(new NumberJointDataType(), "cos(x)")
                },
                NodeType.COS_NODE
        );
    }

    @Override
    public NumberJointDataType[] function(InputJoint[] inputJoints) {
        double data = (Double) inputJoints[0].getJointDataType().getData();
        return new NumberJointDataType[] { new NumberJointDataType(Math.cos(data)) };
    }
}