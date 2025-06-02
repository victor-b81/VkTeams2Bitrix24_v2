package com.mydomain.vkteams2bitrix24.model;

import com.mydomain.vkteams2bitrix24.model.submodel.Bitrix24Group;
import com.mydomain.vkteams2bitrix24.model.submodel.Bitrix24Person;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class Bitrix24TaskResponse {
    private int id;
    private String title;
    private String description;
    private int status;
    private char mark;
    private int priority;
    private char multitask;
    private char notViewed;
    private char replicate;
    private int stageId;
    private int sprintId;
    private int createdBy;
    private Date createdDate;
    private int responsibleId;
    private int changedBy;
    private Date changedDate;
    private int statusChangedBy;
    private int closedBy;
    private Date closedDate;
    private Date activityDate;
    private Date dateStart;
    private Date deadline;
    private Date startDatePlan;
    private Date endDatePlan;
    private String guid;
    private String xmlId;
    private int commentsCount;
    private int serviceCommentsCount;
    private char allowChangeDeadline;
    private char allowTimeTracking;
    private char taskControl;
    private char addInReport;
    private String forkedByTemplateId;
    private int timeEstimate;
    private Date timeSpentInLogs;
    private char matchWorkTime;
    private int forumTopicId;
    private int forumId;
    private String siteId;
    private char subordinate;
    private Date exchangeModified;
    private int exchangeId;
    private int outlookVersion;
    private Date viewedDate;
    private double sorting;
    private int durationFact;
    private char isMuted;
    private char isPinned;
    private char isPinnedInGroup;
    private int flowId;
    private char descriptionInBbcode;
    private Date statusChangedDate;
    private int durationPlan;
    private char favorite;
    private String durationType;
    private char favoriteInGroup;
    private int groupId;
    private String[] auditors;
    private int[] accomplices;
    private int newCommentsCount;
    private Bitrix24Group bitrix24Group;
    private Bitrix24Person creator;
    private Bitrix24Person responsible;
    private int[] accomplicesData;
    private JsonNode auditorsData;
    private int subStatus;
}
