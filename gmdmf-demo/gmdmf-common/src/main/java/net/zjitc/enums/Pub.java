package net.zjitc.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Pub {

    PRIVATE("0", "私有"),
    APPROVAL("1", "审批"),
    PUBLIC("2", "公开");

    @EnumValue
    private final String value;

    @JsonValue
    private final String desc;

    Pub(String value, String desc) {
        this.desc = desc;
        this.value = value;
    }
}
