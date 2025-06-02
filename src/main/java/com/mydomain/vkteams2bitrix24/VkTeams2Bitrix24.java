package com.mydomain.vkteams2bitrix24;

import com.mydomain.vkteams2bitrix24.model.Bitrix24TaskFields;
import com.mydomain.vkteams2bitrix24.model.VkEvent;
import com.mydomain.vkteams2bitrix24.model.VkTaskObject;
import com.mydomain.vkteams2bitrix24.service.Bitrix24RestTemplateServicePost;
import com.mydomain.vkteams2bitrix24.service.FileDownloader;
import com.mydomain.vkteams2bitrix24.service.VkTeamsServiceGet;
import com.mydomain.vkteams2bitrix24.service.VkTeamsServicePost;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@SpringBootApplication
public class VkTeams2Bitrix24 {

    public static void main(String[] args) {

        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        StringBuilder formattedDateTime = new StringBuilder();

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        Bitrix24RestTemplateServicePost restTemplateServicePost = new Bitrix24RestTemplateServicePost();
        VkTeamsServiceGet vkTeamsGetService = new VkTeamsServiceGet();
        VkTeamsServicePost vkTeamsServicePost = new VkTeamsServicePost();
        List<VkEvent> vkEvent = new ArrayList<>();

        int folderId = 14149; // ID Folder Bitrix24

        StringBuilder emploeerName = new StringBuilder(); // Name of sender
        StringBuilder vkTeamsGetFileJsonData = new StringBuilder(); // Vk File Json Data
        StringBuilder vkTeamsGetEventJsonData = new StringBuilder(); // Vk Event Json Data
        StringBuilder chatName = new StringBuilder(); // Vk chat name
        StringBuilder vkAttachment = new StringBuilder(); // Vk attachment file
        StringBuilder vkTextMessage = new StringBuilder(); // Vk all messages from one sender, of given time
        StringBuilder filePath = new StringBuilder(); // Download file Path
        StringBuilder bitrix24ResponseUploadFileJson = new StringBuilder(); // Bitrix24 response, of upload file
        StringBuilder bitrix24ResponseTaskInfoJson = new StringBuilder(); // Bitrix24 response, of create task
        StringBuilder vkMessageText = new StringBuilder(); // Vk chat, send text message

        List<VkTaskObject> vkTaskObjects = new ArrayList<>(); // List of events

        // Hook for Bitrix24
        String bitrix24AddTaskHook = "https://{Name_your_domain}.bitrix24.ru/rest/{id_user_get_hook}/{secret_key}/tasks.task.add.json";
        String bitrix24UploadFileHook = "https://{Name_your_domain}.bitrix24.ru/rest/{id_user_get_hook}/{secret_key}/disk.folder.uploadfile.json";
        String bitrix24AddFileToTaskHook = "https://{Name_your_domain}.bitrix24.ru/rest/{id_user_get_hook}/{secret_key}/tasks.task.files.attach.json";

        // Hook for VK teams
        String vkTeamsGetFileJsonDataHook = "https://myteam.mail.ru/bot/v1/files/getInfo";
        String vkTeamsGetEventHook = "https://myteam.mail.ru/bot/v1/events/get";
        String vkTeamsSendMessageHook = "https://myteam.mail.ru/bot/v1/messages/sendText";

        // Test chat bot Metabot
        String vkBotToken = "{your_token_vk_bot}";

        // Statuses vk chat events
        String deleteStatusYes = "Yes";
        String deleteStatusNo = "No";

        // Initial values
        int vkLastEventId = 0;
        int vkPollTime = 0;

        // New task processing flow
        Thread checkEventsThread = new Thread() {
            @SneakyThrows
            public void run() {
                Thread.currentThread().setName(Thread.currentThread().getName() + " - Check new events");
                log.info("Start throw..." + Thread.currentThread().getName());
                
                vkTeamsGetEventJsonData.append(vkTeamsGetService.getUrlVkTeamsEventData(vkTeamsGetEventHook, vkBotToken, vkLastEventId, vkPollTime));

                while (true) {
                    if (!vkTeamsGetService.getVkTeamsEventData(vkTeamsGetEventJsonData.toString()).getEvents().isEmpty()) {
                        log.info("Clear all variables");
                        // Clearing the object block
                        vkEvent.clear();
                        chatName.delete(0, chatName.length());
                        vkAttachment.delete(0, vkAttachment.length());
                        vkTextMessage.delete(0, vkTextMessage.length());
                        emploeerName.delete(0, emploeerName.length());
                        vkTeamsGetFileJsonData.delete(0, vkTeamsGetFileJsonData.length());

                        log.info("Get message data");
                        vkEvent.add(vkTeamsGetService.getVkTeamsEventData(String.valueOf(vkTeamsGetEventJsonData)).getEvents().getFirst());

                        log.info("Process message data");
                        vkEvent.forEach(ev -> {
                            if (ev.getType().equals("newMessage")) {
                                if (!ev.getPayload().getChat().getType().equals("private")) {
                                    if (ev.getPayload().getFrom().getUserId().contains("@{name_your_domain}")) {
                                        log.info("Own user!");

                                        log.info("Determine the value of the fields");
                                        if (ev.getPayload().getChat().getTitle() != null) {
                                            chatName.append(ev.getPayload().getChat().getTitle());
                                        } else {
                                            chatName.append("Private chat");
                                        }
                                        // Проверяем есть ли файлы
                                        if (ev.getPayload().getParts() != null) {
                                            vkTeamsGetFileJsonData.append(vkTeamsGetFileJsonDataHook + "?" + "vkBotToken=" + vkBotToken + "&fileId=" + ev.getPayload().getParts().getPayload().getFileId());
                                            vkAttachment.append(vkTeamsGetService.getVkTeamsFileData(vkTeamsGetFileJsonData.toString()).getUrl());
                                            if (ev.getPayload().getParts().getPayload().getCaption() == null) {
                                                vkTextMessage.append("Комментариев к файлам нет: ");
                                            } else {
                                                vkTextMessage.append("Комментарии к файлам: " + ev.getPayload().getParts().getPayload().getCaption());
                                            }
                                        } else {
                                            vkAttachment.append("null");
                                            vkTextMessage.append(ev.getPayload().getText());
                                        }

                                        emploeerName.append(ev.getPayload().getFrom().getLastName() + " " + ev.getPayload().getFrom().getFirstName());

                                        log.info("Placing the required data into the object");
                                        if (!vkTaskObjects.isEmpty()) {
                                            if (vkTaskObjects.stream().anyMatch(vkTaskObj -> vkTaskObj.getChatTitle().equals(String.valueOf(chatName)))) {

                                                vkTaskObjects.forEach(vkTaskObject -> {
                                                    if (vkTaskObject.getChatTitle().equals(String.valueOf(chatName))) {
                                                        if (vkTaskObject.getFromWho().equals(emploeerName.toString())) {
                                                            vkTaskObject.setTextMsg(vkTaskObject.getTextMsg() + "\n" + vkTextMessage);
                                                            vkTaskObject.setTimestamp(ev.getPayload().getTimestamp());
                                                            vkTaskObject.setAttachment(vkTaskObject.getAttachment() + "\n" + vkAttachment);
                                                        } else {
                                                            vkTaskObjects.add(new VkTaskObject(String.valueOf(chatName), ev.getPayload().getChat().getChatId(), ev.getPayload().getTimestamp(), emploeerName.toString(), ev.getPayload().getMsgId(), String.valueOf(vkTextMessage), String.valueOf(vkAttachment), deleteStatusNo));
                                                        }
                                                    }
                                                });

                                            } else {
                                                vkTaskObjects.add(new VkTaskObject(String.valueOf(chatName), ev.getPayload().getChat().getChatId(), ev.getPayload().getTimestamp(), emploeerName.toString(), ev.getPayload().getMsgId(), String.valueOf(vkTextMessage), String.valueOf(vkAttachment), deleteStatusNo));
                                            }

                                        } else {
                                            vkTaskObjects.add(new VkTaskObject(String.valueOf(chatName), ev.getPayload().getChat().getChatId(), ev.getPayload().getTimestamp(), emploeerName.toString(), ev.getPayload().getMsgId(), String.valueOf(vkTextMessage), String.valueOf(vkAttachment), deleteStatusNo));
                                        }
                                    } else {
                                        log.info("Not your own !");
                                        vkTeamsServicePost.getVkTeamsSendMsg(vkTeamsSendMessageHook, vkBotToken, vkTaskObjects.getFirst().getChatId(), "У вас нет доступа для обращения.");
                                    }
                                }
                            }
                        });
                        vkTeamsGetEventJsonData.delete(0, vkTeamsGetEventJsonData.length());
                        vkTeamsGetEventJsonData.append(vkTeamsGetService.getUrlVkTeamsEventData(vkTeamsGetEventHook, vkBotToken, vkEvent.getFirst().getEventId(), vkPollTime));
                    }
                    Thread.sleep(2000); // Pause to reduce CPU load
                }
            }
        };
        checkEventsThread.start();

        // Existing Tasks processing flow
        Thread checkTimeThread = new Thread() {
            @SneakyThrows
            public void run() {
                Thread.currentThread().setName(Thread.currentThread().getName() + " - Create new task on Bitrix24");
                log.info("Start throw..." + Thread.currentThread().getName());
                // Get home path, of project
                String projectPath = System.getProperty("user.dir");
                // Id of chats vk teams
                String vkChatIdOne = "{vk_teams_chat_id}@chat.agent";
                String vkChatIdTwo = "{vk_teams_chat_id}@chat.agent";
                // Initials value by default. Index of group. Get from Bitrix24
                int bitrix24GroupIndex = {Index_of_group};
                // Initials value by default. Index of responsible. Get from Bitrix24
                int bitrix24respID = {Index_of_responsible};
                String[] bitrix24Auditor = new String[]{"1"};  // Auditors list

                while (true) {
                    Thread.sleep(2000); // Pause to reduce CPU load
                    for (VkTaskObject vkTaskObject : vkTaskObjects) {
                        if ((System.currentTimeMillis() / 1000) - vkTaskObject.getTimestamp() > 600) {  // Checking for the existence of the last message from one user
                            if (!vkTaskObject.getReadyToDelete().equals("Yes")) {

                                if (vkTaskObject.getChatId().equals(vkChatIdTwo)) {
                                    bitrix24GroupIndex = {Index_of_group};
                                    bitrix24respID = {Index_of_responsible};
                                } else {
                                    bitrix24GroupIndex = {Index_of_group};
                                    bitrix24respID = {Index_of_responsible};
                                }

                                Bitrix24TaskFields bitrix24TaskFields = Bitrix24TaskFields.builder()
                                        .TITLE("Чат БОТ! : " + vkTaskObject.getFromWho())  // Set title of new task
                                        .GROUP_ID(bitrix24GroupIndex)
                                        .RESPONSIBLE_ID(bitrix24respID)
                                        .DESCRIPTION(vkTaskObject.getTextMsg())
                                        .ALLOW_CHANGE_DEADLINE('Y')  // Enable deadline
                                        .AUDITORS(bitrix24Auditor)
                                        .DEADLINE(simpleDateFormat.format(System.currentTimeMillis() + 5L * 24 * 60 * 60 * 1000))  // Set deadline from today.
                                        .build();

                                // Create Bitrix24 task
                                bitrix24ResponseTaskInfoJson.delete(0, bitrix24ResponseTaskInfoJson.length()); // Clearing the bitrix24ResponseTask, before assigning a new data bitrix24ResponseTask
                                bitrix24ResponseTaskInfoJson.append(restTemplateServicePost.createTaskPost(bitrix24AddTaskHook, bitrix24TaskFields));

                                // Upload files to Bitrix24
                                vkTaskObject.getAttachment().lines().toList().forEach(fileAttachmentList -> {
                                    int fileId = 0;
                                    int taskId = 0;
                                    formattedDateTime.delete(0, formattedDateTime.length()); // Clearing the date, before assigning a new date
                                    formattedDateTime.append(formatter1.format(new Date()));

                                    bitrix24ResponseUploadFileJson.delete(0, bitrix24ResponseUploadFileJson.length()); // Clearing the data, before assigning a new data

                                    if (!fileAttachmentList.equals("null")) {
                                        filePath.delete(0, filePath.length());
                                        try {
                                            filePath.append(projectPath + File.separator + "temp" + File.separator + formattedDateTime + "_" + Paths.get(new URL(fileAttachmentList).getPath()).getFileName().toString());
                                        } catch (MalformedURLException e) {
                                            log.info("Errore. MalformedURLException: " + fileAttachmentList);
                                            throw new RuntimeException(e);
                                        }

                                        FileDownloader.downloadFile(fileAttachmentList, filePath.toString());
                                        bitrix24ResponseUploadFileJson.append(restTemplateServicePost.uploadFile(bitrix24UploadFileHook, filePath.toString(), folderId));
                                        if (!bitrix24ResponseTaskInfoJson.toString().isEmpty()) {
                                            if (!bitrix24ResponseUploadFileJson.toString().contains("error")) {
                                                try {
                                                    taskId = restTemplateServicePost.taskMapJsonToObject(bitrix24ResponseTaskInfoJson.toString()).getId();
                                                    fileId = restTemplateServicePost.fileMapJsonToObject(bitrix24ResponseUploadFileJson.toString()).getID();
                                                    Thread.sleep(1500);
                                                    restTemplateServicePost.addFileToTask(bitrix24AddFileToTaskHook, taskId, fileId);
                                                } catch (Exception e) {
                                                    throw new RuntimeException(e);
                                                }
                                            }
                                        } else {
                                            log.info("value bitrix24ResponseTaskInfoJson is Empty!");
                                        }
                                    }
                                    // Delete local copies of files uploaded to Bitrix24
                                    File file = new File(String.valueOf(filePath));
                                    if (file.exists()) {
                                        file.delete();
                                    }
                                });
                                // Sending a message to the VK Teams chat
                                vkMessageText.delete(0, vkMessageText.length()); // Clearing the text msg, before assigning a new text msg
                                vkMessageText.append(vkTaskObject.getFromWho() + "\n" + "По вашему обращению создана задача \n" + vkTaskObject.getTextMsg());
                                vkTeamsServicePost.getVkTeamsSendMsg(vkTeamsSendMessageHook, vkBotToken, vkTaskObject.getChatId(), String.valueOf(vkMessageText));
                                vkTaskObject.setReadyToDelete(deleteStatusYes); // set status to dell ready
                            }
                        }
                    }
                    // Clear all messages who ready to dell
                    vkTaskObjects.removeIf(vkTaskObj -> vkTaskObj.getReadyToDelete().equals(deleteStatusYes));
                }
            }
        };
        checkTimeThread.start();
    }
}
