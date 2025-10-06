package com.mydomain.vkteams2bitrix24.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class Bitrix24TaskFields {
    @JsonProperty("ID")
    int ID;
    @JsonProperty("MARK")
    char MARK;
    @JsonProperty("MULTITASK")
    char MULTITASK;
    @JsonProperty("NOT_VIEWED")
    char NOT_VIEWED;
    @JsonProperty("REPLICATE")
    char REPLICATE;
    @JsonProperty("ALLOW_CHANGE_DEADLINE")
    char ALLOW_CHANGE_DEADLINE;
    @JsonProperty("ALLOW_TIME_TRACKING")
    char ALLOW_TIME_TRACKING;
    @JsonProperty("TASK_CONTROL")
    char TASK_CONTROL;
    @JsonProperty("ADD_IN_REPORT")
    char ADD_IN_REPORT;
    @JsonProperty("FORKED_BY_TEMPLATE_ID")
    char FORKED_BY_TEMPLATE_ID;
    @JsonProperty("MATCH_WORK_TIME")
    char MATCH_WORK_TIME;
    @JsonProperty("SUBORDINATE")
    char SUBORDINATE;
    @JsonProperty("FAVORITE")
    char FAVORITE;
    @JsonProperty("IS_MUTED")
    char IS_MUTED;
    @JsonProperty("IS_PINNED")
    char IS_PINNED;
    @JsonProperty("IS_PINNED_IN_GROUP")
    char IS_PINNED_IN_GROUP;
    @JsonProperty("CREATED_DATE")
    Date CREATED_DATE;
    @JsonProperty("CHANGED_DATE")
    Date CHANGED_DATE;
    @JsonProperty("CLOSED_DATE")
    Date CLOSED_DATE;
    @JsonProperty("DATE_START")
    Date DATE_START;
    @JsonProperty("DEADLINE")
    String DEADLINE;
    @JsonProperty("START_DATE_PLAN")
    Date START_DATE_PLAN;
    @JsonProperty("END_DATE_PLAN")
    Date END_DATE_PLAN;
    @JsonProperty("EXCHANGE_MODIFIED")
    Date EXCHANGE_MODIFIED;
    @JsonProperty("VIEWED_DATE")
    Date VIEWED_DATE;
    @JsonProperty("SORTING")
    double SORTING;
    @JsonProperty("PARENT_ID")
    int PARENT_ID;
    @JsonProperty("PRIORITY")
    int PRIORITY;
    @JsonProperty("STATUS")
    int STATUS;
    @JsonProperty("GROUP_ID")
    int GROUP_ID;
    @JsonProperty("FLOW_ID")
    int FLOW_ID;
    @JsonProperty("STAGE_ID")
    int STAGE_ID;
    @JsonProperty("CREATED_BY")
    int CREATED_BY;
    @JsonProperty("RESPONSIBLE_ID")
    int RESPONSIBLE_ID;
    @JsonProperty("CHANGED_BY")
    int CHANGED_BY;
    @JsonProperty("STATUS_CHANGED_BY")
    int STATUS_CHANGED_BY;
    @JsonProperty("CLOSED_BY")
    int CLOSED_BY;
    @JsonProperty("COMMENTS_COUNT")
    int COMMENTS_COUNT;
    @JsonProperty("NEW_COMMENTS_COUNT")
    int NEW_COMMENTS_COUNT;
    @JsonProperty("TIME_ESTIMATE")
    int TIME_ESTIMATE;
    @JsonProperty("TIME_SPENT_IN_LOGS")
    int TIME_SPENT_IN_LOGS;
    @JsonProperty("FORUM_TOPIC_ID")
    int FORUM_TOPIC_ID;
    @JsonProperty("FORUM_ID")
    int FORUM_ID;
    @JsonProperty("EXCHANGE_ID")
    int EXCHANGE_ID;
    @JsonProperty("OUTLOOK_VERSION")
    int OUTLOOK_VERSION;
    @JsonProperty("DURATION_PLAN")
    int DURATION_PLAN;
    @JsonProperty("DURATION_FACT")
    int DURATION_FACT;
    @JsonProperty("SERVICE_COMMENTS_COUNT")
    int SERVICE_COMMENTS_COUNT;
    @JsonProperty("ACCOMPLICES")
    String[] ACCOMPLICES;
    @JsonProperty("TITLE")
    String TITLE;
    @JsonProperty("DESCRIPTION")
    String DESCRIPTION;
    @JsonProperty("GUID")
    String GUID;
    @JsonProperty("XML_ID")
    String XML_ID;
    @JsonProperty("SITE_ID")
    String SITE_ID;
    @JsonProperty("DURATION_TYPE")
    String DURATION_TYPE;
    @JsonProperty("UF_CRM_TASK")
    String UF_CRM_TASK;
    @JsonProperty("UF_TASK_WEBDAV_FILES")
    String UF_TASK_WEBDAV_FILES;
    @JsonProperty("UF_MAIL_MESSAGE")
    String UF_MAIL_MESSAGE;
    @JsonProperty("AUDITORS")
    String[] AUDITORS;
}
