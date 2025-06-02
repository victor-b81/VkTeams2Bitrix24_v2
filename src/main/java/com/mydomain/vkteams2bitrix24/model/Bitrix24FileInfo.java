package com.mydomain.vkteams2bitrix24.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigInteger;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Bitrix24FileInfo {
    @JsonProperty("ID")
    int ID;
    @JsonProperty("NAME")
    String NAME;
    @JsonProperty("CODE")
    String CODE;
    @JsonProperty("STORAGE_ID")
    int STORAGE_ID;
    @JsonProperty("TYPE")
    String TYPE;
    @JsonProperty("PARENT_ID")
    int PARENT_ID;
    @JsonProperty("DELETED_TYPE")
    int DELETED_TYPE;
    @JsonProperty("GLOBAL_CONTENT_VERSION")
    int GLOBAL_CONTENT_VERSION;
    @JsonProperty("FILE_ID")
    int FILE_ID;
    @JsonProperty("SIZE")
    BigInteger SIZE;
    @JsonProperty("CREATE_TIME")
    Date CREATE_TIME;
    @JsonProperty("UPDATE_TIME")
    Date UPDATE_TIME;
    @JsonProperty("DELETE_TIME")
    Date DELETE_TIME;
    @JsonProperty("CREATED_BY")
    int CREATED_BY;
    @JsonProperty("UPDATED_BY")
    int UPDATED_BY;
    @JsonProperty("DELETED_BY")
    int DELETED_BY;
    @JsonProperty("DOWNLOAD_URL")
    String DOWNLOAD_URL;
    @JsonProperty("DETAIL_URL")
    String DETAIL_URL;

}
