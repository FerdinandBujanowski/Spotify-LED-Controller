package control.type_enums;

import logic.node.joint.joint_types.*;

import java.awt.*;

public enum JointType {

    INTEGER_TYPE(new Color(0, 0, 45), IntegerJointDataType.class, "Integer"),
    NUMBER_TYPE(new Color(0, 0, 128), NumberJointDataType.class, "Number"),
    UNIT_NUMBER_TYPE(new Color(0, 255, 255), UnitNumberJointDataType.class, "Unit Number"),
    INTERVAL_TYPE(new Color(35, 101, 51), IntervalJointDataType.class, "Interval"),
    BOOLEAN_TYPE(new Color(227, 34, 39), BooleanJointDataType.class, "Boolean"),
    COLOR_TYPE(new Color(159, 0, 255), ColorJointDataType.class, "Color"),
    TEXTURE_TYPE(new Color(255, 160, 14), TextureJointDataType.class, "Texture"),
    MASK_TYPE(new Color(255, 215, 0), MaskJointDataType.class, "Mask");

    private final Color color;
    private final Class typeClass;
    private final String name;

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
    public String getName() {
        return this.name;
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

    public static String[] getNames() {
        String[] names = new String[JointType.values().length];
        for(int i = 0; i < names.length; i++) {
            names[i] = JointType.values()[i].getName();
        }
        return names;
    }

    public static JointType getTypeByString(String string) {
        for(JointType jointType : JointType.values()) {
            if(jointType.toString().equals(string)) {
                return jointType;
            }
        }
        return null;
    }
}
