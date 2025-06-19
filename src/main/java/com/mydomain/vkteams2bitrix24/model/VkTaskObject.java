package com.mydomain.vkteams2bitrix24.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class VkTaskObject {
    String chatTitle;
    String chatId;
    long timestamp;
    String fromWho;
    long vkMsgId;
    String textMsg;
    String attachment;
    String readyToDelete;
}
