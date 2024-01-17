package spring.im.chat1;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Utils {
    // ObjectMapper 인스턴스를 정의. JSON 처리를 위해 사용함.
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * JSON 문자열을 Message 객체로 변환
     *
     * @param message JSON 문자열
     * @return Message 객체. JSON 문자열이 해당 객체로 반환된다.
     * @throws Exception
     */
    public static Message getObject(final String message) throws Exception {
        return objectMapper.readValue(message, Message.class);
    }

    /**
     * Message 객체를 JSON 문자열로 변환
     *
     * @param message Message 객체
     * @return JSON 형식의 문자열. Message 객체가 해당 문자열로 반환된다.
     * @throws Exception
     */
    public static String getString(final Message message) throws Exception {
        return objectMapper.writeValueAsString(message);
    }
}
