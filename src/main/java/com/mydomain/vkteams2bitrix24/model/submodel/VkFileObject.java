package com.mydomain.vkteams2bitrix24.model.submodel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VkFileObject {
    String type;
    int size;
    String filename;
    String url;
}
