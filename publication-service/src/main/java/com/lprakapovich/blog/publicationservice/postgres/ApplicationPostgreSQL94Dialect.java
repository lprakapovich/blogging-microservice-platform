package com.lprakapovich.blog.publicationservice.postgres;

import org.hibernate.dialect.PostgreSQL94Dialect;

import java.sql.Types;

public class ApplicationPostgreSQL94Dialect extends PostgreSQL94Dialect {

    public ApplicationPostgreSQL94Dialect() {
        this.registerColumnType(Types.JAVA_OBJECT, "jsonb");
    }
}
