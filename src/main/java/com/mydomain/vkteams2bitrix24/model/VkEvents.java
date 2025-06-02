package com.mydomain.vkteams2bitrix24.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class VkEvents {
     List<VkEvent> events;
     String ok;
}
