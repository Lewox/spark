/*
 * Copyright 2015 - Per Wendel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spark.embeddedserver.jetty.websocket;

import static java.time.temporal.ChronoUnit.MILLIS;
import static org.eclipse.jetty.websocket.core.WebSocketConstants.DEFAULT_MAX_TEXT_MESSAGE_SIZE;

import java.time.Duration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.server.JettyWebSocketCreator;
import org.eclipse.jetty.websocket.server.config.JettyWebSocketServletContainerInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates websocket servlet context handlers.
 * 
 * @author Per Wendel
 * @author León Keuroglián
 */
public class WebSocketServletContextHandlerFactory {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketServletContextHandlerFactory.class);

    /**
     * Creates a new websocket servlet context handler.
     *
     * @param webSocketHandlers          webSocketHandlers
     * @param webSocketIdleTimeoutMillis webSocketIdleTimeoutMillis
     * @return a new websocket servlet context handler or 'null' if creation failed.
     */
    public static ServletContextHandler create(Map<String, WebSocketHandlerWrapper> webSocketHandlers,
                                               Optional<Long> webSocketIdleTimeoutMillis) {
        ServletContextHandler webSocketServletContextHandler = null;
        if (webSocketHandlers != null) {
            try {
                webSocketServletContextHandler = new ServletContextHandler(null, "/", true, false);
                
                JettyWebSocketServletContainerInitializer.configure(webSocketServletContextHandler, (servletContext, wsContainer) -> {
                    // Configure default max size
                    wsContainer.setMaxTextMessageSize(DEFAULT_MAX_TEXT_MESSAGE_SIZE);

                    webSocketIdleTimeoutMillis.ifPresent(timeout -> wsContainer.setIdleTimeout(Duration.of(timeout, MILLIS)));
                    
                    for (Entry<String, WebSocketHandlerWrapper> wsHandler : webSocketHandlers.entrySet()) {
                        final JettyWebSocketCreator webSocketCreator = WebSocketCreatorFactory.create(wsHandler.getValue());
                        wsContainer.addMapping(wsHandler.getKey(), webSocketCreator);
                    }
                });
            } catch (Exception ex) {
                logger.error("creation of websocket context handler failed.", ex);
                webSocketServletContextHandler = null;
            }
        }
        return webSocketServletContextHandler;
    }
}
