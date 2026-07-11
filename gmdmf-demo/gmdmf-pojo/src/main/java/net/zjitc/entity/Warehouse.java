package net.zjitc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Warehouse {

    private Long id;

    private String school;

    private Long teacherId;

    private String name;

    private LocalDateTime createTime;
}
