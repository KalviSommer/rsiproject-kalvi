package com.example.rsiadvisor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

    // http://localhost:8190/rsiadvisor/alertParams/
    @PostMapping("rsiadvisor/alertParams/{symbolId}/{userId}/{rsiFilter}/{rsiTimeframe}")
    public String alertParams(@PathVariable("symbolId") int symbolId, @PathVariable("userId") int userId,
                              @PathVariable ("rsiFilter") int rsiFilter, @PathVariable("rsiTimeframe") String rsiTimeframe) {
        return rsiService.alertParams(symbolId, userId, rsiFilter, rsiTimeframe);
    }


}
