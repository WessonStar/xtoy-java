package dev.xtoy.common.dataset.page;

import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 分页定义：包含分页迭代器所需的所有组件
 * @param <TPage> 数据页类型
 * @param <TItem> 数据项类型
 * @param <TPosition> 数据位置类型
 */
public class XPagingDefinition<TPage, TItem, TPosition> {

    private final XPageFetcher<TPage, TPosition> fetcher;
    private final XPageItemExtractor<TPage, TItem> extractor;
    private final XPagePositionResolver<TPage, TPosition> positionResolver;
    private final XCompletionPolicy<TPage, TItem> completionPolicy;

    private XPagingDefinition(Builder<TPage, TItem, TPosition> builder) {
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

    public static <TPage, TItem, TPosition> Builder<TPage, TItem, TPosition> builder() {
        return new Builder<>();
    }

    public Stream<TItem> itemStream(TPosition startPosition, int pageSize, int limit) {
        return stream(startPosition, pageSize, limit)
                .flatMap(x -> x.items().stream());
    }

    public Stream<XPageState<TPage, TItem, TPosition>> stream(TPosition startPosition, int pageSize, int limit) {
        return StreamSupport.stream(
                iterable(startPosition, pageSize, limit).spliterator(),
                false
        );
    }

    public Iterable<XPageState<TPage, TItem, TPosition>> iterable(TPosition startPosition, int pageSize, int limit) {
        return () -> iterator(startPosition, pageSize, limit);
    }

    public Iterator<XPageState<TPage, TItem, TPosition>> iterator(TPosition startPosition, int pageSize, int limit) {
        return new XPagingIterator<>(new XPagingCursor<>(fetcher(), extractor(), positionResolver(), completionPolicy(),
                startPosition, pageSize, limit));
    }

    public XPageFetcher<TPage, TPosition> fetcher() {
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

        private XPageFetcher<TPage, TPosition> fetcher;
        private XPageItemExtractor<TPage, TItem> extractor;
        private XPagePositionResolver<TPage, TPosition> positionResolver;
        private XCompletionPolicy<TPage, TItem> completionPolicy;

        public Builder<TPage, TItem, TPosition> fetcher(XPageFetcher<TPage, TPosition> fetcher) {
            this.fetcher = fetcher;
            return this;
        }

        public Builder<TPage, TItem, TPosition> extractor(XPageItemExtractor<TPage, TItem> extractor) {
            this.extractor = extractor;
            return this;
        }

        public Builder<TPage, TItem, TPosition> positionResolver(XPagePositionResolver<TPage, TPosition> positionResolver) {
            this.positionResolver = positionResolver;
            return this;
        }

        public Builder<TPage, TItem, TPosition> completionPolicy(XCompletionPolicy<TPage, TItem> completionPolicy) {
            this.completionPolicy = completionPolicy;
            return this;
        }

        public XPagingDefinition<TPage, TItem, TPosition> build() {
            return new XPagingDefinition<>(this);
        }
    }
}
