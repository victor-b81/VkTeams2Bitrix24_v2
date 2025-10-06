package com.mydomain.vkteams2bitrix24;

import com.mydomain.vkteams2bitrix24.service.EventCheckService;
import com.mydomain.vkteams2bitrix24.service.EventProcessingServise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@EnableScheduling
@SpringBootApplication
@EnableAsync
public class VkTeams2Bitrix24 {

    @Autowired
    private EventCheckService eventCheckService;

    @Autowired
    private EventProcessingServise eventProcessingServise;

    public static void main(String[] args) {
        SpringApplication.run(VkTeams2Bitrix24.class, args);
    }

    @Scheduled(fixedDelay = 5000)
    public void checkEventsThread () {
        eventCheckService.getMessages();
    }

    @Scheduled(fixedDelay = 22000)
    public void checkTimeThread () {
        if (!eventCheckService.getVkTaskObjects().isEmpty()) eventProcessingServise.eventProcessing();
    }
}
