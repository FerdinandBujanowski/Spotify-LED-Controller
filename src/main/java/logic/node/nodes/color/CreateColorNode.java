package logic.node.nodes.color;

import control.type_enums.NodeType;
import logic.node.joint.*;
import logic.node.joint.joint_types.*;
import logic.node.nodes.constant.ConstantColorNode;

import java.awt.*;

public class CreateColorNode extends ConstantColorNode {

    public CreateColorNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new UnitNumberJointDataType(), "R"),
                        new InputJoint(new UnitNumberJointDataType(), "G"),
                        new InputJoint(new UnitNumberJointDataType(), "B"),
                },
                new OutputJoint[] {
                        new OutputJoint(new ColorJointDataType(), "Color")
                },
                NodeType.CREATE_COLOR_NODE
        );
    }

    @Override
    public ColorJointDataType[] function(InputJoint[] inputJoints) {
        int r = (int)Math.round((double)inputJoints[0].getJointDataType().getData() * 255);
        int g = (int)Math.round((double)inputJoints[1].getJointDataType().getData() * 255);
        int b = (int)Math.round((double)inputJoints[2].getJointDataType().getData() * 255);
        return new ColorJointDataType[] { new ColorJointDataType(new Color(r, g, b)) };
    }
}
