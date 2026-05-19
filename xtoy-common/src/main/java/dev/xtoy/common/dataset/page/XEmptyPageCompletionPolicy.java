package dev.xtoy.common.dataset.page;

import java.util.List;

/**
 * 空页完成策略：当前页数据项数量为 0 时，认为已经完成
 * @param <TPage> 数据页类型
 * @param <TItem> 数据项类型
 */
public class XEmptyPageCompletionPolicy<TPage, TItem> implements XCompletionPolicy<TPage, TItem> {

    @Override
    public boolean isFinished(TPage page, List<TItem> items, int pageSize) {
        return items.isEmpty();
    }
}
