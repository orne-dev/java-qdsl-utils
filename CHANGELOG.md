# :package: 0.1.0

01. :gift: Initial version
01. :wrench: Added `org.apache.commons:commons-lang3:3.12.0` dependency.
01. :wrench: Added `com.querydsl:querydsl-core:5.0.0` dependency.
01. :wrench: Added `commons-beanutils:commons-beanutils:1.9.4` optional dependency.
01. :gift: Added Querydsl expression translation system.
    01. Added `dev.orne.qdsl.ValueTranslator` interface.
    01. Added `dev.orne.qdsl.ExpressionTranslator` interface.
    01. Added `dev.orne.qdsl.ValueAssigment` class.
    01. Added `dev.orne.qdsl.AssigmentTranslator` interface.
    01. Added `dev.orne.qdsl.TranslateVisitor` class.
    01. Added `dev.orne.qdsl.PropertyPathTranslator` class.
    01. Added `dev.orne.qdsl.DelegatedTranslateVisitor` class.
    01. Added `dev.orne.qdsl.Translator` class.
