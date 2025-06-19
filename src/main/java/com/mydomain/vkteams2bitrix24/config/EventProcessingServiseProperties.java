package com.mydomain.vkteams2bitrix24.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ToString
@Component
@ConfigurationProperties(prefix = "eventprocessingservisecfg")
public class EventProcessingServiseProperties {
    private int bitrix24folderId;
    private String bitrix24UserHook;
    private String bitrix24AddTaskHook;
    private String bitrix24UploadFileHook;
    private String bitrix24AddFileToTaskHook;
    private int bitrix24DefIndexGroup;
    private int bitrix24RespID;
    private String vkChatAXO;
    private String vkChatIT;
}
