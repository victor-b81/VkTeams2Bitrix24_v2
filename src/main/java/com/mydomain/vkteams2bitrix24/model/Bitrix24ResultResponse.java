package com.mydomain.vkteams2bitrix24.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Bitrix24ResultResponse {
    private List<Bitrix24TaskResponse> tasks;
}
