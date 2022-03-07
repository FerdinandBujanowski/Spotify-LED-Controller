package logic.node.nodes.color;

import control.type_enums.NodeType;
import logic.node.LogicNode;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.ColorJointDataType;
import logic.node.joint.joint_types.NumberJointDataType;

import java.awt.*;

public class SplitColorNode extends LogicNode {

    public SplitColorNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new ColorJointDataType(), "Color")
                },
                new OutputJoint[] {
                        new OutputJoint(new NumberJointDataType(), "R"),
                        new OutputJoint(new NumberJointDataType(), "G"),
                        new OutputJoint(new NumberJointDataType(), "B"),
                },
                NodeType.SPLIT_COLOR_NODE
        );
    }

    @Override
    public NumberJointDataType[] function(InputJoint[] inputJoints) {
        Color inputColor = (Color) inputJoints[0].getJointDataType().getData();
        double r = inputColor.getRed();
        double g = inputColor.getGreen();
        double b = inputColor.getBlue();
        return new NumberJointDataType[] {
                new NumberJointDataType(r),
                new NumberJointDataType(g),
                new NumberJointDataType(b),
        };
    }
}
