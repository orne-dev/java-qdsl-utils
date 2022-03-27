package dev.orne.qdsl.test.model;

import java.time.OffsetDateTime;

import com.querydsl.core.annotations.Config;
import com.querydsl.core.annotations.QueryEntity;

@QueryEntity
@Config(defaultVariableName=IItem.DEFAULT_ALIAS)
public interface IItem {

    public static final String DEFAULT_ALIAS = "items";

    Long getId();

    String getCode();

    String getName();

    ICategory getCategory();

    IType getType();

    OffsetDateTime getCreationDate();

    OffsetDateTime getModificationDate();

    IItem clone();
}
