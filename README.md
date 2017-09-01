# neu

Well-typed wrapper over the official [neo4j java driver](https://github.com/neo4j/neo4j-java-driver).

## (Desired) Features

- Type-safe query parameters (no `Values.value(Object)`) and results
- Query builder with parameters (inline!)
- Decoder combinators for working with query results
- Monadic transaction support (like [sane-dbc](https://github.com/novarto-oss/sane-dbc))
