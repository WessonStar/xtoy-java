package dev.xtoy.common.dataset.page;

import java.util.List;

/**
 * 分页状态
 * @param rawPage 原始页数据
 * @param items 当前页数据项
 * @param nextPosition 下一页位置
 * @param <TPage> 数据页类型
 * @param <TItem> 数据项类型
 * @param <TPosition> 数据位置类型
 */
public record XPageState<TPage, TItem, TPosition>(
        TPage rawPage,
        List<TItem> items,
        TPosition nextPosition
) {
}
