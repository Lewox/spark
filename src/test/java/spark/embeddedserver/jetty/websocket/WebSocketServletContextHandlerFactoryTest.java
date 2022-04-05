package spark.embeddedserver.jetty.websocket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mockConstructionWithAnswer;

import jakarta.servlet.Filter;
import java.util.Map;
import java.util.Optional;
import org.eclipse.jetty.http.pathmap.PathSpec;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler.Context;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.core.server.WebSocketMappings;
import org.eclipse.jetty.websocket.core.server.WebSocketNegotiator;
import org.eclipse.jetty.websocket.server.JettyWebSocketServerContainer;
import org.eclipse.jetty.websocket.servlet.WebSocketUpgradeFilter;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.stubbing.Answer;

public class WebSocketServletContextHandlerFactoryTest {

    private static final String WEB_SOCKET_PATH = "/websocket";

    @Test
    public void testCreate_whenWebSocketHandlersIsNull_thenReturnNull() {
        final ServletContextHandler servletContextHandler = WebSocketServletContextHandlerFactory.create(null, Optional.empty());

        assertNull(servletContextHandler, "Should return null because no WebSocket Handlers were passed");
    }

    @Test
    public void testCreate_whenNoIdleTimeoutIsPresent() throws Exception {
        final Map<String, WebSocketHandlerWrapper> webSocketHandlers = Map.of(WEB_SOCKET_PATH, new WebSocketHandlerClassWrapper(WebSocketTestHandler.class));
        final ServletContextHandler servletContextHandler = WebSocketServletContextHandlerFactory.create(webSocketHandlers, Optional.empty());
   
        final Server server = new Server();
        server.setHandler(servletContextHandler);
        server.start();
        
        final Context servletContext = servletContextHandler.getServletContext();
        final Filter webSocketUpgradeFilter = WebSocketUpgradeFilter.getFilter(servletContext).getFilter();
        assertNotNull(webSocketUpgradeFilter, "Should return a WebSocketUpgradeFilter because we configured it to have one");

        final WebSocketMappings webSocketMappings = WebSocketMappings.getMappings(servletContext);
        final WebSocketNegotiator negotiator = webSocketMappings.getMatchedNegotiator(WEB_SOCKET_PATH, (PathSpec p) -> {});
        assertNotNull(negotiator, "Should return the WebSocketNegotiator path specified when context handler was created");
    }
    
    @Test
    public void testCreate_whenTimeoutIsPresent() throws Exception {
        final Long timeout = 1000L;
        final Map<String, WebSocketHandlerWrapper> webSocketHandlers = Map.of(WEB_SOCKET_PATH, new WebSocketHandlerClassWrapper(WebSocketTestHandler.class));

        final ServletContextHandler servletContextHandler = WebSocketServletContextHandlerFactory.create(webSocketHandlers, Optional.of(timeout));

        final Server server = new Server();
        server.setHandler(servletContextHandler);
        server.start();
        
        final Context servletContext = servletContextHandler.getServletContext();
        final Filter webSocketUpgradeFilter = WebSocketUpgradeFilter.getFilter(servletContext).getFilter();
        assertNotNull(webSocketUpgradeFilter, "Should return a WebSocketUpgradeFilter because we configured it to have one");

        final JettyWebSocketServerContainer container = servletContextHandler.getBean(JettyWebSocketServerContainer.class);

        assertEquals(timeout, container.getIdleTimeout().toMillis(),
                "Timeout value should be the same as the timeout specified when context handler was created");

        final WebSocketMappings webSocketMappings = WebSocketMappings.getMappings(servletContext);
        final WebSocketNegotiator negotiator = webSocketMappings.getMatchedNegotiator(WEB_SOCKET_PATH, (PathSpec p) -> {});
        assertNotNull(negotiator, "Should return the WebSocketNegotiator path specified when context handler was created");
    }

    @Test
    public void testCreate_whenWebSocketContextHandlerCreationFails_thenThrowException() {
        try (MockedConstruction<ServletContextHandler> ignored = mockConstructionWithAnswer(ServletContextHandler.class, (Answer<RuntimeException>) invocation -> {
            throw new RuntimeException("Expected test error");
        })) {
            final Map<String, WebSocketHandlerWrapper> webSocketHandlers = Map.of(WEB_SOCKET_PATH, new WebSocketHandlerClassWrapper(WebSocketTestHandler.class));

            final ServletContextHandler servletContextHandler = WebSocketServletContextHandlerFactory.create(webSocketHandlers, Optional.empty());

            assertNull(servletContextHandler, "Should return null because Websocket context handler was not created");
        }
    }
}
