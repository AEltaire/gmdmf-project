package net.zjitc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.zjitc.enums.Pub;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subject {

    private Long id;
    private Long warehouseId;
    private String type;
    private String label;
    private String question;
    private String options;
    private String answer;
    private String analysis;
    private String level;
    private Pub pub;
    private Float pass;
    private Float coefficient;
}
