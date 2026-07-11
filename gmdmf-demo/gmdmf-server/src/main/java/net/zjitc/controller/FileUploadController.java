package net.zjitc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.ResourceDTO;
import net.zjitc.result.Result;
import net.zjitc.service.ResourceService;
import net.zjitc.utils.AliOssUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;


@Tag(name = "文件上传相关接口")
@RestController
@RequiredArgsConstructor
public class FileUploadController {

    private final ResourceService resourceService;

    // 将图片内容保存到阿里云中
    @Operation(summary = "图片上传")
    @PostMapping("/uploadImg")
    public Result<String> uploadImage(MultipartFile file) throws Exception {
        String fileName = generateFileName(file);
        String URL = uploadToAliOss(fileName, file);
        return Result.success(Code.POST_OK, "上传成功", URL);
    }

    // 将文件内容保存到阿里云中
    @Operation(summary = "文件上传")
    @PostMapping("/uploadFile")
    public Result<String> uploadFile(ResourceDTO resourceDTO) throws Exception {
        MultipartFile file = resourceDTO.getFile();
        String fileName = generateFileName(file);
        String URL = uploadToAliOss(fileName, file);
        resourceDTO.setUrl(URL);
        resourceService.saveResource(resourceDTO);
        return Result.success(Code.POST_OK, "上传成功");
    }

    // 将文件内容保存到阿里云中
    @Operation(summary = "视频上传")
    @PostMapping("/uploadMP4")
    public Result<String> uploadMP4(MultipartFile file) throws Exception {
        System.out.println(file.getOriginalFilename());
        String fileName = generateFileName(file);
        String URL = uploadToAliOss(fileName, file);
        return Result.success(Code.POST_OK, "上传成功", URL);
    }

    private String generateFileName(MultipartFile file) {
        Integer index = file.getOriginalFilename().lastIndexOf(".");
        String fileName = file.getOriginalFilename();
        if (index != null) {
            fileName = new Date().getTime() + file.getOriginalFilename().substring(index);
        }
        return fileName;
    }

    private String uploadToAliOss(String fileName, MultipartFile file) throws Exception {
        return AliOssUtil.uploadFile(fileName, file.getInputStream());
    }
}
