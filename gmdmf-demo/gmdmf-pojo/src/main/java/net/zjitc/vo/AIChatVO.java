package net.zjitc.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIChatVO {

    String model;
    LocalDateTime created_at;
    Message message;
    String done_reason;
    Boolean done;
    Integer totalDuration;
    Integer load_duration;
    Integer prompt_eval_count;
    Integer prompt_eval_duration;
    Integer eval_count;
    Integer eval_duration;

    public  String getContent() {
        return message.getContent();
    }
}

@Data
class  Message{
    String role;
    String content;
}