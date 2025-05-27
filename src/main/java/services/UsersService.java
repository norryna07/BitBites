package main.java.services;

import main.java.repositories.UsersRepository;
import main.java.users.User;


public class UsersService extends BitBitesService<User>{

    public UsersService() {
        repository = UsersRepository.getInstance();
    }
}
