package com.mydomain.vkteams2bitrix24.model.submodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class Bitrix24UploadFileResponseResult {
    @JsonProperty("ID")
    public int iD;
    @JsonProperty("NAME")
    public String nAME;
    @JsonProperty("CODE")
    public Object cODE;
    @JsonProperty("STORAGE_ID")
    public String sTORAGE_ID;
    @JsonProperty("TYPE")
    public String tYPE;
    @JsonProperty("PARENT_ID")
    public String pARENT_ID;
    @JsonProperty("DELETED_TYPE")
    public int dELETED_TYPE;
    @JsonProperty("GLOBAL_CONTENT_VERSION")
    public int gLOBAL_CONTENT_VERSION;
    @JsonProperty("FILE_ID")
    public int fILE_ID;
    @JsonProperty("SIZE")
    public String sIZE;
    @JsonProperty("CREATE_TIME")
    public Date cREATE_TIME;
    @JsonProperty("UPDATE_TIME")
    public Date uPDATE_TIME;
    @JsonProperty("DELETE_TIME")
    public Object dELETE_TIME;
    @JsonProperty("CREATED_BY")
    public String cREATED_BY;
    @JsonProperty("UPDATED_BY")
    public String uPDATED_BY;
    @JsonProperty("DELETED_BY")
    public Object dELETED_BY;
    @JsonProperty("DOWNLOAD_URL")
    public String dOWNLOAD_URL;
    @JsonProperty("DETAIL_URL")
    public String dETAIL_URL;
}
