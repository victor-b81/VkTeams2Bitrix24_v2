package com.mydomain.vkteams2bitrix24.model.submodel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Bitrix24Person {
    private int id;
    private String name;
    private String link;
    private String icon;
    private String workPosition;
}
