package net.zjitc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManageDTO {

    private Long id;
    private String name;
    private String username;
    private String password;
    private String introduce;
    private String picture;
}
