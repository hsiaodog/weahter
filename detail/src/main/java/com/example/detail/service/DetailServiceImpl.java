package com.example.detail.service;

import com.example.detail.config.EndpointConfig;
import com.example.detail.pojo.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class DetailServiceImpl implements DetailService{
    private final RestTemplate restTemplate;

    @Autowired
    public DetailServiceImpl(RestTemplate getDetailRestTemplate) {
        this.restTemplate = getDetailRestTemplate;
    }

    @Override
    @Retryable(include = IllegalAccessError.class)
    public List<Integer> findCityIdByName(List<String> city) {
        List<Integer> ans = new ArrayList<>();
        for (String s : city) {
            City[] cities = restTemplate.getForObject(EndpointConfig.queryWeatherByCity + s, City[].class);
            for(City c: cities) {
                System.out.println(c);
                if (c != null && c.getWoeid() != null) {
                    ans.add(c.getWoeid());
                }
            }
        }
        System.out.println(city);
        System.out.println(ans);
        System.out.println("detail");
        return ans;
    }
}
