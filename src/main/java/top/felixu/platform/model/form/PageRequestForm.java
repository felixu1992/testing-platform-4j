package top.felixu.platform.model.form;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import top.felixu.common.parameter.Splitters;

import java.io.Serializable;
import java.util.List;

/**
 * 分页参数封装
 *
 * @author felixu
 * @since 2021.08.02
 */
@Data
public class PageRequestForm implements Serializable {

    /**
     * 默认页数
     */
    private static final int DEFAULT_PAGE = 1;

    /**
     * 默认每页数量
     */
    private static final int DEFAULT_SIZE = 10;

    /**
     * 页数
     */
    private int current;

    /**
     * 每页数量
     */
    private int size;

    /**
     * 是否查询总数
     */
    private boolean searchCount = true;

    /**
     * 排序字段
     */
    private List<String> orders;

    public PageRequestForm() {
        this(null, null);
    }

    public PageRequestForm(Integer current, Integer size) {
        this.setCurrent(current);
        this.setSize(size);
    }

    public void setCurrent(Integer current) {
        if (current == null || current < DEFAULT_PAGE)
            current = DEFAULT_PAGE;
        this.current = current;
    }

    public void setSize(Integer size) {
        if (size == null || size < 1)
            size = DEFAULT_SIZE;
        this.size = size;
    }

    public <T> Page<T> toPage() {
        Page<T> page = new Page<>(this.current, this.size, this.searchCount);
        if (!CollectionUtils.isEmpty(orders))
            orders.parallelStream().forEach(order -> {
                List<String> orderRule = Splitters.DOT.splitToList(order);
                if (!CollectionUtils.isEmpty(orderRule)) {
                    OrderItem item = new OrderItem();
                    item.setColumn(orderRule.get(0));
                    if (orderRule.size() > 1)
                        item.setAsc(!"desc".equalsIgnoreCase(orderRule.get(1)));
                    page.addOrder(item);
                }
            });
        return page;
    }
}
