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

        var sessionId = session.getId();
        log.trace("new connection request: {}", sessionId);

        sessions.put(sessionId, session); // 세션 저장

        Message message = Message.builder()
                .sender(sessionId)
                .receiver("all")
                .build();
        message.newConnect(); // 새로운 접속자

        sessions.values().forEach(s -> {
            try{
                if(!s.getId().equals(sessionId)){
                    s.sendMessage(new TextMessage(Utils.getString(message)));
                }
            } catch (Exception e){
                log.error("fail to send message", e);
            }
        });
    }

    // 양방향 데이터 통신
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {

        Message message = Utils.getObject(textMessage.getPayload());
        message.setSender(session.getId());

        WebSocketSession receiver = sessions.get(message.getReceiver());

        if(receiver != null && receiver.isOpen()){
            receiver.sendMessage(new TextMessage(Utils.getString(message)));
        }
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

        var sessionId = session.getId();
        sessions.remove(sessionId);

        final Message message = new Message();
        message.closeConnect(); // 접속 종료
        message.setSender(sessionId);

        sessions.values().forEach(s -> {
            try{
                s.sendMessage(new TextMessage(Utils.getString(message)));
            } catch (Exception e){
                log.error("fail to send message", e);
            }
        });


    }
}
