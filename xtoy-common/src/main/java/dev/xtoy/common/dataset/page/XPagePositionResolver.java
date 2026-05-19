package dev.xtoy.common.dataset.page;

/**
 * 分页位置解析器
 * @param <TPage> 数据页类型
 * @param <TPosition> 数据位置类型
 */
@FunctionalInterface
public interface XPagePositionResolver<TPage, TPosition> {

    /**
     * 下一个分页位置
     * @param page 当前页数据
     * @param currentPosition 当前分页位置
     * @return 下一个分页位置
     */
    TPosition next(TPage page, TPosition currentPosition);
}
