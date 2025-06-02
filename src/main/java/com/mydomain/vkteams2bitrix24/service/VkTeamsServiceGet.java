package com.mydomain.vkteams2bitrix24.service;

import com.mydomain.vkteams2bitrix24.model.VkEvents;
import com.mydomain.vkteams2bitrix24.model.submodel.VkFileObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class VkTeamsServiceGet {
    //GET Request
    @Autowired
    private RestTemplate restTemplate = new RestTemplate();;

    public VkFileObject getVkTeamsFileData(String command) {
        ResponseEntity<VkFileObject> response = restTemplate.getForEntity(command, VkFileObject.class);
        return response.getBody();
    }

    public VkEvents getVkTeamsEventData(String command) {
        ResponseEntity<VkEvents> response = restTemplate.getForEntity(command, VkEvents.class);
        return response.getBody();
    }

    public String getUrlVkTeamsEventData(String webhookUrl, String token, int lastEventId, int pollTime) {
        StringBuilder urlBuilder = new StringBuilder(webhookUrl + "?" + "token=" + token + "&" + "lastEventId=" + lastEventId + "&" + "pollTime=" + pollTime);
        return urlBuilder.toString();
    }

}
