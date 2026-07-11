package net.zjitc.service;

import net.zjitc.entity.Message;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


public interface MessageService extends IService<Message> {

    boolean send(Long manageId, String content);

    List<Message> findByManageId(Long manageId);
}
