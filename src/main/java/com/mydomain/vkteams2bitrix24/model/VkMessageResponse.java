package com.mydomain.vkteams2bitrix24.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class VkMessageResponse {
    private long msgId;
    private boolean ok;
}
