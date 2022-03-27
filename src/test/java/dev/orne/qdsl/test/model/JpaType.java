package dev.orne.qdsl.test.model;

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
