package scs.ubbcluj.ro.utils.validators;

public interface Validator<T> {

    /**
     * @param entity object to be validated
     * @throws ValidationException if entity is not valid
     */
    void validate(T entity) throws ValidationException;
}
