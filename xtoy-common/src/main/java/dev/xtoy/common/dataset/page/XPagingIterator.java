package dev.xtoy.common.dataset.page;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * 分页迭代器
 * @param <TPage>     数据页类型
 * @param <TItem>     数据项类型
 * @param <TPosition> 数据位置类型
 */
public class XPagingIterator<TPage, TItem, TPosition> implements Iterator<XPageState<TPage, TItem, TPosition>> {

    private final XPagingCursor<TPage, TItem, TPosition> cursor;

    private XPageState<TPage, TItem, TPosition> buffer;
    private boolean loaded;

    public XPagingIterator(XPagingCursor<TPage, TItem, TPosition> cursor) {
        this.cursor = cursor;
    }

    @Override
    public boolean hasNext() {
        ensureLoaded();
        return buffer != null;
    }

    @Override
    public XPageState<TPage, TItem, TPosition> next() {
        ensureLoaded();

        if (buffer == null) {
            throw new NoSuchElementException();
        }

        XPageState<TPage, TItem, TPosition> res = buffer;
        buffer = null;
        loaded = false;
        return res;
    }

    private void ensureLoaded() {
        if (loaded) return;
        loaded = true;

        Optional<XPageState<TPage, TItem, TPosition>> next = cursor.next();
        if (next.isEmpty()) {
            buffer = null;
            return;
        }

        buffer = next.get();
    }
}
