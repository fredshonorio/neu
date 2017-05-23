package com.fredhonorio.neu.query;

import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.harness.ServerControls;
import org.neo4j.harness.TestServerBuilders;

import java.io.Closeable;
import java.io.IOException;

public class Neo4jInProcess implements Closeable {

    private final ServerControls controls;

    private Neo4jInProcess(ServerControls controls) {
        this.controls = controls;
    }

    public Driver driver() {
        return GraphDatabase.driver(
            controls.boltURI(),
            Config.build().withEncryptionLevel(Config.EncryptionLevel.NONE).toConfig());
    }

    public static Neo4jInProcess build() {
        ServerControls ctrl = TestServerBuilders.newInProcessBuilder().newServer();
        return new Neo4jInProcess(ctrl);
    }

    @Override
    public void close() throws IOException {
        controls.close();
    }
}
