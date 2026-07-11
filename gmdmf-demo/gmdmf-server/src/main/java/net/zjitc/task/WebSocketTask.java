package net.zjitc.task;

//import io.swagger.v3.core.util.Json;
//import lombok.RequiredArgsConstructor;
//import net.zjitc.service.OpinionService;
//import net.zjitc.vo.OpinionVO;
//import net.zjitc.websocket.MessageWebSocketServer;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class WebSocketTask {
//
//    private final MessageWebSocketServer messageWebSocketServer;
//    private final OpinionService opinionService;
//
//    /**
//     * 通过WebSocket每隔5秒向客户端发送消息
//     */
//    @Scheduled(cron = "0/5 * * * * ?")
//    public void sendMessageToClient() {
//        List<OpinionVO> opinionVOS = opinionService.findAll();
//        messageWebSocketServer.sendToAllClient(Json.pretty(opinionVOS));
//    }
//}
