package ru.runa.wfe.chat.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.runa.wfe.chat.dto.broadcast.MessageBroadcast;
import ru.runa.wfe.chat.socket.SessionInfo;
import ru.runa.wfe.chat.utils.ChatSessionUtils;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@CommonsLog
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SessionMessageSender implements MessageSender {

    @Qualifier("mailMessageSender")
    private final MessageSender messageSender;
    private final ObjectMapper chatObjectMapper;

    @Override
    public void handleMessage(MessageBroadcast dto, Set<SessionInfo> sessions) {
        try {
            for (SessionInfo sessionInfo : sessions) {
                Session session = sessionInfo.getSession();
                session.getBasicRemote().sendText(chatObjectMapper.writeValueAsString(dto));
            }
        } catch (IOException e) {
            log.error("An error occurred while sending a message to " +
                    ChatSessionUtils.getUser(sessions.iterator().next().getSession()).getName(), e);
            messageSender.handleMessage(dto, sessions);
        }
    }
}
