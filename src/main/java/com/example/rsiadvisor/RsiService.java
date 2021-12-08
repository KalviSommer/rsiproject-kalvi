package com.example.rsiadvisor;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RsiService {

    @Autowired
    private RsiRepository rsiRepository;

    public String createNewUser(UsersDto newUser) { //ennem public String createAccount(@PathVariable("accountNr") String accountNr )
        Integer a = rsiRepository.createNewUser(newUser);
        return "New user is created and the user id is: " + a;
    }
}
