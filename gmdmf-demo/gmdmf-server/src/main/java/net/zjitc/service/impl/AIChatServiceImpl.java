package net.zjitc.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import net.zjitc.service.AIChatService;
import net.zjitc.vo.AIChatVO;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
@RequiredArgsConstructor
public class AIChatServiceImpl implements AIChatService {

    private final RestTemplateBuilder restTemplateBuilder;

    @Override
    public String chat(String input) {

        // 使用糊涂工具包创建 json 对象
        JSONObject requestBody = new JSONObject();

        requestBody.putOpt("model", "deepseek-r1:1.5b");
        JSONArray messages = new JSONArray();
        JSONObject message = new JSONObject().putOpt("role", "user").putOpt("content", input);
        messages.put(message);
        requestBody.putOpt("messages", messages);
        requestBody.putOpt("stream", false);

        // 通过 restTemplate 发送 post 请求
        String object = restTemplateBuilder.build().postForObject("http://localhost:11434/api/chat", requestBody.toString(), String.class);
        AIChatVO aiChatVO = JSONUtil.toBean(object, AIChatVO.class);
        return aiChatVO.getContent();
    }
}
