package dev.orne.qdsl.test.model;

import java.time.OffsetDateTime;

import com.querydsl.core.annotations.Config;
import com.querydsl.core.annotations.QueryEntity;

@QueryEntity
@Config(defaultVariableName=ICategory.DEFAULT_ALIAS)
public interface ICategory {

    public static final String DEFAULT_ALIAS = "categories";

    Long getId();

    String getName();

    OffsetDateTime getCreationDate();

    OffsetDateTime getModificationDate();

    ICategory clone();
}
