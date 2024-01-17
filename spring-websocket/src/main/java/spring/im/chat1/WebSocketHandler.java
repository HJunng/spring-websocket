package spring.im.chat1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    /**
     * 웹소켓 연결
     * : 클라이언트가 웹소켓 서버에 연결된 다른 사용자에게 접속 여부를 전달해주는 로직.
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        final String sessionId = session.getId();
        final String enterdMessage = sessionId + "님이 입장하셨습니다.";

        log.trace("new connection request: {}", sessionId);

        sessions.put(sessionId, session); // 세션 저장

        sessions.values().forEach(s -> {
            try{
                if(!s.getId().equals(sessionId)){
                    s.sendMessage(new TextMessage(enterdMessage));
                }
            } catch (Exception e){
                log.error("fail to send message", e);
            }
        });
    }

    // 양방향 데이터 통신
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {

        final String sessionId = session.getId();
        sessions.values().forEach(s -> {
            if(!s.getId().equals(sessionId) && s.isOpen()){
                try{
                    s.sendMessage(textMessage);
                } catch (Exception e){
                    log.error("runtime exception", e);
                }
            }
        });
    }

    // 웹소켓 통신 에러
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        // TODO
        super.handleTransportError(session, exception);
    }

    // 웹소켓 연결 해제
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        final String  sessionId = session.getId();
        final String exitMessage = sessionId + "님이 퇴장하셨습니다.";

        sessions.remove(sessionId); // 세션 삭제

        // 퇴장 메시지 전송
        sessions.values().forEach(s -> {
            try{
                if(!s.getId().equals(sessionId) && s.isOpen()) {
                    s.sendMessage(new TextMessage(exitMessage));
                }
            } catch (Exception e){
                log.error("fail to send message", e);
            }
        });


    }
}
