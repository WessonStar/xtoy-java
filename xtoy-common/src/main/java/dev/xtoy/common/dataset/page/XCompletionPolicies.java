package dev.xtoy.common.dataset.page;

/**
 * 分页完成策略
 */
public final class XCompletionPolicies {
    private XCompletionPolicies() {
    }

    /**
     * 短页完成策略
     */
    public static <TPage, TItem> XCompletionPolicy<TPage, TItem> shortPage() {
        return (page, items, pageSize) ->
                items.size() < pageSize;
    }

    /**
     * 空页完成策略
     */
    public static <TPage, TItem> XCompletionPolicy<TPage, TItem> emptyPage() {
        return (page, items, pageSize) ->
                items.isEmpty();
    }
}
