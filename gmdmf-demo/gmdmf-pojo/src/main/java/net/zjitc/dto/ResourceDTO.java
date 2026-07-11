package net.zjitc.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ResourceDTO {

    private Long id;
    private Long courseId;
    private String name;
    private String type;
    @JsonIgnore
    private String url;
    private MultipartFile file;
}
