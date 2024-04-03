package scs.ubbcluj.ro.repository.Paging;

import scs.ubbcluj.ro.repository.Repository;
import scs.ubbcluj.ro.domain.Entity;

public interface PagingRepository<ID, E extends Entity<ID>>
        extends Repository<ID, E> {

    Page<E> findAll(Pageable pageable);

}
