package com.mydomain.vkteams2bitrix24.service;

import com.mydomain.vkteams2bitrix24.config.EventProcessingServiseProperties;
import com.mydomain.vkteams2bitrix24.model.Bitrix24TaskFields;
import com.mydomain.vkteams2bitrix24.model.VkMessageResponse;
import com.mydomain.vkteams2bitrix24.model.VkTaskObject;
import com.mydomain.vkteams2bitrix24.utils.FileDownloader;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@ToString
@Data
@Slf4j
@Service
public class EventProcessingServise {

    @Autowired
    private EventCheckService eventCheckService;

    // Get project path
    private String projectPath = System.getProperty("user.dir");
    private String bitrix24AddTaskHook;
    private String bitrix24UploadFileHook;
    private String bitrix24AddFileToTaskHook;
    private int bitrix24folderId;
    private int bitrix24IndexGroup;
    private int bitrix24RespID;
    private String vkChatAXO;
    private String vkChatIT;
    private final SimpleDateFormat vkTimeStampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    private final SimpleDateFormat vkTimeStampAddToFileFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    private StringBuffer addDateTimeToFile = new StringBuffer();
    private StringBuffer bitrix24ResponseUploadFileJson = new StringBuffer(); // Bitrix24 response, of upload file
    private StringBuffer bitrix24ResponseTaskInfoJson = new StringBuffer(); // Bitrix24 response, of create task
    private StringBuffer dowloadFilePath = new StringBuffer(); // Download file Path
    private StringBuffer vkMessageText = new StringBuffer(); // Vk chat, send text message

    private Bitrix24RestTemplateServicePost bitrix24RestTemplateServicePost = new Bitrix24RestTemplateServicePost();
    private VkTeamsServicePost vkTeamsServicePost = new VkTeamsServicePost();
    List<VkTaskObject> vkTaskObjects = new ArrayList<>(); // List of events
    Iterator<VkTaskObject> iteratorVkTaskObject;
    VkTaskObject vkTaskObject;
    private VkMessageResponse vkMessageResponse = new VkMessageResponse();


    @Autowired
    public EventProcessingServise(EventProcessingServiseProperties eventProcessingServiseProperties) {
        this.bitrix24AddTaskHook = eventProcessingServiseProperties.getBitrix24UserHook() + eventProcessingServiseProperties.getBitrix24AddTaskHook();
        this.bitrix24UploadFileHook = eventProcessingServiseProperties.getBitrix24UserHook() + eventProcessingServiseProperties.getBitrix24UploadFileHook();
        this.bitrix24AddFileToTaskHook = eventProcessingServiseProperties.getBitrix24UserHook() + eventProcessingServiseProperties.getBitrix24AddFileToTaskHook();
        this.bitrix24folderId = eventProcessingServiseProperties.getBitrix24folderId();
        this.bitrix24IndexGroup = eventProcessingServiseProperties.getBitrix24DefIndexGroup();
        this.bitrix24RespID = eventProcessingServiseProperties.getBitrix24RespID();
        this.vkChatAXO = eventProcessingServiseProperties.getVkChatAXO();
        this.vkChatIT = eventProcessingServiseProperties.getVkChatIT();
    }

    @Async
    public void eventProcessing() {
        log.info("Starting event processing...");
        vkTaskObjects = eventCheckService.getVkTaskObjects();
        iteratorVkTaskObject = vkTaskObjects.iterator();
        if (!vkTaskObjects.isEmpty()) {
            while (iteratorVkTaskObject.hasNext()) {
                vkTaskObject = iteratorVkTaskObject.next();
                if ((System.currentTimeMillis() / 1000) - vkTaskObject.getTimestamp() > 20) {
                    if (!vkTaskObject.getReadyToDelete().equals("Yes")) {

                        if (vkTaskObject.getChatId().equals(vkChatAXO)) {
                            bitrix24IndexGroup = 47;
                            bitrix24RespID = 49;
                        } else {
                            bitrix24IndexGroup = 45;
                            bitrix24RespID = 77;
                        }

                        Bitrix24TaskFields bitrix24TaskFields = Bitrix24TaskFields.builder()
                                .TITLE("Чат БОТ! : " + vkTaskObject.getFromWho())
                                .GROUP_ID(bitrix24IndexGroup)
                                .RESPONSIBLE_ID(bitrix24RespID)
                                .DESCRIPTION(vkTaskObject.getTextMsg())
                                .ALLOW_CHANGE_DEADLINE('Y')
                                .AUDITORS(new String[]{"1"})
                                .DEADLINE(vkTimeStampFormat.format(System.currentTimeMillis() + 5L * 24 * 60 * 60 * 1000))
                                .build();

                        // Create Bitrix24 task
                        bitrix24ResponseTaskInfoJson.delete(0, bitrix24ResponseTaskInfoJson.length()); // Clearing the bitrix24ResponseTask, before assigning a new data bitrix24ResponseTask
                        //bitrix24ResponseTaskInfoJson.append(bitrix24RestTemplateServicePost.createTaskPost(bitrix24AddTaskHook, bitrix24TaskFields));
                        log.info("Create task: " + bitrix24TaskFields);

                        // Upload files to Bitrix24
                        vkTaskObject.getAttachment().lines().toList().forEach(fileAttachmentList -> {
                            int fileId = 0;
                            int taskId = 0;
                            addDateTimeToFile.delete(0, addDateTimeToFile.length()); // Clearing the date, before assigning a new date
                            addDateTimeToFile.append(vkTimeStampAddToFileFormat.format(new Date()));

                            bitrix24ResponseUploadFileJson.delete(0, bitrix24ResponseUploadFileJson.length()); // Clearing the data, before assigning a new data

                            if (!fileAttachmentList.equals("null")) {
                                dowloadFilePath.delete(0, dowloadFilePath.length());
                                try {
                                    dowloadFilePath.append(projectPath + File.separator + "temp" + File.separator + bitrix24IndexGroup + "_" + addDateTimeToFile + "_" + Paths.get(new URL(fileAttachmentList).getPath()).getFileName().toString());
                                } catch (MalformedURLException e) {
                                    log.error("Errore. MalformedURLException: " + fileAttachmentList);
                                    throw new RuntimeException(e);
                                }

                                FileDownloader.downloadFile(fileAttachmentList, dowloadFilePath.toString());

                                bitrix24ResponseUploadFileJson.append(bitrix24RestTemplateServicePost.uploadFile(bitrix24UploadFileHook, dowloadFilePath.toString(), bitrix24folderId));
                                if (!bitrix24ResponseTaskInfoJson.toString().isEmpty()) {
                                    if (!bitrix24ResponseUploadFileJson.toString().contains("error")) {
                                        try {
                                            taskId = bitrix24RestTemplateServicePost.taskMapJsonToObject(bitrix24ResponseTaskInfoJson.toString()).getId();
                                            fileId = bitrix24RestTemplateServicePost.fileMapJsonToObject(bitrix24ResponseUploadFileJson.toString()).getID();
                                            bitrix24RestTemplateServicePost.addFileToTask(bitrix24AddFileToTaskHook, taskId, fileId);
                                            log.info("Add file to task: " + taskId + " " + fileId);
                                        } catch (Exception e) {
                                            log.error("Errore. MalformedURLException: " + bitrix24ResponseTaskInfoJson.toString());
                                            throw new RuntimeException(e);
                                        }
                                    }
                                } else {
                                    log.info("value bitrix24ResponseTaskInfoJson is Empty!");
                                }
                            }
                            // Delete local copies of files uploaded to Bitrix24
                            File file = new File(String.valueOf(dowloadFilePath));
                            if (file.exists()) {
                                file.delete();
                            }
                        });
                        // Sending a message to the VK Teams chat
                        vkMessageText.delete(0, vkMessageText.length()); // Clearing the text msg, before assigning a new text msg
                        vkMessageText.append(vkTaskObject.getFromWho()).append("\n").append("По вашему обращению создана задача \n").append(vkTaskObject.getTextMsg());
                        vkMessageResponse = vkTeamsServicePost.getVkTeamsSendMsg(eventCheckService.getVkTeamsSendMessageHook(), eventCheckService.getVkBotToken(), vkTaskObject.getChatId(), vkTaskObject.getVkMsgId(), String.valueOf(vkMessageText));
                        if (vkMessageResponse.isOk()){
                            log.info("VkTeams send message is ok: " + true);
                        } else {
                            log.error("VkTeams send message failed: " + false);
                        }
                        eventCheckService.checkToDelVkTaskObject(vkTaskObject.getTimestamp());
                    }
                }
            }
        }
    }
}
