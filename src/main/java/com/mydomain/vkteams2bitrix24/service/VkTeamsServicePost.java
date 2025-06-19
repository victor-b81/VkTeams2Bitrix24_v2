package com.mydomain.vkteams2bitrix24.service;

import com.mydomain.vkteams2bitrix24.utils.HttpHeadersUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class VkTeamsServicePost {

    private final RestTemplate restTemplate = new RestTemplate();;
    private final HttpHeaders headers = HttpHeadersUtil.createHeaders();

    //Send message to chat Vk Teams
    public String getVkTeamsSendMsg(String webhookUrl, String token, String chatId, long replyMsgId, String textMsg) {
        StringBuilder urlBuilder = new StringBuilder(webhookUrl + "?" + "token=" + token + "&" + "chatId=" + chatId  + "&" + "replyMsgId=" + replyMsgId + "&" + "text=" + textMsg);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        HttpEntity<String> response = restTemplate.exchange(urlBuilder.toString(), HttpMethod.POST, entity, String.class);
        log.info("Send message to chat :" + response.getBody());
        urlBuilder.delete(0, urlBuilder.length());
        return response.getBody();
    }
}
