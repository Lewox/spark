package spark.embeddedserver.jetty.websocket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mockConstructionWithAnswer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.servlet.ServletContext;
import org.eclipse.jetty.http.pathmap.MappedResource;
import org.eclipse.jetty.http.pathmap.PathSpec;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.server.NativeWebSocketConfiguration;
import org.eclipse.jetty.websocket.server.WebSocketServerFactory;
import org.eclipse.jetty.websocket.server.WebSocketUpgradeFilter;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.stubbing.Answer;

public class WebSocketServletContextHandlerFactoryTest {

    final String webSocketPath = "/websocket";
    private ServletContextHandler servletContextHandler;

    @Test
    public void testCreate_whenWebSocketHandlersIsNull_thenReturnNull() {

        servletContextHandler = WebSocketServletContextHandlerFactory.create(null, Optional.empty());

        assertNull(servletContextHandler, "Should return null because no WebSocket Handlers were passed");

    }

    @Test
    public void testCreate_whenNoIdleTimeoutIsPresent() {

        Map<String, WebSocketHandlerWrapper> webSocketHandlers = new HashMap<>();

        webSocketHandlers.put(webSocketPath, new WebSocketHandlerClassWrapper(WebSocketTestHandler.class));

        servletContextHandler = WebSocketServletContextHandlerFactory.create(webSocketHandlers, Optional.empty());

        ServletContext servletContext = servletContextHandler.getServletContext();

        WebSocketUpgradeFilter webSocketUpgradeFilter = (WebSocketUpgradeFilter) servletContext
                .getAttribute("org.eclipse.jetty.websocket.server.WebSocketUpgradeFilter");

        assertNotNull(webSocketUpgradeFilter, "Should return a WebSocketUpgradeFilter because we configured it to have one");

        NativeWebSocketConfiguration webSocketConfiguration = (NativeWebSocketConfiguration) servletContext
                .getAttribute(NativeWebSocketConfiguration.class.getName());

        MappedResource<WebSocketCreator> mappedResource = webSocketConfiguration.getMatch("/websocket");
        PathSpec pathSpec = mappedResource.getPathSpec();

        assertEquals(webSocketPath, pathSpec.getDeclaration(),
                "Should return the WebSocket path specified when context handler was created");

        // Because spark works on a non-initialized / non-started ServletContextHandler
        // and WebSocketUpgradeFilter
        // the stored WebSocketCreator is wrapped for persistence through the start/stop
        // of those contexts.
        // You cannot unwrap or cast to that WebSocketTestHandler this way.
        // Only websockets that are added during a live context can be cast this way.
        // WebSocketCreator sc = mappedResource.getResource();
        // assertTrue("Should return true because handler should be an instance of the
        // one we passed when it was created",
        // sc.getHandler() instanceof WebSocketTestHandler);
    }

    @Test
    public void testCreate_whenTimeoutIsPresent() {

        final Long timeout = 1000L;

        Map<String, WebSocketHandlerWrapper> webSocketHandlers = new HashMap<>();

        webSocketHandlers.put(webSocketPath, new WebSocketHandlerClassWrapper(WebSocketTestHandler.class));

        servletContextHandler = WebSocketServletContextHandlerFactory.create(webSocketHandlers, Optional.of(timeout));

        ServletContext servletContext = servletContextHandler.getServletContext();

        WebSocketUpgradeFilter webSocketUpgradeFilter = (WebSocketUpgradeFilter) servletContext
                .getAttribute("org.eclipse.jetty.websocket.server.WebSocketUpgradeFilter");

        assertNotNull(webSocketUpgradeFilter, "Should return a WebSocketUpgradeFilter because we configured it to have one");

        NativeWebSocketConfiguration webSocketConfiguration = (NativeWebSocketConfiguration) servletContext
                .getAttribute(NativeWebSocketConfiguration.class.getName());

        WebSocketServerFactory webSocketServerFactory = webSocketConfiguration.getFactory();
        assertEquals(timeout.longValue(), webSocketServerFactory.getPolicy().getIdleTimeout(),
                "Timeout value should be the same as the timeout specified when context handler was created");

        MappedResource<WebSocketCreator> mappedResource = webSocketConfiguration.getMatch("/websocket");
        PathSpec pathSpec = mappedResource.getPathSpec();

        assertEquals(webSocketPath, pathSpec.getDeclaration(),
                "Should return the WebSocket path specified when context handler was created");

        // Because spark works on a non-initialized / non-started ServletContextHandler
        // and WebSocketUpgradeFilter
        // the stored WebSocketCreator is wrapped for persistence through the start/stop
        // of those contexts.
        // You cannot unwrap or cast to that WebSocketTestHandler this way.
        // Only websockets that are added during a live context can be cast this way.
        // WebSocketCreator sc = mappedResource.getResource();
        // assertTrue(sc.getHandler() instanceof WebSocketTestHandler,
        // "Should return true because handler should be an instance of the one we
        // passed when it was created",);
    }

    @Test
    public void testCreate_whenWebSocketContextHandlerCreationFails_thenThrowException() {
        try (MockedConstruction<ServletContextHandler> ignored = mockConstructionWithAnswer(ServletContextHandler.class, (Answer<RuntimeException>) invocation -> {
            throw new RuntimeException("Some problem initializing context handler!");
        })) {
            Map<String, WebSocketHandlerWrapper> webSocketHandlers = new HashMap<>();

            webSocketHandlers.put(webSocketPath, new WebSocketHandlerClassWrapper(WebSocketTestHandler.class));

            servletContextHandler = WebSocketServletContextHandlerFactory.create(webSocketHandlers, Optional.empty());

            assertNull(servletContextHandler, "Should return null because Websocket context handler was not created");
        }
    }
}
