package scs.ubbcluj.ro.repository;

import scs.ubbcluj.ro.domain.Entity;
import scs.ubbcluj.ro.utils.validators.ValidationException;
import scs.ubbcluj.ro.utils.validators.Validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {
    Map<ID,E> entities;
    Validator<E> validator;

    public InMemoryRepository(Validator<E> validator) {
        entities = new HashMap<>();
        this.validator = validator;
    }

    @Override
    public Optional<E> findOne(ID id) throws IllegalArgumentException{
        if (id==null)
            throw new IllegalArgumentException("ID must be not null !");
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public Optional<E> save(E entity) throws IllegalArgumentException, ValidationException {
        if (entity==null)
            throw new IllegalArgumentException("Entity must be not null !");

        validator.validate(entity);

        var last_entity = entities.get(entity.getID());

        if(last_entity != null) {
            return Optional.of(last_entity);
        }

        entities.put(entity.getID(), entity);
        return Optional.empty();
    }

    @Override
    public Optional<E> delete(ID id) {
        return Optional.ofNullable(entities.remove(id));
    }

    @Override
    public Optional<E> update(E entity) throws IllegalArgumentException, ValidationException {
        if(entity == null)
            throw new IllegalArgumentException("Entity must be not null !");

        validator.validate(entity);

        var last_entity = entities.get(entity.getID());

        if(last_entity != null) {
            entities.replace(entity.getID(), entity);
            return Optional.of(last_entity);
        }

        entities.put(entity.getID(), entity);
        return Optional.empty();
    }
}
