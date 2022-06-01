package logic.node.nodes.plane;

import control.type_enums.MixingAlgorithmType;
import control.type_enums.NodeType;
import logic.led.LogicPlane;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.PlaneJointDataType;
import logic.node.nodes.mask.MultiplyMaskWithUnitNode;

public class OverlayPlaneNode extends SimplePlaneNode {

    private final  MixingAlgorithmType mixingAlgorithmType;

    public OverlayPlaneNode(int nodeIndex, MixingAlgorithmType mixingAlgorithmType) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new PlaneJointDataType(), "Over"),
                        new InputJoint(new PlaneJointDataType(), "Under")
                },
                new OutputJoint[] {
                        new OutputJoint(
                                new PlaneJointDataType(),
                                "Output [" + mixingAlgorithmType.getName().substring(0, 3).toUpperCase() + "]"
                        )
                },
                NodeType.OVERLAY_PLANE_NODE,
                new Object[] { mixingAlgorithmType }
        );
        this.mixingAlgorithmType = mixingAlgorithmType;
    }

    @Override
    public PlaneJointDataType[] function(InputJoint[] inputJoints) {
        LogicPlane planeOver = (LogicPlane) inputJoints[0].getJointDataType().getData();
        LogicPlane planeUnder = (LogicPlane) inputJoints[1].getJointDataType().getData();

        switch(this.mixingAlgorithmType) {
            case OVERLAY -> {
                return new PlaneJointDataType[] {
                        new PlaneJointDataType(LogicPlane.getPlaneOverlay(planeOver, planeUnder))
                };
            }
            case ADDITIVE -> {
                return new PlaneJointDataType[] {
                        new PlaneJointDataType(LogicPlane.getPlaneAdditive(planeOver, planeUnder))
                };
            }
            default -> {
                return new PlaneJointDataType[] { new PlaneJointDataType() };
            }
        }
    }
}
