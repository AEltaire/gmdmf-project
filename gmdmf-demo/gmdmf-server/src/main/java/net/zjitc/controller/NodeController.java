package net.zjitc.controller;

import cn.hutool.core.collection.CollUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import net.zjitc.constant.Code;
import net.zjitc.dto.NodeDTO;
import net.zjitc.result.Result;
import net.zjitc.service.NodeService;
import net.zjitc.vo.NodeVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "节点相关接口")
@RestController
@RequestMapping("/node")
@RequiredArgsConstructor
public class NodeController extends BaseController{

    private final NodeService nodeService;


    @Operation(summary = "添加节点")
    @PostMapping
    private Result<List<NodeVO>> save(@RequestBody NodeDTO nodeDTO) {
        boolean flag = nodeService.saveNode(nodeDTO);
        return toPostAjax(flag, "添加节点成功");
    }


    @Operation(summary = "修改节点")
    @PutMapping
    private Result<List<NodeVO>> update(@RequestBody NodeDTO nodeDTO) throws Exception {
        boolean flag = nodeService.updateNode(nodeDTO);
        return toPutAjax(flag, "修改节点成功");
    }


    @Operation(summary = "删除节点")
    @DeleteMapping("{id}")
    private Result<List<NodeVO>> delete(@PathVariable("id") Long id) throws Exception {
        boolean flag = nodeService.deleteNode(id);
        return toDeleteAjax(flag, "删除节点成功");
    }

    @Operation(summary = "查询所有")
    @GetMapping("/all")
    private Result<List<NodeVO>> findAll() {

        List<NodeVO> nodeVOS = nodeService.findAll();
        return toGetAjax(CollUtil.isEmpty(nodeVOS),"查询节点成功", nodeVOS);
    }

    @Operation(summary = "根据课程id查询")
    @GetMapping("{id}")
    private Result<List<NodeVO>> findByChapterId(@PathVariable("id") Long chapterId) {

        List<NodeVO> nodeVOS = nodeService.findByChapterId(chapterId);
        return toGetAjax(CollUtil.isEmpty(nodeVOS),"查询节点成功", nodeVOS);
    }
}
