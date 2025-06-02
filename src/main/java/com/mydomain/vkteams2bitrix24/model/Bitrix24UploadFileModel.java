package com.mydomain.vkteams2bitrix24.model;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class Bitrix24UploadFileModel {
    int id;
    Map<String, String> data;
    List<String> fileContent;
}

