# :package: 0.1.0

01. :wrench: Added `org.apache.commons:commons-lang3:3.12.0` dependency.
01. :wrench: Added `com.querydsl:querydsl-core:5.0.0` dependency.
01. :wrench: Added `commons-beanutils:commons-beanutils:1.9.4` optional dependency.
01. :gift: Added utility classes.
    01. Added `dev.orne.qdsl.ChainedReplaceVisitor` class.
    01. Added `dev.orne.qdsl.OrderSpecifierVisitor` interface.
    01. Added `dev.orne.qdsl.OrderSpecifierReplaceVisitor` interface.
    01. Added `dev.orne.qdsl.QBeanBuilder` class.
01. :gift: Added wrapped clauses system.
    01. Added public API.
        01. Added `dev.orne.qdsl.wrap.ProjectionType` interface.
            01. Added `dev.orne.qdsl.wrap.ProjectionType.Basic` enumeration.
        01. Added `dev.orne.qdsl.wrap.ExtendedQueryClause` interface.
        01. Added `dev.orne.qdsl.wrap.ExtendedGroupableQueryClause` interface.
        01. Added `dev.orne.qdsl.wrap.StoredValue` class.
        01. Added `dev.orne.qdsl.wrap.StoredValueVisitor` interface.
        01. Added `dev.orne.qdsl.wrap.StoredValueReplaceVisitor` interface.
        01. Added `dev.orne.qdsl.wrap.StoredValues` class.
        01. Added `dev.orne.qdsl.wrap.StoredValuesVisitor` interface.
        01. Added `dev.orne.qdsl.wrap.StoredValuesReplaceVisitor` interface.
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
        01. Added `dev.orne.qdsl.wrap.WrappedClauseFactory` class.
    01. Added wrapped clauses default implementations.
        01. Added `dev.orne.qdsl.wrap.impl.ExpressionTransformationException` exception.
        01. Added `dev.orne.qdsl.wrap.impl.ExpressionTransformer` interface.
        01. Added `dev.orne.qdsl.wrap.impl.NopExpressionTransformer` class.
        01. Added `dev.orne.qdsl.wrap.impl.PreFetchedSubQueryExpression` class.
        01. Added `dev.orne.qdsl.wrap.impl.PreFetchedSubQueryExtractor` class.
        01. Added `dev.orne.qdsl.wrap.impl.WrappedClause` class.
        01. Added `dev.orne.qdsl.wrap.impl.WrappedQueryClause` class.
        01. Added `dev.orne.qdsl.wrap.impl.WrappedCollQueryClause` class.
        01. Added `dev.orne.qdsl.wrap.impl.WrappedStoreClause` class.
        01. Added `dev.orne.qdsl.wrap.impl.WrappedInsertClause` class.
        01. Added `dev.orne.qdsl.wrap.impl.WrappedUpdateClause` class.
        01. Added `dev.orne.qdsl.wrap.impl.WrappedDeleteClause` class.
    01. :gift: Added wrapped clause expression transformation system.
        01. Added `dev.orne.qdsl.wrap.impl.transform.BaseExpressionTransformer` class.
        01. Added `dev.orne.qdsl.wrap.impl.transform.ChainedExpressionTransformer` class.
        01. Added `dev.orne.qdsl.wrap.impl.transform.ValueTransformer` interface.
        01. Added `dev.orne.qdsl.wrap.impl.transform.SimpleExpressionTransformer` interface.
        01. Added `dev.orne.qdsl.wrap.impl.transform.StoredValuesTransformer` interface.
        01. Added `dev.orne.qdsl.wrap.impl.transform.SimplePathTransformer` class.
        01. Added `dev.orne.qdsl.wrap.impl.transform.ExpressionTransformers` interface.
