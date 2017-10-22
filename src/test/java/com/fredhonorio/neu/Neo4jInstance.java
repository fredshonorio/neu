package com.fredhonorio.neu;

import com.fredhonorio.neu.query.Var;
import com.fredhonorio.neu.query.Write;
import javaslang.Lazy;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.harness.ServerControls;
import org.neo4j.harness.TestServerBuilders;

import java.io.Closeable;
import java.io.IOException;

import static com.fredhonorio.neu.query.param.Builder.builder;

public class Neo4jInstance {

    private static Lazy<Driver> inProcess = Lazy.of(() -> InProcess.build().driver());

    private static final boolean IN_PROCESS = true;

    /**
     * Returns a new instance of a test driver.
     * If IN_PROCESS is true, creates an in-process neo4j instance, otherwise connects to a localhost instance.
     * @return
     */
    public static synchronized Driver driver() {
        return IN_PROCESS
            ? inProcess.get()
            : GraphDatabase.driver("bolt://localhost", Config.build().withEncryption().toConfig());
    }

    /**
     * Returns a new instance of a test driver, first deleting all nodes an relationships.
     * If IN_PROCESS is true, creates an in-process neo4j instance, otherwise connects to a localhost instance.
     * @return
     */
    public static synchronized Driver clean() {
        Driver d = driver();
        cleanup(d);
        return d;
    }

    public static void cleanup(Driver driver) {
        Var n = Var.of("n");
        Write.writeSession(driver, builder().Match().node(n).s("DETACH DELETE").s(n).build()).get();
    }

    private static class InProcess implements Closeable {

        private final ServerControls controls;

        private InProcess(ServerControls controls) {
            this.controls = controls;
        }

        public Driver driver() {
            return GraphDatabase.driver(
                controls.boltURI(),
                Config.build().withoutEncryption().toConfig());
        }

        public static InProcess build() {
            ServerControls ctrl = TestServerBuilders.newInProcessBuilder().newServer();
            return new InProcess(ctrl);
        }

        @Override
        public void close() throws IOException {
            controls.close();
        }
    }
}
