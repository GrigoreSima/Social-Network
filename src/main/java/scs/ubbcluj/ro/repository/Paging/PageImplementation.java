package scs.ubbcluj.ro.repository.Paging;

import java.util.stream.Stream;

public class PageImplementation<T> implements Page<T> {
    private final Pageable pageable;
    private final Stream<T> content;

    public PageImplementation(Pageable pageable, Stream<T> content) {
        this.pageable = pageable;
        this.content = content;
    }

    @Override
    public Pageable getPageable() {
        return this.pageable;
    }

    @Override
    public Pageable nextPageable() {
        return new PageableImplementation(this.pageable.getPageNumber() + 1, this.pageable.getPageSize());
    }

    @Override
    public Stream<T> getContent() {
        return this.content;
    }
}
