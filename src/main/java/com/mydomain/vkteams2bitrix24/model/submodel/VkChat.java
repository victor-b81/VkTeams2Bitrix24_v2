package com.mydomain.vkteams2bitrix24.model.submodel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class VkChat {
    String chatId;
    String type;
    String title;
}
