package net.zjitc.page;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PageBean<T> {

    private Long total;
    private Long pageNum;
    private List<T> list;

    // 传入字节码，将 page 对象转 PageBean<VO> 返回
    public static <PO, VO> PageBean<VO> of(Page<PO> page, Class<VO> clazz) {

        PageBean<VO> pageBean = new PageBean<>();

        pageBean.setTotal(page.getTotal());
        pageBean.setPageNum(page.getPages());

        List<PO> records = page.getRecords();
        if (CollUtil.isEmpty(records)) {
            pageBean.setList(Collections.emptyList());
            return pageBean;
        }

        // 通过 hutool 工具包的 BeanUtil 拷贝属性返回
        pageBean.setList(BeanUtil.copyToList(records, clazz));
        return pageBean;
    }

    // 传入自定义处理函数，将 page 对象转 PageBean<VO> 返回
    public static <PO, VO> PageBean<VO> of(Page<PO> page, Function<PO, VO> convertor) {

        PageBean<VO> pageBean = new PageBean<>();

        pageBean.setTotal(page.getTotal());
        pageBean.setPageNum(page.getPages());

        List<PO> records = page.getRecords();
        if (CollUtil.isEmpty(records)) {
            pageBean.setList(Collections.emptyList());
            return pageBean;
        }

        // 通过 stream 流进行处理
        pageBean.setList(records.stream().map(convertor).collect(Collectors.toList()));
        return pageBean;
    }
}
