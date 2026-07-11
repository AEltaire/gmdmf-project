package net.zjitc.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.zjitc.enums.Rule;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManageVO {

    private Long id;

    private String name;

    private String username;

    private String introduce;

    private String picture;
    
    private String rule;
}
