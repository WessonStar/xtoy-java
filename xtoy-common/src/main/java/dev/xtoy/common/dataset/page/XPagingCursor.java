package dev.xtoy.common.dataset.page;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 分页游标
 * @param <TPage> 数据页类型
 * @param <TItem> 数据项类型
 * @param <TPosition> 数据位置类型
 */
public class XPagingCursor<TPage, TItem, TPosition> {
    private final XPageFetcher<TPage, TPosition> fetcher;
    private final XPageItemExtractor<TPage, TItem> extractor;
    private final XPagePositionResolver<TPage, TPosition> positionResolver;
    private final XCompletionPolicy<TPage, TItem> completionPolicy;

    private final int pageSize;
    private final int limit;

    private TPosition position;
    private int fetchedCount;
    private boolean finished;

    public XPagingCursor(
            XPageFetcher<TPage, TPosition> fetcher,
            XPageItemExtractor<TPage, TItem> extractor,
            XPagePositionResolver<TPage, TPosition> positionResolver,
            XCompletionPolicy<TPage, TItem> completionPolicy,
            TPosition startPosition,
            int pageSize,
            int limit
    ) {
        this.fetcher = Objects.requireNonNull(fetcher);
        this.extractor = Objects.requireNonNull(extractor);
        this.positionResolver = Objects.requireNonNull(positionResolver);
        this.completionPolicy = Objects.requireNonNull(completionPolicy);
        this.position = startPosition;
        this.pageSize = pageSize;
        this.limit = limit;
    }

    public Optional<XPageState<TPage, TItem, TPosition>> next() {
        if (finished || fetchedCount >= limit) {
            return Optional.empty();
        }

        TPosition currentPosition = position;
        int remaining = limit - fetchedCount;
        int requestSize = Math.min(pageSize, remaining);
        TPage rawPage = fetcher.fetch(currentPosition, requestSize);
        List<TItem> items = extractor.extract(rawPage);
        if (items == null) {
            items = List.of();
        }
        if (items.size() > remaining) {
            items = items.subList(0, remaining);
        }

        fetchedCount += items.size();
        boolean protocolFinished = completionPolicy.isFinished(rawPage, items, requestSize);
        boolean limitReached = fetchedCount >= limit;
        finished = protocolFinished || limitReached;

        TPosition nextPosition = null;
        if (!finished) {
            nextPosition = positionResolver.next(rawPage, currentPosition);
            if (Objects.equals(nextPosition, currentPosition)) {
                throw new IllegalStateException(
                        "Paging stuck: nextPosition not advancing. nextPosition=" + currentPosition
                );
            }
            position = nextPosition;
        }

        return Optional.of(new XPageState<>(rawPage, items, nextPosition));
    }
}
