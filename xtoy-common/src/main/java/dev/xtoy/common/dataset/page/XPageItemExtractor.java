package dev.xtoy.common.dataset.page;

import java.util.List;

/**
 * 分页数据项提取器
 * @param <TPage> 数据页类型
 * @param <TItem> 数据项类型
 */
@FunctionalInterface
public interface XPageItemExtractor<TPage, TItem> {

    /**
     * 从数据页中提取数据项
     * @param page 数据页
     * @return 数据项列表
     */
    List<TItem> extract(TPage page);
}
