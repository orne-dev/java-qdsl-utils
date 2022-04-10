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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "TYPES")
public class JpaType
extends Type {

    public JpaType() {
        super();
    }
    public JpaType(IType copy) {
        super(copy);
    }
    @Column(name = "CODE", nullable = false)
    @Override
    public String getCode() {
        return super.getCode();
    }
    @Override
    public JpaType withCode(String code) {
        setCode(code);
        return this;
    }
    @Column(name = "NAME", nullable = false)
    @Override
    public String getName() {
        return super.getName();
    }
    @Override
    public JpaType withName(String name) {
        setName(name);
        return this;
    }
    @ManyToOne(optional=true)
    @JoinColumn(name="PARENT", nullable=true)
    @Override
    public JpaType getParent() {
        return (JpaType) super.getParent();
    }
    @Override
    public void setParent(IType parent) {
        if (parent == null) {
            super.setParent(null);
        } else if (parent instanceof JpaType) {
            super.setParent(parent);
        } else {
            super.setParent(new JpaType(parent));
        }
    }
    @Override
    public JpaType withParent(IType parent) {
        setParent(parent);
        return this;
    }
    @Override
    public JpaType clone() {
        return new JpaType(this);
    }
}
