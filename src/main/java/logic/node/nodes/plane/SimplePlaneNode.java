package logic.node.nodes.plane;

import control.type_enums.NodeType;
import logic.led.LogicMask;
import logic.led.LogicPlane;
import logic.node.LogicNode;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.ColorJointDataType;
import logic.node.joint.joint_types.JointDataType;
import logic.node.joint.joint_types.MaskJointDataType;
import logic.node.joint.joint_types.PlaneJointDataType;

import java.awt.*;

public class SimplePlaneNode extends LogicNode {

    public SimplePlaneNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new MaskJointDataType(), "Mask"),
                        new InputJoint(new ColorJointDataType(), "Color")
                },
                new OutputJoint[] {
                        new OutputJoint(new PlaneJointDataType(), "Output")
                },
                NodeType.SIMPLE_PLANE_NODE
        );
    }

    @Override
    public JointDataType[] function(InputJoint[] inputJoints) {
        LogicMask mask = (LogicMask) inputJoints[0].getJointDataType().getData();
        Color color = (Color) inputJoints[1].getJointDataType().getData();
        return new JointDataType[] {
                new PlaneJointDataType(LogicPlane.multiplyMaskWithColor(mask, color))
        };
    }

    @Override
    public Color[][] getPlaneColorValues(Integer nullInteger) {
        LogicPlane logicPlane = (LogicPlane) this.getOutputJoints()[0].getJointDataType().getData();
        return logicPlane.getColorValues();
    }
}
