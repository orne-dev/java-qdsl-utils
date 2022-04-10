package dev.orne.qdsl.test.model;

/*-
 * #%L
 * Orne Querydsl Utils
 * %%
 * Copyright (C) 2021 - 2022 Orne Developments
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

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
