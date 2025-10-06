package com.mydomain.vkteams2bitrix24.model;

import com.mydomain.vkteams2bitrix24.model.submodel.Bitrix24UploadFileResponseResult;
import com.mydomain.vkteams2bitrix24.model.submodel.Bitrix24UploadFileResponseTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Bitrix24UploadFileResponse {
    public Bitrix24UploadFileResponseResult result;
    public Bitrix24UploadFileResponseTime time;
}
