package logic.node.nodes.color;

import control.type_enums.JointType;
import logic.node.LogicNode;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.ColorJointDataType;

import java.awt.*;

public class ConstantColorNode extends LogicNode {

    public Color color;

    public ConstantColorNode(int nodeIndex, Color color) {
        super(
                nodeIndex,
                new InputJoint[0],
                new OutputJoint[] {
                        new OutputJoint(
                                new ColorJointDataType(),
                                "[" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ", " + color.getAlpha() + "]"
                        )
                }
        );
        this.color = color;
    }

    @Override
    public ColorJointDataType[] function(InputJoint[] inputJoints) {
        return new ColorJointDataType[] { new ColorJointDataType(this.color) };
    }
}
