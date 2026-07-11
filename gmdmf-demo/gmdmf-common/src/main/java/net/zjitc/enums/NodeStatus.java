package net.zjitc.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum NodeStatus {

    PRIVATE("0", "私有"),
    VETO("1", "否决"),
    PASS("2", "通过");

    @EnumValue
    private final String value;

    @JsonValue
    private final String desc;

    NodeStatus(String value, String desc) {
        this.desc = desc;
        this.value = value;
    }
}
