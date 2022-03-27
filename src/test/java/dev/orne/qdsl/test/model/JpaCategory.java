package dev.orne.qdsl.test.model;

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
