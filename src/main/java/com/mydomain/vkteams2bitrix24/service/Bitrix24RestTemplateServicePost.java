package com.mydomain.vkteams2bitrix24.service;

import com.mydomain.vkteams2bitrix24.model.*;
import com.soc_apteka.vkteams2bitrix24.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Paths;
import java.util.*;

@Service
@Slf4j
public class Bitrix24RestTemplateServicePost {

    @Autowired
    private RestTemplate restTemplate = new RestTemplate();

    public String createTaskPost(String hook, Bitrix24TaskFields bitrix24TaskFields) {

        String returnData;

        // Создаем заголовки
        HttpHeaders headers = new HttpHeaders();

        // Указываем `content-type` заголовок
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Указываем `accept` заголовок
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));


        // Формируем параметры тела запроса
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> newTasks = new HashMap<>();
        Map<String, Object> taskCreateData = new HashMap<>();

        try {
            // Получаем обьект содержащий поля для формирования тела запроса
            String json = new ObjectMapper().writeValueAsString(bitrix24TaskFields);
            JsonNode rootNode = mapper.readTree(json);

            // Массив существующих ключей задачи
            String[] keys = {
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

            // Исключаем "пустые поля" и помещаем окончательный набор полей в Map taskCreateData
            for (String key : keys) {
                JsonNode value = rootNode.findValue(key);
                if (!value.asText().equals("0") && !value.asText().equals("\u0000")
                        && !value.asText().equals("null") && !value.asText().equals("0.0")) {
                    taskCreateData.put(key, value);
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // Формируем окончательную форму запроса создания новой задачи.
        newTasks.put("fields", taskCreateData);

        // Создаем запрос
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(newTasks, headers);

        // Отправляем запрос POST
        ResponseEntity<String> response = restTemplate.postForEntity(hook, entity, String.class);

        // Проверяем ответ
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

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
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
        ObjectMapper objectMapper = new ObjectMapper();
        jsonNode = objectMapper.readTree(jsonString);
        bitrix24FileInfo = objectMapper.readValue(jsonNode.get("result").toString(), Bitrix24FileInfo.class);
        return bitrix24FileInfo;
    }

    // Deserialize json to Bitrix24 task object
    public Bitrix24ResponseCreateTask taskMapJsonToObject(String jsonString) throws Exception {
        JsonNode jsonNode;
        ObjectMapper objectMapper = new ObjectMapper();
        jsonNode = objectMapper.readTree(jsonString);
        Bitrix24ResponseCreateTask bitrix24ResponseCreateTask = objectMapper.readValue(jsonNode.findValue("result").findValue("task").toString(), Bitrix24ResponseCreateTask.class);
        return bitrix24ResponseCreateTask;
    }

    // Add file on Bitrix disk to Task
    public String addFileToTask (String webhookUrl, int taskID, int fileID){
        Bitrix24addFileToTask bitrix24addFileToTask = Bitrix24addFileToTask.builder().taskId(taskID).fileId(fileID).build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Bitrix24addFileToTask> requestEntity = new HttpEntity<>(bitrix24addFileToTask, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(webhookUrl, requestEntity, String.class);
        log.info("Bitrix24 add file to task, status " + response.getStatusCode());
        return response.getBody();
    }
}