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
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "CATEGORIES")
public class JpaCategory
extends Category {

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
    private Set<JpaItem> items;

    public JpaCategory() {
        super();
    }
    public JpaCategory(ICategory copy) {
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
    public JpaCategory withId(Long id) {
        setId(id);
        return this;
    }
    @Column(name = "NAME", nullable = false)
    @Override
    public String getName() {
        return super.getName();
    }
    @Override
    public JpaCategory withName(String name) {
        setName(name);
        return this;
    }
    @GeneratedValue
    @Column(name = "CREATION_DATE", nullable = false, insertable = false, updatable = false)
    @Override
    public OffsetDateTime getCreationDate() {
        return super.getCreationDate();
    }
    @Override
    public JpaCategory withCreationDate(OffsetDateTime creationDate) {
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
    public JpaCategory withModificationDate(OffsetDateTime modificationDate) {
        setModificationDate(modificationDate);
        return this;
    }
    @Override
    public JpaCategory clone() {
        return new JpaCategory(this);
    }
}
