package dev.orne.qdsl.test.model;

import java.time.OffsetDateTime;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Category
implements ICategory {

    private Long id;
    private String name;
    private OffsetDateTime creationDate;
    private OffsetDateTime modificationDate;

    public Category() {
        super();
    }
    public Category(ICategory copy) {
        super();
        this.id = copy.getId();
        this.name = copy.getName();
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
    public Category withId(Long id) {
        setId(id);
        return this;
    }
    @Override
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Category withName(String name) {
        setName(name);
        return this;
    }
    @Override
    public OffsetDateTime getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(OffsetDateTime creationDate) {
        this.creationDate = creationDate;
    }
    public Category withCreationDate(OffsetDateTime creationDate) {
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
    public Category withModificationDate(OffsetDateTime modificationDate) {
        setModificationDate(modificationDate);
        return this;
    }
    @Override
    public Category clone() {
        return new Category(this);
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
