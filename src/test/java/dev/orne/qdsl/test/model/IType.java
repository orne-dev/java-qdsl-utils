package dev.orne.qdsl.test.model;

import com.querydsl.core.annotations.Config;
import com.querydsl.core.annotations.QueryEntity;

@QueryEntity
@Config(defaultVariableName=IType.DEFAULT_ALIAS)
public interface IType {

    public static final String DEFAULT_ALIAS = "types";

    String getCode();

    IType getParent();

    String getName();
}
