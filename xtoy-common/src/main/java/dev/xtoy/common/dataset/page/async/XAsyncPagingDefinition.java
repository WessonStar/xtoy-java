package dev.xtoy.common.dataset.page.async;

import dev.xtoy.common.dataset.page.*;

import java.util.Objects;

/**
 * 异步分页定义
 */
public class XAsyncPagingDefinition<TPage, TItem, TPosition> {

    private final XAsyncPageFetcher<TPage, TPosition> fetcher;
    private final XPageItemExtractor<TPage, TItem> extractor;
    private final XPagePositionResolver<TPage, TPosition> positionResolver;
    private final XCompletionPolicy<TPage, TItem> completionPolicy;

    private XAsyncPagingDefinition(XAsyncPagingDefinition.Builder<TPage, TItem, TPosition> builder) {
        this.fetcher = Objects.requireNonNull(
                builder.fetcher,
                "fetcher"
        );

        this.extractor = Objects.requireNonNull(
                builder.extractor,
                "extractor"
        );

        this.positionResolver = Objects.requireNonNull(
                builder.positionResolver,
                "positionResolver"
        );

        this.completionPolicy = Objects.requireNonNull(
                builder.completionPolicy,
                "completionPolicy"
        );
    }

    /**
     * 游标
     * @param startPosition 起始位置
     * @param pageSize 页大小
     * @param limit 数量限制
     * @return 异步分页游标
     */
    public XAsyncPagingCursor<TPage, TItem, TPosition> cursor(TPosition startPosition, int pageSize, int limit) {
        return new XAsyncPagingCursor<>(fetcher(), extractor(), positionResolver(), completionPolicy(),
                startPosition, pageSize, limit);
    }

    public XAsyncPageFetcher<TPage, TPosition> fetcher() {
        return fetcher;
    }

    public XPageItemExtractor<TPage, TItem> extractor() {
        return extractor;
    }

    public XPagePositionResolver<TPage, TPosition> positionResolver() {
        return positionResolver;
    }

    public XCompletionPolicy<TPage, TItem> completionPolicy() {
        return completionPolicy;
    }

    public static class Builder<TPage, TItem, TPosition> {

        private XAsyncPageFetcher<TPage, TPosition> fetcher;
        private XPageItemExtractor<TPage, TItem> extractor;
        private XPagePositionResolver<TPage, TPosition> positionResolver;
        private XCompletionPolicy<TPage, TItem> completionPolicy;

        public XAsyncPagingDefinition.Builder<TPage, TItem, TPosition> fetcher(XAsyncPageFetcher<TPage, TPosition> fetcher) {
            this.fetcher = fetcher;
            return this;
        }

        public XAsyncPagingDefinition.Builder<TPage, TItem, TPosition> extractor(XPageItemExtractor<TPage, TItem> extractor) {
            this.extractor = extractor;
            return this;
        }

        public XAsyncPagingDefinition.Builder<TPage, TItem, TPosition> positionResolver(XPagePositionResolver<TPage, TPosition> positionResolver) {
            this.positionResolver = positionResolver;
            return this;
        }

        public XAsyncPagingDefinition.Builder<TPage, TItem, TPosition> completionPolicy(XCompletionPolicy<TPage, TItem> completionPolicy) {
            this.completionPolicy = completionPolicy;
            return this;
        }

        public XAsyncPagingDefinition<TPage, TItem, TPosition> build() {
            return new XAsyncPagingDefinition<>(this);
        }
    }
}
