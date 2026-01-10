package dev.xtoy.common.dataset;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * 数据页列表迭代器
 * @param <TPage> 数据页类型
 * @param <TItem> 数据项类型
 */
public abstract class XPageListIterator<TPage, TItem> implements Iterator<List<TItem>> {
    private final int pageSize;
    private final int limit;

    private int pageIndex;
    private int fetchedCount = 0;
    private boolean finished = false;

    /**
     * 构造函数
     * @param startIndex 起始页索引
     * @param pageSize 页大小
     * @param limit 最大数据项数量
     */
    public XPageListIterator(int startIndex, int pageSize, int limit) {
        if (startIndex < 0) {
            throw new IllegalArgumentException("startIndex must be non-negative");
        }
        if (pageSize <= 0) {
            throw new IllegalArgumentException("pageSize must be positive");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("limit must be positive");
        }

        this.pageIndex = startIndex;
        this.pageSize = pageSize;
        this.limit = limit;
    }

    @Override
    public boolean hasNext() {
        return !finished && fetchedCount < limit;
    }

    @Override
    public List<TItem> next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        TPage dataPage = fetchPage(pageIndex, pageSize);
        List<TItem> dataList = getDataList(dataPage);
        if (dataList == null || dataList.isEmpty()) {
            finished = true;
            return Collections.emptyList();
        }

        int remaining = limit - fetchedCount;
        List<TItem> crtDataList = dataList.size() > remaining
                ? dataList.subList(0, remaining)
                : dataList;
        fetchedCount += crtDataList.size();
        if (dataList.size() < pageSize || fetchedCount >= limit) {
            finished = true;
        } else {
            this.pageIndex++;
        }

        return crtDataList;
    }

    /**
     * 获取数据页
     * @param pageIndex 页索引
     * @param pageSize 页大小
     * @return 数据页
     */
    protected abstract TPage fetchPage(int pageIndex, int pageSize);

    /**
     * 从数据页中提取数据列表
     * @param dataPage 数据页
     * @return 数据列表
     */
    protected abstract List<TItem> getDataList(TPage dataPage);
}
