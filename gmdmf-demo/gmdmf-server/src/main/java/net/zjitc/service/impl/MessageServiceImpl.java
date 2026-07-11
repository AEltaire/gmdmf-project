package net.zjitc.service.impl;

import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import net.zjitc.entity.Message;
import net.zjitc.mapper.MessageMapper;
import net.zjitc.service.MessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.zjitc.utils.CheckEmptyOrRuleUtil;
import net.zjitc.utils.ThreadLocalUtil;
import net.zjitc.websocket.MessageWebSocketServer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    private final MessageWebSocketServer messageWebSocketServer;

    @Override
    public boolean send(Long manageId, String content) {

        CheckEmptyOrRuleUtil.isManager();
        Message message = new Message().setCome(ThreadLocalUtil.getId()).setSend(manageId).setContent(content).setCreateTime(LocalDateTime.now());
        save(message);
        List<Message> messageList = findByManageId(manageId);
        messageWebSocketServer.sendToAllClient(manageId, Json.pretty(messageList));
        return true;
    }

    @Override
    public List<Message> findByManageId(Long manageId) {

        CheckEmptyOrRuleUtil.isManager();
        List<Message> messages1 = lambdaQuery().eq(Message::getSend, manageId).eq(Message::getCome, ThreadLocalUtil.getId()).list();
        List<Message> messages2 = lambdaQuery().eq(Message::getCome, manageId).eq(Message::getSend, ThreadLocalUtil.getId()).list();
        messages1.addAll(messages2);
        return messages1.stream().sorted((Comparator.comparing(Message::getCreateTime))).toList();
    }
}
