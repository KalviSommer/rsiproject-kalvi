package com.example.rsiadvisor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
public class RsiController {

    @Autowired
    private RsiService rsiService;


    //    http://localhost:8190/rsiadvisor/newuser
    @PostMapping("rsiadvisor/newuser") //ennem oli lesson5solution/account/{accountNr}
    public String createNewUser(@RequestBody UsersDto newUser) {

        return rsiService.createNewUser(newUser);
    }

    // http://localhost:8190/rsiadvisor/getuser/
    @GetMapping("rsiadvisor/getuser/{id}")
    public UsersDto getUser(@PathVariable("id") int id) {
        return rsiService.getUser(id);
    }

}
