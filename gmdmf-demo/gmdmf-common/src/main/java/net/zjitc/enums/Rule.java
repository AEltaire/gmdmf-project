package net.zjitc.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Rule {

    STUDENT(1, "学生"),
    TEACHER(2, "教师"),
    MANAGE(3, "管理");

    @EnumValue
    private final int value;

    @JsonValue
    private final String desc;

    Rule(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

}
