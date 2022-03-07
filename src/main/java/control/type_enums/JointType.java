package control.type_enums;

import logic.node.joint.joint_types.*;

import java.awt.*;

public enum JointType {

    /*
    COLOR PALETTE:
    0, 56, 68,
    0, 82, 86,
    0, 108, 103,
    121, 128, 142,
    241, 148, 180,
    248, 163, 90,
    255, 177, 0,
    255, 206, 99,
    255, 235, 198
     */

    NUMBER_TYPE(new Color(0, 56, 68), NumberJointDataType.class, "Number"),
    INTEGER_TYPE(new Color(0, 82, 86), IntegerJointDataType.class, "Integer"),
    UNIT_NUMBER_TYPE(new Color(0, 108, 103), UnitNumberJointDataType.class, "Unit Number"),
    INTERVAL_TYPE(new Color(121, 128, 142), IntervalJointDataType.class, "Interval"),
    BOOLEAN_TYPE(new Color(241, 148, 180), BooleanJointDataType.class, "Boolean"),
    COLOR_TYPE(new Color(248, 163, 90), ColorJointDataType.class, "Color"),
    MASK_TYPE(new Color(255, 255, 255), MaskJointDataType.class, "Mask");

    private Color color;
    private Class typeClass;
    String name;

    JointType(Color color, Class typeClass, String name) {
        this.color = color;
        this.typeClass = typeClass;
        this.name = name;
    }

    public Color getColor() {
        return this.color;
    }

    public Class getTypeClass() {
        return this.typeClass;
    }

    public static JointType getJointTypeByTypeClass(Class typeClass) {
        for(JointType jointType : JointType.values()) {
            if(jointType.getTypeClass() == typeClass) {
                return jointType;
            }
        }
        return null;
    }

    public static JointDataType getCopyOfDataTypeByJointType(JointType jointType) {
        JointDataType jointDataType = null;

        try {
            jointDataType = (JointDataType) jointType.getTypeClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return jointDataType;
    }

    public String getName() {
        return this.name;
    }
}
