package dev.xtoy.common.dataset.page;

import java.util.List;

/**
 * 分页完成策略
 * @param <TPage> 数据页类型
 * @param <TItem> 数据项类型
 */
@FunctionalInterface
public interface XCompletionPolicy <TPage, TItem> {

    /**
     * 是否完成
     * @param page 当前页数据
     * @param items 当前页数据项
     * @param pageSize 分页大小
     * @return 是否完成
     */
    boolean isFinished(TPage page, List<TItem> items, int pageSize);
}
