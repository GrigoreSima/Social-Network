package scs.ubbcluj.ro.utils.validators;
import scs.ubbcluj.ro.domain.User;

public class UserValidator implements Validator<User>{

    @Override
    public void validate(User entity) throws ValidationException {
        validateName(entity);
    }

    /**
     * @param entity user to validate
     * @throws ValidationException Name needs to be only with letters from English alphabet
     */
    private void validateName(User entity) throws ValidationException
    {
        entity.getName().forEach(x ->
        {
            if(!x.matches("[a-zA-Z]+"))
                throw new ValidationException("Username is not valid!");
        });
    }
}
