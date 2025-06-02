package com.mydomain.vkteams2bitrix24.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class Bitrix24addFileToTask {
    int taskId;
    int fileId;
}
