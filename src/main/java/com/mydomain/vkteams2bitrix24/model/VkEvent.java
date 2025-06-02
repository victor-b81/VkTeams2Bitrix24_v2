package com.mydomain.vkteams2bitrix24.model;

import com.mydomain.vkteams2bitrix24.model.submodel.VkPayload;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class VkEvent {
    int eventId;
    VkPayload payload;
    String type;
}
