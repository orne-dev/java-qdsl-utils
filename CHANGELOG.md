# :package: 0.1.0

01. :wrench: Added `org.apache.commons:commons-lang3:3.12.0` dependency.
01. :wrench: Added `com.querydsl:querydsl-core:5.0.0` dependency.
01. :wrench: Added `commons-beanutils:commons-beanutils:1.9.4` optional dependency.
01. :gift: Added Querydsl utility classes.
    01. Added `dev.orne.qdsl.ChainedReplaceVisitor` class.
    01. Added `dev.orne.qdsl.OrderSpecifierVisitor` interface.
    01. Added `dev.orne.qdsl.OrderSpecifierReplaceVisitor` interface.
    01. Added `dev.orne.qdsl.QBeanBuilder` class.
01. :gift: Added Querydsl wrapped clauses system.
    01. Added `dev.orne.qdsl.wrap.ProjectionType` interface.
      01. Added `dev.orne.qdsl.wrap.ProjectionType.Basic` enumeration.
    01. Added `dev.orne.qdsl.wrap.ExtendedQueryClause` interface.
    01. Added `dev.orne.qdsl.wrap.ExtendedGroupableQueryClause` interface.
    01. Added `dev.orne.qdsl.wrap.ExtendedStoreClause` interface.
    01. Added `dev.orne.qdsl.wrap.ExtendedInsertClause` interface.
    01. Added `dev.orne.qdsl.wrap.ExtendedUpdateClause` interface.
    01. Added `dev.orne.qdsl.wrap.EntityPathNotSupportedException` exception.
    01. Added `dev.orne.qdsl.wrap.WrappedClauseProvider` interface.
    01. Added `dev.orne.qdsl.wrap.WrappedQueryClauseProvider` interface.
    01. Added `dev.orne.qdsl.wrap.WrappedInsertClauseProvider` interface.
    01. Added `dev.orne.qdsl.wrap.WrappedUpdateClauseProvider` interface.
    01. Added `dev.orne.qdsl.wrap.WrappedDeleteClauseProvider` interface.
    01. Added `dev.orne.qdsl.wrap.WrappedClauseProviderRegistry` interface.
    01. Added `dev.orne.qdsl.wrap.ClauseProviderNotFoundException` exception.
    01. Added `dev.orne.qdsl.wrap.ClauseFactory` class.
01. :gift: Added Querydsl expression translation system.
    01. Added `dev.orne.qdsl.ValueTranslator` interface.
    01. Added `dev.orne.qdsl.ExpressionTranslator` interface.
    01. Added `dev.orne.qdsl.ValueAssigment` class.
    01. Added `dev.orne.qdsl.ValueAssigments` class.
    01. Added `dev.orne.qdsl.ValueAssigmentTranslator` interface.
    01. Added `dev.orne.qdsl.ValueAssignmentVisitor` interface.
    01. Added `dev.orne.qdsl.ValueAssignmentReplaceVisitor` interface.
    01. Added `dev.orne.qdsl.TranslateVisitor` class.
    01. Added `dev.orne.qdsl.SimplePathTranslator` class.
    01. Added `dev.orne.qdsl.QueryTranslationException` exception.
    01. Added `dev.orne.qdsl.Translator` interface.
    01. Added `dev.orne.qdsl.NopTranslator` class.
    01. Added `dev.orne.qdsl.ChainedTranslator` class.
