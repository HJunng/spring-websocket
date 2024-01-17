package spring.im.chat1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Slf4j
@Configuration
@EnableWebSocket // 웹소켓 서버를 사용하도록 정의.
public class WebSocketConfig implements WebSocketConfigurer{
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(signalingHandler(), "/ws/chat")
                .setAllowedOrigins("*"); // 클라이언트에서 웹소켓서버에 요청 시 모든 요청 허용.
    }

    @Bean
    public WebSocketHandler signalingHandler() {
        return new WebSocketHandler();
    }
}
