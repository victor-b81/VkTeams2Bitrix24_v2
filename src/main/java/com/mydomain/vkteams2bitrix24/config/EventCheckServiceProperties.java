package com.mydomain.vkteams2bitrix24.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ToString
@Component
@ConfigurationProperties(prefix = "eventcheckservicecfg")
public class EventCheckServiceProperties {
    private String vkTeamsBotHook;
    private String vkTeamsGetFileDataHook;
    private String vkTeamsGetEventHook;
    private String vkTeamsSendMessageHook;
    private String vkBotToken;
}
