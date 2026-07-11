package net.zjitc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.zjitc.enums.Pub;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDTO {

    private Long id;

    @NotNull(groups = Add.class, message = "题库id不能为空")
    private Long warehouseId;

    @NotBlank(groups = Add.class, message = "题目类型不能为空")
    private String type;

    @NotBlank(groups = Add.class, message = "题目名不能为空")
    private String label;

    @NotBlank(groups = Add.class, message = "题目问题不能为空")
    private String question;

    private String options;

    @NotBlank(groups = Add.class, message = "答案不能为空")
    private String answer;

    @NotBlank(groups = Add.class, message = "解析不能为空")
    private String analysis;

    @NotBlank(groups = Add.class, message = "难度不能为空")
    private String level;

    private Pub pub;

    private Float coefficient;

    private Float pass;

    public interface Add extends Default {
    }

    public interface Update extends Default {
    }
}
