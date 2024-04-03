package scs.ubbcluj.ro.service;

import scs.ubbcluj.ro.domain.User;
import scs.ubbcluj.ro.repository.DB.UserDBLoginRepository;

public class LoginService {

    private final UserDBLoginRepository repository;

    public LoginService(UserDBLoginRepository  repository) {
        this.repository = repository;
    }

    public Boolean login(String username, String password) {
        return repository.findOne(username, password).isPresent();
    }

    public User getOne(String username, String password)
    {
        return repository.findOne(username, password).orElse(null);
    }
}
