package spark.embeddedserver;

import java.io.File;
import org.eclipse.jetty.server.CustomRequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import spark.Spark;
import spark.embeddedserver.jetty.EmbeddedJettyFactory;
import spark.embeddedserver.jetty.JettyServerFactory;

import static org.eclipse.jetty.server.CustomRequestLog.EXTENDED_NCSA_FORMAT;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmbeddedServersTest {

    @Test
    public void testAddAndCreate_whenCreate_createsCustomServer(@TempDir File requestLogDir) throws Exception {
        // Create custom Server
        Server server = new Server();
        File requestLogFile = new File(requestLogDir, "request.log");
        server.setRequestLog(new CustomRequestLog(requestLogFile.getAbsolutePath(), EXTENDED_NCSA_FORMAT));
        JettyServerFactory serverFactory = mock(JettyServerFactory.class);
        when(serverFactory.create(0, 0, 0)).thenReturn(server);

        String id = "custom";

        // Register custom server
        EmbeddedServers.add(id, new EmbeddedJettyFactory(serverFactory));
        EmbeddedServer embeddedServer = EmbeddedServers.create(id, null, null, null, false);
        assertNotNull(embeddedServer);

        embeddedServer.trustForwardHeaders(true);
        embeddedServer.ignite("localhost", 0, null, 0, 0, 0);

        assertTrue(requestLogFile.exists());
        embeddedServer.extinguish();
        verify(serverFactory).create(0, 0, 0);
    }

    @Test
    public void testAdd_whenConfigureRoutes_createsCustomServer(@TempDir File requestLogDir) {
        File requestLogFile = new File(requestLogDir, "request.log");
        // Register custom server
        EmbeddedServers.add(EmbeddedServers.Identifiers.JETTY, new EmbeddedJettyFactory(new JettyServerFactory() {
            @Override
            public Server create(int maxThreads, int minThreads, int threadTimeoutMillis) {
                Server server = new Server();
                server.setRequestLog(new CustomRequestLog(requestLogFile.getAbsolutePath(), EXTENDED_NCSA_FORMAT));
                return server;
            }

            @Override
            public Server create(ThreadPool threadPool) {
                return null;
            }
        }));
        Spark.get("/", (request, response) -> "OK");
        Spark.awaitInitialization();

        assertTrue(requestLogFile.exists());
    }

    @AfterAll
    public static void tearDown() {
        Spark.stop();
    }

}
