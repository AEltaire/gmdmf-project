package net.zjitc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DepartmentDTO implements Serializable {

    private Long id;
    private String name;
    private Long schoolId;
    private String schoolName;
    private Boolean exhibition;
}
