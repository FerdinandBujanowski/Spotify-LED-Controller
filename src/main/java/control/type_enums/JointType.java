package control.type_enums;

import logic.node.joint.joint_types.*;

import java.awt.*;

public enum JointType {

    BOOLEAN_TYPE(new Color(189, 62, 66), BooleanJointDataType.class),
    COLOR_TYPE(new Color(189, 62, 168), ColorJointDataType.class),
    FUNCTION_INSTANCE_TYPE(new Color(100, 100, 100), FunctionInstanceJointDataType.class),
    INTEGER_TYPE(new Color(16, 8, 77), IntegerJointDataType.class),
    INTERVAL_TYPE(new Color(37, 110, 35, 255), IntervalJointDataType.class),
    NUMBER_TYPE(new Color(62, 74, 189), NumberJointDataType.class),
    UNIT_NUMBER_TYPE(new Color(62, 189, 189), UnitNumberJointDataType.class);

    private Color color;
    private Class typeClass;
    JointType(Color color, Class typeClass) {
        this.color = color;
        this.typeClass = typeClass;
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
}
