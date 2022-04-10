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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Item
implements IItem {

    private Long id;
    private String code;
    private String name;
    private ICategory category;
    private IType type;
    private OffsetDateTime creationDate;
    private OffsetDateTime modificationDate;

    public Item() {
        super();
    }
    public Item(IItem copy) {
        super();
        this.id = copy.getId();
        this.code = copy.getCode();
        this.name = copy.getName();
        this.category = copy.getCategory();
        this.type = copy.getType();
        this.creationDate = copy.getCreationDate();
        this.modificationDate = copy.getModificationDate();
    }
    @Override
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Item withId(Long id) {
        setId(id);
        return this;
    }
    @Override
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public Item withCode(String code) {
        setCode(code);
        return this;
    }
    @Override
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Item withName(String name) {
        setName(name);
        return this;
    }
    @Override
    public ICategory getCategory() {
        return category;
    }
    public void setCategory(ICategory category) {
        this.category = category;
    }
    public Item withCategory(ICategory category) {
        setCategory(category);
        return this;
    }
    @Override
    public IType getType() {
        return type;
    }
    public void setType(IType type) {
        this.type = type;
    }
    public Item withType(IType type) {
        setType(type);
        return this;
    }
    @Override
    public OffsetDateTime getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(OffsetDateTime creationDate) {
        this.creationDate = creationDate;
    }
    public Item withCreationDate(OffsetDateTime creationDate) {
        setCreationDate(creationDate);
        return this;
    }
    @Override
    public OffsetDateTime getModificationDate() {
        return modificationDate;
    }
    public void setModificationDate(OffsetDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }
    public Item withModificationDate(OffsetDateTime modificationDate) {
        setModificationDate(modificationDate);
        return this;
    }
    @Override
    public Item clone() {
        return new Item(this);
    }
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
