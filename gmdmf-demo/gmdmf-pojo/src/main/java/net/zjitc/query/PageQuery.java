package net.zjitc.query;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Schema(description = "分页查询实体")
public class PageQuery {

    @Schema(description = "当前页")
    private Integer currentPage = 1;
    @Schema(description = "每页条数")
    private Integer pageSize = 10;
    @Schema(description = "排序字段")
    private String sortBy;
    @Schema(description = "排序规则")
    private Boolean isAsc = true;

    // 分页对象转换
    public <T> Page<T> toMpPage(OrderItem... orderItems) {

        // 1 创建分页对象，并传入分页参数
        Page<T> page = Page.of(currentPage, pageSize);

        // 2.设置排序条件
        if (StrUtil.isNotBlank(sortBy)) {
            // 2.1 如果排序字段不为空
            page.addOrder(new OrderItem().setColumn(sortBy).setAsc(isAsc));
        } else if (orderItems != null) {
            // 2.2 如果排序字段为空，则根据传入的字段进行排序
            page.addOrder(orderItems);
        }

        // 3.返回对象
        return page;
    }

    // 指定字段排序
    public <T> Page<T> toMpPage(String defaultSortBy, Boolean defaultAsc) {
        return toMpPage(new OrderItem().setColumn(defaultSortBy).setAsc(defaultAsc));
    }

    // 根据创建时间排序
    public <T> Page<T> toMpPageDefaultSortByCreateTime() {
        return toMpPage(OrderItem.desc("create_time"));
    }

    // 根据更新时间排序
    public <T> Page<T> toMpPageDefaultSortByUpdateTime() {
        return toMpPage(OrderItem.desc("update_time"));
    }
}
