package dev.xtoy.common.dataset.page;

/**
 * 分页数据获取器
 * @param <TPage> 数据页类型
 * @param <TPosition> 数据位置类型
 */
@FunctionalInterface
public interface XPageFetcher<TPage, TPosition> {

    /**
     * 获取数据页
     * @param position 数据位置
     * @param pageSize 分页大小
     * @return 数据页
     */
    TPage fetch(TPosition position, int pageSize);
}
