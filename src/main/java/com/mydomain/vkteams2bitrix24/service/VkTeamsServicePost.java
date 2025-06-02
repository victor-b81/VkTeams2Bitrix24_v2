package com.mydomain.vkteams2bitrix24.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class VkTeamsServicePost {

    @Autowired
    private RestTemplate restTemplate = new RestTemplate();;

    //Send message to chat Vk Teams
    public String getVkTeamsSendMsg(String webhookUrl, String token, String chatId, String textMsg) {
        StringBuilder urlBuilder = new StringBuilder(webhookUrl + "?" + "token=" + token + "&" + "chatId=" + chatId + "&" + "text=" + textMsg);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(headers);
        HttpEntity<String> response = restTemplate.exchange(urlBuilder.toString(), HttpMethod.POST, entity, String.class);
        log.info("Send message to chat :" + response.getBody());
        urlBuilder.delete(0, urlBuilder.length());
        return response.getBody();
    }
}
