package logic.node.nodes.color;

import control.type_enums.NodeType;
import logic.node.LogicNode;
import logic.node.joint.*;
import logic.node.joint.joint_types.*;

import java.awt.*;

public class CreateColorNode extends LogicNode {

    public CreateColorNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new NumberJointDataType(), "R"),
                        new InputJoint(new NumberJointDataType(), "G"),
                        new InputJoint(new NumberJointDataType(), "B"),
                },
                new OutputJoint[] {
                        new OutputJoint(new ColorJointDataType(), "Color")
                },
                NodeType.CREATE_COLOR_NODE
        );
    }

    @Override
    public ColorJointDataType[] function(InputJoint[] inputJoints) {
        int r = (int)Math.round((double)inputJoints[0].getJointDataType().getData());
        int g = (int)Math.round((double)inputJoints[1].getJointDataType().getData());
        int b = (int)Math.round((double)inputJoints[2].getJointDataType().getData());
        return new ColorJointDataType[] { new ColorJointDataType(new Color(r, g, b)) };
    }
}
