package com.mydomain.vkteams2bitrix24.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mydomain.vkteams2bitrix24.model.*;
import com.mydomain.vkteams2bitrix24.utils.EncodeFileToBase64;
import com.mydomain.vkteams2bitrix24.utils.HttpHeadersUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Paths;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class Bitrix24RestTemplateServicePost {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();
    private final HttpHeaders headers = HttpHeadersUtil.createHeaders();

    // Array of existing task keys
    private static final String[] keys = {
            "ID", "MARK", "MULTITASK", "NOT_VIEWED", "REPLICATE",
            "ALLOW_CHANGE_DEADLINE", "ALLOW_TIME_TRACKING", "TASK_CONTROL", "ADD_IN_REPORT", "FORKED_BY_TEMPLATE_ID",
            "MATCH_WORK_TIME", "SUBORDINATE", "FAVORITE", "IS_MUTED", "IS_PINNED",
            "IS_PINNED_IN_GROUP", "CREATED_DATE", "CHANGED_DATE", "CLOSED_DATE", "DATE_START",
            "DEADLINE", "START_DATE_PLAN", "END_DATE_PLAN", "EXCHANGE_MODIFIED", "VIEWED_DATE",
            "SORTING", "PARENT_ID", "PRIORITY", "STATUS", "GROUP_ID",
            "FLOW_ID", "STAGE_ID", "CREATED_BY", "RESPONSIBLE_ID", "CHANGED_BY",
            "STATUS_CHANGED_BY", "CLOSED_BY", "COMMENTS_COUNT", "NEW_COMMENTS_COUNT", "TIME_ESTIMATE",
            "TIME_SPENT_IN_LOGS", "FORUM_TOPIC_ID", "FORUM_ID", "EXCHANGE_ID", "OUTLOOK_VERSION",
            "DURATION_PLAN", "DURATION_FACT", "SERVICE_COMMENTS_COUNT", "ACCOMPLICES", "TITLE",
            "DESCRIPTION", "GUID", "XML_ID", "SITE_ID", "DURATION_TYPE",
            "UF_CRM_TASK", "UF_TASK_WEBDAV_FILES", "UF_MAIL_MESSAGE", "AUDITORS"
    };


    public String createTaskPost(String hook, Bitrix24TaskFields bitrix24TaskFields) {
        String returnData;

        // Set `accept` header
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // Forming the parameters of the request body
        Map<String, Object> newTasks = new HashMap<>();
        Map<String, Object> taskCreateData = new HashMap<>();

        try {
            // Get an object containing fields for forming the request body
            String json = new ObjectMapper().writeValueAsString(bitrix24TaskFields);
            JsonNode rootNode = objectMapper.readTree(json);

            // Eliminate "empty fields" and place the final set of fields in Map taskCreateData
            for (String key : keys) {
                JsonNode value = rootNode.findValue(key);
                if (!value.asText().equals("0") && !value.asText().equals("\u0000")
                        && !value.asText().equals("null") && !value.asText().equals("0.0")) {
                    taskCreateData.put(key, value);
                }
            }
        } catch (JsonProcessingException e) {
            log.error("Error while processing JSON: {}", e.getMessage(), e);
        }

        //
        //We are forming the final form of the request to create a new task.
        newTasks.put("fields", taskCreateData);

        // Create a request
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(newTasks, headers);

        // Sending a POST request
        ResponseEntity<String> response = restTemplate.postForEntity(hook, entity, String.class);

        // Checking the answer
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Request Successful. (Bitrix24, create task)");
            returnData = response.getBody();
        } else {
            log.info("Request Failed. (Bitrix24, create task)");
            returnData = response.getStatusCode().toString();
        }
        return returnData;
    }

    public String uploadFile(String webhookUrl, String filePath, int folderId) {

        StringBuilder fileName = new StringBuilder();
        StringBuilder fileBase64String = new StringBuilder();
        Map<String, String> data = new HashMap<>();
        List<String> fileContent = new ArrayList<>();

        data.clear();
        fileName.delete(0, fileName.length());
        fileBase64String.delete(0, fileBase64String.length());

        fileName.append(Paths.get(filePath).getFileName().toString());
        fileBase64String.append(EncodeFileToBase64.encodeFileToBase64(filePath));

        data.put("NAME", fileName.toString());
        fileContent.add(fileName.toString());
        fileContent.add(fileBase64String.toString());

        Bitrix24UploadFileModel bitrix24UploadFileModel = Bitrix24UploadFileModel.builder()
                .id(folderId).data(data).fileContent(fileContent).build();

        // Set `accept` header
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Bitrix24UploadFileModel> requestEntity = new HttpEntity<>(bitrix24UploadFileModel, headers);

        ResponseEntity<String> response = null;
        try {
            response = restTemplate.postForEntity(webhookUrl, requestEntity, String.class);
        } catch (Exception e) {
            log.error("Errore upload file: " + e.getMessage());
        }
        if (response.getStatusCode().isError()) {
            try {
                log.info("Let's try again");
                response = restTemplate.postForEntity(webhookUrl, requestEntity, String.class);
            } catch (Exception e) {
                log.error("Errore upload file: " + e.getMessage());
            }
        }
        log.info("Upload to Bitrix24 folder, status " + response.getStatusCode());
        return response.getBody();
    }

    // Deserialize json to Bitrix24 uplad file object
    public Bitrix24FileInfo fileMapJsonToObject(String jsonString) throws Exception {
        Bitrix24FileInfo bitrix24FileInfo;
        JsonNode jsonNode;
        jsonNode = objectMapper.readTree(jsonString);
        bitrix24FileInfo = objectMapper.readValue(jsonNode.get("result").toString(), Bitrix24FileInfo.class);
        return bitrix24FileInfo;
    }

    // Deserialize json to Bitrix24 task object
    public Bitrix24ResponseCreateTask taskMapJsonToObject(String jsonString) throws Exception {
        JsonNode jsonNode;
        jsonNode = objectMapper.readTree(jsonString);
        Bitrix24ResponseCreateTask bitrix24ResponseCreateTask = objectMapper.readValue(jsonNode.findValue("result").findValue("task").toString(), Bitrix24ResponseCreateTask.class);
        return bitrix24ResponseCreateTask;
    }

    // Add file on Bitrix disk to Task
    public String addFileToTask(String webhookUrl, int taskID, int fileID) {
        Bitrix24addFileToTask bitrix24addFileToTask = Bitrix24addFileToTask.builder().taskId(taskID).fileId(fileID).build();

        // Set `accept` header
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Bitrix24addFileToTask> requestEntity = new HttpEntity<>(bitrix24addFileToTask, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(webhookUrl, requestEntity, String.class);
        log.info("Bitrix24 add file to task, status " + response.getStatusCode());
        return response.getBody();
    }



}