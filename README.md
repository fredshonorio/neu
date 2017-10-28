# neu
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Download](https://api.bintray.com/packages/fredhonorio-com/maven/neu/images/download.svg)](https://bintray.com/fredhonorio-com/maven/neu/_latestVersion)
[![Travis](https://travis-ci.org/fredshonorio/neu.svg?branch=master)](https://travis-ci.org/fredshonorio/neu)
[![Ccodecov](https://codecov.io/gh/fredshonorio/neu/branch/master/graph/badge.svg)](https://codecov.io/gh/fredshonorio/neu)

Well-typed wrapper over the official [neo4j java driver](https://github.com/neo4j/neo4j-java-driver).


# Features

__Type-safe query parameters__

We provide wrappers over the cypher type-system, and enforce these types throughout the library. In the official driver
the user can attempt to build user parameters that will fail at runtime (e.g.: `Values.value(new Object())` which has
no sensible corresponding value in Cypher). We provide a class hierarchy for every value that crosses the Java <-> Cypher
border as well as utility methods to create values for these classes with minimal (but not zero) boilerplate.

__Query builder with inline parameters__

Writing queries involves a large amount of stringly-typed programming which can make maintaining a schema difficult.
If we decide that the `Profile` label should instead be `User`, we'll need to find and replace every occurrence. Even
worse are property names, which can be shared between different entities and may be hard to distinguish.
We provide a query builder that accepts schema-related elements (node labels, relationship types, etc.) as values. We now have
all the tools that we'd have for any other value. We can quite easily see every time a given label is referenced by using
an IDE, for example.
Besides the schema, the library allows parameter definition to be done inline, and does the work of naming parameters
and creating a parameterized statement.

__Statement result decoders__

Handling the result of a query can also become cumbersome because it involves type checks and casts
to prove that the data has the right shape. `ResultDecoder`s are functions that verify the
shape of a result coming from the driver and transform it into a useful value.
Since `ResultDecoder`s are Java values, they can be composed with other `ResultDecoders`,
through the use of many combinators.

__Composable defered operations__

A popular idea in functional programming is to separate the description of a
computation from its execution. This means that we can execute the same computation
in different contexts, and it's possible to compose multiple computations (because they
have not happened yet). In our case we distinguish graph operations from
their execution by describing operations in a `GraphDB` type. A `GraphDB<T>` is
a value, that once interpreted returns a `T`, or fails. Interpreting simply means supplying a
`GraphDB<T>` with a `StatementRunner`, therefore an interpreter is just a function that can
get a session or transaction from the driver. The library provides interpreters for read-only and write operations, in a
session or a transaction.
