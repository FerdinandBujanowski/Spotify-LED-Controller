package logic.node.nodes.compare;

import control.type_enums.NodeType;
import logic.node.LogicNode;
import logic.node.joint.*;
import logic.node.joint.joint_types.*;

public class LessNode extends LogicNode {

    public LessNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new NumberJointDataType(), "Number A"),
                        new InputJoint(new NumberJointDataType(), "Number B")
                },
                new OutputJoint[] {
                        new OutputJoint(new BooleanJointDataType(), "<")
                },
                NodeType.LESS_NODE
        );
    }

    @Override
    public BooleanJointDataType[] function(InputJoint[] inputJoints) {
        double a = (Double) inputJoints[0].getJointDataType().getData();
        double b = (Double) inputJoints[1].getJointDataType().getData();
        return new BooleanJointDataType[] { new BooleanJointDataType(a < b) };
    }
}