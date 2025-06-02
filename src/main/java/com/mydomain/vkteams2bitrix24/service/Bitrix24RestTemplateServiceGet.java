package com.mydomain.vkteams2bitrix24.service;

import com.mydomain.vkteams2bitrix24.model.Bitrix24TasksResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class Bitrix24RestTemplateServiceGet {
//GET Request
    @Autowired
    private RestTemplate restTemplate = new RestTemplate();;

    public Bitrix24TasksResponse getTasksList(String command) {
        ResponseEntity<Bitrix24TasksResponse> response = restTemplate.getForEntity(command, Bitrix24TasksResponse.class);
        return response.getBody();
     }
}