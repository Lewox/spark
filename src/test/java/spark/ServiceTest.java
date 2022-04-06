package spark;

import jakarta.servlet.http.HttpServletResponse;

import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import spark.embeddedserver.EmbeddedServer;
import spark.embeddedserver.EmbeddedServers;
import spark.route.Routes;
import spark.ssl.SslStores;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static spark.Service.ignite;

public class ServiceTest {

    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT = 8080;
    private static final int NOT_FOUND_STATUS_CODE = HttpServletResponse.SC_NOT_FOUND;

    private Service service;

    @BeforeEach
    public void test() {
        service = ignite();
    }

    @Test
    public void testEmbeddedServerIdentifier_defaultAndSet() {
        assertEquals(EmbeddedServers.defaultIdentifier(), service.embeddedServerIdentifier(), "Should return defaultIdentifier()");

        Object obj = new Object();

        service.embeddedServerIdentifier(obj);

        assertEquals(obj, service.embeddedServerIdentifier(), "Should return expected obj");
    }

    @Test
    public void testEmbeddedServerIdentifier_thenThrowIllegalStateException() {
        Object obj = new Object();

        Whitebox.setInternalState(service, "initialized", true);
        
        final IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.embeddedServerIdentifier(obj));
        assertEquals("This must be done before route mapping has begun", ex.getMessage());
    }

    @Test
    public void testHalt_whenOutParameters_thenThrowHaltException() {
        assertThrows(HaltException.class, () -> service.halt());
    }

    @Test
    public void testHalt_whenStatusCode_thenThrowHaltException() {
        assertThrows(HaltException.class, () -> service.halt(NOT_FOUND_STATUS_CODE));
    }

    @Test
    public void testHalt_whenBodyContent_thenThrowHaltException() {
        assertThrows(HaltException.class, () -> service.halt("error"));
    }

    @Test
    public void testHalt_whenStatusCodeAndBodyContent_thenThrowHaltException() {
        assertThrows(HaltException.class, () -> service.halt(NOT_FOUND_STATUS_CODE, "error"));
    }

    @Test
    public void testIpAddress_whenInitializedFalse() {
        service.ipAddress(IP_ADDRESS);

        String ipAddress = Whitebox.getInternalState(service, "ipAddress");
        assertEquals(IP_ADDRESS, ipAddress, "IP address should be set to the IP address that was specified");
    }

    @Test
    public void testIpAddress_whenInitializedTrue_thenThrowIllegalStateException() {
        Whitebox.setInternalState(service, "initialized", true);
        
        final IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.ipAddress(IP_ADDRESS));
        assertEquals("This must be done before route mapping has begun", ex.getMessage());
    }

    @Test
    public void testSetIpAddress_whenInitializedFalse() {
        service.ipAddress(IP_ADDRESS);

        String ipAddress = Whitebox.getInternalState(service, "ipAddress");
        assertEquals(IP_ADDRESS, ipAddress, "IP address should be set to the IP address that was specified");
    }

    @Test
    public void testSetIpAddress_whenInitializedTrue_thenThrowIllegalStateException() {
        Whitebox.setInternalState(service, "initialized", true);
        
        final IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.ipAddress(IP_ADDRESS));
        assertEquals("This must be done before route mapping has begun", ex.getMessage());
    }

    @Test
    public void testPort_whenInitializedFalse() {
        service.port(PORT);

        int port = Whitebox.getInternalState(service, "port");
        assertEquals(PORT, port, "Port should be set to the Port that was specified");
    }

    @Test
    public void testPort_whenInitializedTrue_thenThrowIllegalStateException() {
        Whitebox.setInternalState(service, "initialized", true);
        
        final IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.port(PORT));
        assertEquals("This must be done before route mapping has begun", ex.getMessage());
    }

    @Test
    public void testSetPort_whenInitializedFalse() {
        service.port(PORT);

        int port = Whitebox.getInternalState(service, "port");
        assertEquals(PORT, port, "Port should be set to the Port that was specified");
    }

    @Test
    public void testSetPort_whenInitializedTrue_thenThrowIllegalStateException() {
        Whitebox.setInternalState(service, "initialized", true);
        
        final IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.port(PORT));
        assertEquals("This must be done before route mapping has begun", ex.getMessage());
    }

    @Test
    public void testGetPort_whenInitializedFalse_thenThrowIllegalStateException() {
        Whitebox.setInternalState(service, "initialized", false);
        
        final IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.port());
        assertEquals("This must be done after route mapping has begun", ex.getMessage());
    }

    @Test
    public void testGetPort_whenInitializedTrue() {
        Whitebox.setInternalState(service, "initialized", true);
        Whitebox.setInternalState(service, "port", PORT);

        int actualPort = service.port();

        assertEquals(PORT, actualPort, "Port retrieved should be the port setted");
    }

    @Test
    public void testGetPort_whenInitializedTrue_Default() {
        int expectedPort = Service.SPARK_DEFAULT_PORT;
        Whitebox.setInternalState(service, "initialized", true);

        int actualPort = service.port();

        assertEquals(expectedPort, actualPort, "Port retrieved should be the port setted");
    }

    @Test
    public void testThreadPool_whenOnlyMaxThreads() {
        service.threadPool(100);
        int maxThreads = Whitebox.getInternalState(service, "maxThreads");
        int minThreads = Whitebox.getInternalState(service, "minThreads");
        int threadIdleTimeoutMillis = Whitebox.getInternalState(service, "threadIdleTimeoutMillis");
        assertEquals(100, maxThreads, "Should return maxThreads specified");
        assertEquals(-1, minThreads, "Should return minThreads specified");
        assertEquals(-1, threadIdleTimeoutMillis, "Should return threadIdleTimeoutMillis specified");
    }

    @Test
    public void testThreadPool_whenMaxMinAndTimeoutParameters() {
        service.threadPool(100, 50, 75);
        int maxThreads = Whitebox.getInternalState(service, "maxThreads");
        int minThreads = Whitebox.getInternalState(service, "minThreads");
        int threadIdleTimeoutMillis = Whitebox.getInternalState(service, "threadIdleTimeoutMillis");
        assertEquals(100, maxThreads, "Should return maxThreads specified");
        assertEquals(50, minThreads, "Should return minThreads specified");
        assertEquals(75, threadIdleTimeoutMillis, "Should return threadIdleTimeoutMillis specified");
    }

    @Test
    public void testThreadPool_whenMaxMinAndTimeoutParameters_thenThrowIllegalStateException() {
        Whitebox.setInternalState(service, "initialized", true);
        
        final IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.threadPool(100, 50, 75));
        assertEquals("This must be done before route mapping has begun", ex.getMessage());
    }

    @Test
    public void testSecure_thenReturnNewSslStores() {
        service.secure("keyfile", "keypassword", "truststorefile", "truststorepassword");
        SslStores sslStores = Whitebox.getInternalState(service, "sslStores");
        assertNotNull(sslStores, "Should return a SslStores because we configured it to have one");
        assertEquals("keyfile", sslStores.keystoreFile(), "Should return keystoreFile from SslStores");
        assertEquals("keypassword", sslStores.keystorePassword(), "Should return keystorePassword from SslStores");
        assertEquals("truststorefile", sslStores.trustStoreFile(), "Should return trustStoreFile from SslStores");
        assertEquals("truststorepassword", sslStores.trustStorePassword(), "Should return trustStorePassword from SslStores");
    }

    @Test
    public void testSecure_whenInitializedTrue_thenThrowIllegalStateException() {
        Whitebox.setInternalState(service, "initialized", true);
        
        final IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.secure(null, null, null, null));
        assertEquals("This must be done before route mapping has begun", ex.getMessage());
    }

    @Test
    public void testSecure_whenInitializedFalse_thenThrowIllegalArgumentException() {
        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.secure(null, null, null, null));
        assertEquals("Must provide a keystore file to run secured", ex.getMessage());
    }

    @Test
    public void testWebSocketIdleTimeoutMillis_whenInitializedTrue_thenThrowIllegalStateException() {
        Whitebox.setInternalState(service, "initialized", true);
        
        final IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.webSocketIdleTimeoutMillis(100));
        assertEquals("This must be done before route mapping has begun", ex.getMessage());
    }

    @Test
    public void testWebSocket_whenInitializedTrue_thenThrowIllegalStateException() {
        Whitebox.setInternalState(service, "initialized", true);
        
        final IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.webSocket("/", DummyWebSocketListener.class));
        assertEquals("This must be done before route mapping has begun", ex.getMessage());
    }
    
    @Test
    public void testWebSocket_whenPathNull_thenThrowNullPointerException() {
        final NullPointerException ex = assertThrows(NullPointerException.class, () -> service.webSocket(null, new DummyWebSocketListener()));
        assertEquals("WebSocket path cannot be null", ex.getMessage());
    }
    
    @Test
    public void testWebSocket_whenHandlerNull_thenThrowNullPointerException() {
        final NullPointerException ex = assertThrows(NullPointerException.class, () -> service.webSocket("/", null));
        assertEquals("WebSocket handler class cannot be null", ex.getMessage());
    }
    
    @Test
    @Timeout(value = 300, unit = MILLISECONDS)
    public void stopExtinguishesServer() {
        Service service = Service.ignite();
        Routes routes = Mockito.mock(Routes.class);
        EmbeddedServer server = Mockito.mock(EmbeddedServer.class);
        service.routes = routes;
        service.server = server;
        service.initialized = true;
        service.stop();
        try {
        	// yes, this is ugly and forces to set a test timeout as a precaution :(
            while (service.initialized) {
            	Thread.sleep(20);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Mockito.verify(server).extinguish();
    }
    
    @Test
    public void awaitStopBlocksUntilExtinguished() {
        Service service = Service.ignite();
        Routes routes = Mockito.mock(Routes.class);
        EmbeddedServer server = Mockito.mock(EmbeddedServer.class);
        service.routes = routes;
        service.server = server;
        service.initialized = true;
        service.stop();
        service.awaitStop();
        Mockito.verify(server).extinguish();
        assertFalse(service.initialized);
    }
    
    @WebSocket
    protected static class DummyWebSocketListener {
    }
}
