package com.example.detail.controller;

import com.example.detail.service.DetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class DetailController {

    private final DetailService detailService;

    @Value("${server.port}")
    private int serverPort;


    @Autowired
    public DetailController(DetailService detailService) {
        this.detailService = detailService;
    }

//    @GetMapping("/port")
//    public ResponseEntity<?> getDetails() {
//        return new ResponseEntity<>("detail service port is " + serverPort, HttpStatus.OK);
//    }


    @GetMapping("/nametoid")
    public ResponseEntity<?> queryWeatherByCity(@RequestParam(required = true) List<String> city) {
        System.out.println("received request");
        return new ResponseEntity<>(detailService.findCityIdByName(city), HttpStatus.OK);
    }


    @GetMapping("/port")
    public ResponseEntity<?> queryWeatherByCity() {
        return new ResponseEntity<>("detail service + " + serverPort, HttpStatus.OK);

    }


}
