package com.mydomain.vkteams2bitrix24.model.submodel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class VkPayload {
    VkChat chat;
    VkFrom from;
    long msgId;
    String text;
    long timestamp;
    List<VkParts> parts;

    public VkParts getParts() {
        if (this.parts == null){
            return null;
        } else {
            return parts.getFirst();
        }
    }
}
