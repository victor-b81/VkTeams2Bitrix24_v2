package com.mydomain.vkteams2bitrix24.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Bitrix24ResponseCreateTask {
    int id;
    int parentId;
    String title;
    String description;
    String mark;
    char priority;
    char multitask;
    char notViewed;
    char replicate;
    int stageId;
    int sprintId;
    int backlogId;
    int createdBy;
    Date createdDate;
    int responsibleId;
    int changedBy;
    Date changedDate;
    int statusChangedBy;
    int closedBy;
    Date closedDate;
    Date activityDate;
    Date dateStart;
    Date deadline;
    Date startDatePlan;
    Date endDatePlan;
    String guid;
    String xmlId;
    int commentsCount;
    int serviceCommentsCount;
    char allowChangeDeadline;
    char allowTimeTracking;
    char taskControl;
    char addInReport;
    int forkedByTemplateId;
    int timeEstimate;
    int timeSpentInLogs;
    char matchWorkTime;
    int forumTopicId;
    int forumId;
    String siteId;
    char subordinate;
    Date exchangeModified;
    int exchangeId;
    int outlookVersion;
    Date viewedDate;
    double sorting;
    int durationFact;
    char isMuted;
    char isPinned;
    char isPinnedInGroup;
    int flowId;
    char descriptionInBbcode;
    char status;
    Date statusChangedDate;
    int durationPlan;
    String durationType;
    char favorite;
    int groupId;
    String[] auditors;
    int[] accomplices;
    String[] checklist;
    JsonNode group;
    JsonNode creator;
    JsonNode responsible;
    JsonNode accomplicesData;
    JsonNode auditorsData;
    int newCommentsCount;
    JsonNode action;
    JsonNode checkListTree;
    Boolean checkListCanAdd;
}
