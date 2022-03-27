package dev.orne.qdsl.test.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Type
implements IType {

    private String code;
    private IType parent;
    private String name;

    public Type() {
        super();
    }
    public Type(IType copy) {
        super();
        this.code = copy.getCode();
        this.parent = copy.getParent();
        this.name = copy.getName();
    }
    @Override
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public Type withCode(String code) {
        setCode(code);
        return this;
    }
    @Override
    public IType getParent() {
        return parent;
    }
    public void setParent(IType parent) {
        this.parent = parent;
    }
    public Type withParent(IType parent) {
        setParent(parent);
        return this;
    }
    @Override
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Type withName(String name) {
        setName(name);
        return this;
    }
    @Override
    public Type clone() {
        return new Type(this);
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
