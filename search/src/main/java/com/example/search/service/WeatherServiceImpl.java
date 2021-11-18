package com.example.search.service;


import com.example.search.config.EndpointConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class WeatherServiceImpl implements WeatherService{

    private final RestTemplate restTemplate;

    @Autowired
    public WeatherServiceImpl(RestTemplate getRestTemplate) {
        this.restTemplate = getRestTemplate;
    }


    //do retry while failed
    @Override
    @Retryable(include = IllegalAccessError.class)
    public List<Map<String, Map>> findCityIdByName(List<String> city) {
        String allCities = String.join(",", city);
        List<Map<String, Map>> ans = new ArrayList<>();
        List<Integer> cityIds = restTemplate.getForObject(EndpointConfig.queryDetail+ "/nametoid?city=" + allCities, ArrayList.class);
//        ForkJoinPool pool = new ForkJoinPool();
//        CompletableFuture<Map<String, Map>> future = CompletableFuture.supplyAsync(()-> {
//           try{
//               return restTemplate.getForObject(EndpointConfig.queryWeatherById + , HashMap.class);
//           }catch (Exception e) {
//
//           }
//        });

        for (Integer c : cityIds) {
            if (c != null) {
                Map<String, Map> weatherById = findCityNameById(c);
                ans.add(weatherById);
            }
        }
//        System.out.println(allCities);
//        System.out.println(city);
//        System.out.println(ans);
//        System.out.println("weather");

        return ans;
    }

    @Override
    public Map<String, Map> findCityNameById(int id) {
        Map<String, Map> ans = restTemplate.getForObject(EndpointConfig.queryWeatherById + id, HashMap.class);
        return ans;
    }
}


/**
 *  -> gateway -> eureka
 *       |
 *   load balance
 *       |
 *   weather-search -> hystrix(thread pool) -> 3rd party weather api
 *
 *
 *  circuit breaker(hystrix)
 * *  * *  * *  * *  * *  * *  * *  * *  * *  * *  * *  * *  * *  * *
 *   weather-search service should get city id from detail service
 *   and use multi-threading to query city's weather details
 *
 *   gateway
 *     |
 *  weather-service -> (id1, id2, id3 + multi-thread)-> 3rd party api(id <-> weather)
 *    |
 *  detail-service -> 3rd party api (city <-> id)
 *
 *  failed situations:
 *      1. 3rd party api timeout -> retry + hystrix
 *      2. 3rd party api available time / rate limit
 *      3. security verification
 *  response
 *      1. no id -> error / empty
 *      2. large response -> pagination / file download (link / email)
 *  performance
 *      1. cache / db
 *
 *   gateway
 *     |
 *  weather-service -> cache(city - id - weather) (LFU)
 *    |
 *   DB (city - id - weather) <-> service <->  message queue  <-> scheduler <-> 3rd party api(city - id)
 *                                                                  |
 *                                                         update id - weather every 30 min
 *                                                         update city - id relation once per day
 *
 *  homework :
 *      deadline -> Wednesday midnight
 *      1. update detail service
 *          a. send request to 3rd party api -> get id by city
 *      2. update search service
 *          a. add ThreadPool
 *          b. send request to detail service -> get id by city
 *          c. use CompletableFuture send request to 3rd party api -> get weather by ids
 *          d. add retry feature
 */