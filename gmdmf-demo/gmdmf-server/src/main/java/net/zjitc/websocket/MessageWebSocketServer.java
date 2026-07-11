package net.zjitc.websocket;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.RequiredArgsConstructor;
import net.zjitc.service.MessageService;
import net.zjitc.utils.ThreadLocalUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@ServerEndpoint("/ws/{id}/{manageId}")
public class MessageWebSocketServer {

    //存放会话对象
    private static Map<Long, Session> sessionMap = new HashMap();
    private Long manageId;
    private Long id;

    @OnOpen
    public void onOpen(Session session, @PathParam("id") Long id, @PathParam("manageId") Long manageId) {
        sessionMap.put(id, session);
        this.id = id;
        this.manageId = manageId;
    }

    // 收到客户端消息/请求后调用的方法
    @OnMessage
    public void onMessage(String message) {
    }

    @OnClose
    public void onClose() {
        sessionMap.remove(ThreadLocalUtil.getId());
    }

    public void sendToAllClient(Long manageId, String message) {
        Session session = sessionMap.get(manageId);
        try {
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendToAllClient(String message) {
        Collection<Session> sessions = sessionMap.values();
        for (Session session : sessions) {
            try {
                //服务器向客户端发送消息
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
