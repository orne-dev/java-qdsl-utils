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

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "CATEGORIES")
public class JpaItem
extends Item {

    public JpaItem() {
        super();
    }
    public JpaItem(IItem copy) {
        super(copy);
    }
    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false, insertable = false, updatable = false)
    @Override
    public Long getId() {
        return super.getId();
    }
    @Override
    public JpaItem withId(Long id) {
        setId(id);
        return this;
    }
    @Column(name = "CODE", nullable = false)
    @Override
    public String getCode() {
        return super.getCode();
    }
    @Override
    public JpaItem withCode(String code) {
        setCode(code);
        return this;
    }
    @Column(name = "NAME", nullable = false)
    @Override
    public String getName() {
        return super.getName();
    }
    @Override
    public JpaItem withName(String name) {
        setName(name);
        return this;
    }
    @ManyToOne(optional=false)
    @JoinColumn(name="CATEGORY", nullable=false)
    @Override
    public JpaCategory getCategory() {
        return (JpaCategory) super.getCategory();
    }
    @Override
    public void setCategory(ICategory category) {
        if (category == null) {
            super.setCategory(null);
        } else if (category instanceof JpaCategory) {
            super.setCategory(category);
        } else {
            super.setCategory(new JpaCategory(category));
        }
    }
    @Override
    public JpaItem withCategory(ICategory category) {
        setCategory(category);
        return this;
    }
    @ManyToOne(optional=true)
    @JoinColumn(name="TYPE", nullable=true)
    @Override
    public JpaType getType() {
        return (JpaType) super.getType();
    }
    @Override
    public void setType(IType type) {
        if (type == null) {
            super.setType(null);
        } else if (type instanceof JpaType) {
            super.setType(type);
        } else {
            super.setType(new JpaType(type));
        }
    }
    @Override
    public JpaItem withType(IType type) {
        setType(type);
        return this;
    }
    @GeneratedValue
    @Column(name = "CREATION_DATE", nullable = false, insertable = false, updatable = false)
    @Override
    public OffsetDateTime getCreationDate() {
        return super.getCreationDate();
    }
    @Override
    public JpaItem withCreationDate(OffsetDateTime creationDate) {
        setCreationDate(creationDate);
        return this;
    }
    @GeneratedValue
    @Column(name = "MODIFICATION_DATE", nullable = false, insertable = false, updatable = false)
    @Override
    public OffsetDateTime getModificationDate() {
        return super.getModificationDate();
    }
    @Override
    public JpaItem withModificationDate(OffsetDateTime modificationDate) {
        setModificationDate(modificationDate);
        return this;
    }
    @Override
    public JpaItem clone() {
        return new JpaItem(this);
    }
}
