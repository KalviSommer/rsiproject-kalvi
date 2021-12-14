package com.example.rsiadvisor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.List;


@RestController
public class RsiController {

    @Autowired
    private RsiService rsiService;


    //    http://localhost:8190/rsiadvisor/newuser
    @PostMapping("rsiadvisor/newuser") //ennem oli lesson5solution/account/{accountNr}
    public Integer createNewUser(@RequestBody UsersDto newUser) throws MessagingException {

        return rsiService.createNewUser(newUser);
    }

    // http://localhost:8190/rsiadvisor/getuser/
    @GetMapping("rsiadvisor/getuser/{id}")
    public UsersDto getUser(@PathVariable("id") int id) {
        return rsiService.getUser(id);
    }

    // http://localhost:8190/rsiadvisor/setAlert/
    @PostMapping("rsiadvisor/setAlert/{symbolId}/{userId}/{rsiFilter}/{rsiTimeframe}")
    public void setAlert(@PathVariable("symbolId") int symbolId, @PathVariable("userId") int userId,
                              @PathVariable ("rsiFilter") int rsiFilter, @PathVariable("rsiTimeframe") String rsiTimeframe) throws MessagingException {
        rsiService.setAlert(symbolId, userId, rsiFilter, rsiTimeframe);
    }

    // http://localhost:8190/rsiadvisor/alertlist/
    @GetMapping("rsiadvisor/alertlist/{userId}")
    public List<AlertDto> alertList(@PathVariable("userId") int userId) {
        return rsiService.alertList(userId);
    }

    // http://localhost:8090/rsiadvisor/deletealert/
    @DeleteMapping("rsiadvisor/deletealert/{id}")
    public void deleteAlert(@PathVariable("id") int n) {
        rsiService.deleteAlert(n);
    }



}
