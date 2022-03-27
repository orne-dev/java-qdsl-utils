package dev.orne.qdsl.test.model;

public enum TypeEnum
implements IType {
    TYPE1("L1", null, "TYPE 1"),
    TYPE2("L2", null, "TYPE 2"),
    TYPE3("L3", null, "TYPE 3"),
    TYPE4("L4", null, "TYPE 4"),
    TYPE1_1("L11", TYPE1, "TYPE 1.1"),
    TYPE1_2("L12", TYPE1, "TYPE 1.2"),
    TYPE1_3("L13", TYPE1, "TYPE 1.3"),
    TYPE3_1("L31", TYPE3, "TYPE 3.1"),
    TYPE4_1("L41", TYPE4, "TYPE 4.1"),
    TYPE4_2("L42", TYPE4, "TYPE 4.2"),
    TYPE4_3("L43", TYPE4, "TYPE 4.3"),
    TYPE4_4("L44", TYPE4, "TYPE 4.4"),
    ;

    public final String code;
    public final TypeEnum parent;
    public final String name;
    private TypeEnum(String code, TypeEnum parent, String name) {
        this.code = code;
        this.parent = parent;
        this.name = name;
    }
    @Override
    public String getCode() {
        return code;
    }
    @Override
    public TypeEnum getParent() {
        return parent;
    }
    @Override
    public String getName() {
        return name;
    }
    public static TypeEnum byCode(String code) {
        for (TypeEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
