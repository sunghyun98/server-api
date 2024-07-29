package gong.server_api;

import gong.server_api.handler.ChatWebSocketHandler;
import gong.server_api.handler.HeaderValidationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatWebSocketHandler chatWebSocketHandler;
    private final HeaderValidationInterceptor headerValidationInterceptor;
    @Autowired
    public WebSocketConfig(ChatWebSocketHandler chatWebSocketHandler, HeaderValidationInterceptor headerValidationInterceptor) {
        this.chatWebSocketHandler = chatWebSocketHandler;
        this.headerValidationInterceptor = headerValidationInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler, "/ws/chat")
                .addInterceptors(headerValidationInterceptor)
                .setAllowedOrigins("*");
    }
}