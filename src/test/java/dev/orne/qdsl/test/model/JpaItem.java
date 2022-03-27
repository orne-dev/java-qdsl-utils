package dev.orne.qdsl.test.model;

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
