package com.mydomain.vkteams2bitrix24.model.submodel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Bitrix24Group {
    private int id;
    private String name;
    private boolean opened;
    private int memberCount;
    private String image;
    private Object additionalData;
}
