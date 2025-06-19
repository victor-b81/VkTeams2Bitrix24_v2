package com.mydomain.vkteams2bitrix24.service;

import com.mydomain.vkteams2bitrix24.config.EventCheckServiceProperties;
import com.mydomain.vkteams2bitrix24.model.VkEvent;
import com.mydomain.vkteams2bitrix24.model.VkTaskObject;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ToString
@Data
@Slf4j
@Service
public class EventCheckService {

    private String vkTeamsGetEventHook;
    private String vkBotToken;
    private String vkTeamsSendMessageHook;
    // Initial values
    private int vkLastEventId = 0;
    private int vkPollTime = 0;
    // Statuses vk chat events
    private String deleteStatusYes = "Yes";
    private String deleteStatusNo = "No";

    List<VkEvent> vkEvent = new ArrayList<>();
    List<VkTaskObject> vkTaskObjects = Collections.synchronizedList(new ArrayList<>()); // List of events

    StringBuffer vkTeamsGetEventJsonData = new StringBuffer();  // Vk Event Json Data
    StringBuffer vkTeamsGetFileJsonDataHook = new StringBuffer(); // Vk First Part of hook get file data
    StringBuffer vkTeamsGetFileJsonData = new StringBuffer(); // Vk File Json Data
    StringBuffer vkChatName = new StringBuffer(); // Vk chat name
    StringBuffer vkAttachment = new StringBuffer(); // Vk attachment file
    StringBuffer vkTextMessage = new StringBuffer(); // Vk all messages from one sender, of given time
    StringBuffer vkFilePath = new StringBuffer(); // Download file Path
    StringBuffer vkEmploeerName = new StringBuffer(); // Name of sender

    private VkTeamsServiceGet vkTeamsServiceGet = new VkTeamsServiceGet();
    private VkTeamsServicePost vkTeamsServicePost = new VkTeamsServicePost();


    @Autowired
    public EventCheckService(EventCheckServiceProperties eventCheckServiceProperties) {
        vkTeamsGetEventHook = eventCheckServiceProperties.getVkTeamsBotHook() + eventCheckServiceProperties.getVkTeamsGetEventHook();
        vkBotToken = eventCheckServiceProperties.getVkBotToken();
        vkTeamsGetEventJsonData.append(vkTeamsServiceGet.getUrlVkTeamsEventData(vkTeamsGetEventHook, vkBotToken, vkLastEventId, vkPollTime));
        vkTeamsGetFileJsonDataHook.append(eventCheckServiceProperties.getVkTeamsBotHook() + eventCheckServiceProperties.getVkTeamsGetFileDataHook() + "?" + "token=" + vkBotToken);
        vkTeamsSendMessageHook = eventCheckServiceProperties.getVkTeamsBotHook() + eventCheckServiceProperties.getVkTeamsSendMessageHook();
    }

    @Async
    public void getMessages() {
        log.info("Start checking for new Event!");
        if (!vkTaskObjects.isEmpty()){
            vkTaskObjects.removeIf(vkTaskObject -> vkTaskObject.getReadyToDelete().equals(deleteStatusYes));
        }
        if (!vkTeamsServiceGet.getVkTeamsEventData(vkTeamsGetEventJsonData.toString()).getEvents().isEmpty()) {

            log.info("Get message data");
            vkEvent.add(vkTeamsServiceGet.getVkTeamsEventData(String.valueOf(vkTeamsGetEventJsonData)).getEvents().getFirst());

            log.info("Process message data");
            vkEvent.forEach(vkEvent -> {
                if (vkEvent.getType().equals("newMessage")) {
                    if (!vkEvent.getPayload().getChat().getType().equals("private")) {
                        if (vkEvent.getPayload().getFrom().getUserId().contains("@blik-simf.ru")) {
                            log.info("is a own user!");

                            log.info("Determine the value of the fields");
                            if (vkEvent.getPayload().getChat().getTitle() != null) {
                                vkChatName.append(vkEvent.getPayload().getChat().getTitle());
                            } else {
                                vkChatName.append("Private chat");
                            }
                            // Проверяем есть ли файлы
                            if (vkEvent.getPayload().getParts() != null) {
                                log.info("event have attachment! Getting data");
                                vkTeamsGetFileJsonData.append(vkTeamsGetFileJsonDataHook + "&fileId=" + vkEvent.getPayload().getParts().getPayload().getFileId());
                                vkAttachment.append(vkTeamsServiceGet.getVkTeamsFileData(vkTeamsGetFileJsonData.toString()).getUrl());
                                if (vkEvent.getPayload().getParts().getPayload().getCaption() == null) {
                                    log.info("the event has an attachment with out a comment!");
                                    vkTextMessage.append("Комментариев к файлам нет: ");
                                } else {
                                    log.info("the event has an attachment with a comment! adding comment!");
                                    vkTextMessage.append("Комментарии к файлам: " + vkEvent.getPayload().getParts().getPayload().getCaption());
                                }
                            } else {
                                log.info("event with out attachment! move forvard");
                                vkAttachment.append("null");
                                vkTextMessage.append(vkEvent.getPayload().getText());
                            }

                            vkEmploeerName.append(vkEvent.getPayload().getFrom().getLastName() + " " + vkEvent.getPayload().getFrom().getFirstName());

                            log.info("Placing the required data into the object");
                            if (!vkTaskObjects.isEmpty()) {
                                if (vkTaskObjects.stream().anyMatch(vkTaskObj -> vkTaskObj.getChatTitle().equals(String.valueOf(vkChatName)))) {

                                    vkTaskObjects.forEach(vkTaskObject -> {
                                        if (vkTaskObject.getChatTitle().equals(String.valueOf(vkChatName))) {
                                            if (vkTaskObject.getFromWho().equals(vkEmploeerName.toString())) {
                                                vkTaskObject.setTextMsg(vkTaskObject.getTextMsg() + "\n" + vkTextMessage);
                                                vkTaskObject.setTimestamp(vkEvent.getPayload().getTimestamp());
                                                vkTaskObject.setAttachment(vkTaskObject.getAttachment() + "\n" + vkAttachment);
                                            } else {
                                                vkTaskObjects.add(new VkTaskObject(String.valueOf(vkChatName), vkEvent.getPayload().getChat().getChatId(), vkEvent.getPayload().getTimestamp(), vkEmploeerName.toString(), vkEvent.getPayload().getMsgId(), String.valueOf(vkTextMessage), String.valueOf(vkAttachment), deleteStatusNo));
                                            }
                                        }
                                    });

                                } else {
                                    vkTaskObjects.add(new VkTaskObject(String.valueOf(vkChatName), vkEvent.getPayload().getChat().getChatId(), vkEvent.getPayload().getTimestamp(), vkEmploeerName.toString(), vkEvent.getPayload().getMsgId(), String.valueOf(vkTextMessage), String.valueOf(vkAttachment), deleteStatusNo));
                                }

                            } else {
                                vkTaskObjects.add(new VkTaskObject(String.valueOf(vkChatName), vkEvent.getPayload().getChat().getChatId(), vkEvent.getPayload().getTimestamp(), vkEmploeerName.toString(), vkEvent.getPayload().getMsgId(), String.valueOf(vkTextMessage), String.valueOf(vkAttachment), deleteStatusNo));
                            }
                        } else {
                            log.info("Not your own !");
                            vkTeamsServicePost.getVkTeamsSendMsg(vkTeamsSendMessageHook, vkBotToken, vkTaskObjects.getFirst().getChatId(), vkEvent.getPayload().getMsgId(),"У вас нет доступа для обращения.");
                        }
                    }
                }
            });
            log.info("Getting data about the last read event");
            vkTeamsGetEventJsonData.delete(0, vkTeamsGetEventJsonData.length());
            vkTeamsGetEventJsonData.append(vkTeamsServiceGet.getUrlVkTeamsEventData(vkTeamsGetEventHook, vkBotToken, vkEvent.getFirst().getEventId(), vkPollTime));

            // Clearing the object block
            log.info("Clear all variables");
            vkEvent.clear();
            vkChatName.delete(0, vkChatName.length());
            vkAttachment.delete(0, vkAttachment.length());
            vkTextMessage.delete(0, vkTextMessage.length());
            vkEmploeerName.delete(0, vkEmploeerName.length());
            vkTeamsGetFileJsonData.delete(0, vkTeamsGetFileJsonData.length());
            log.info("End checking for new Event!");
        } else {
            log.info("Nothing to do !");
        }
    }

    @Async
    public void checkToDelVkTaskObject(long timeStamp) {
        vkTaskObjects.forEach(taskObject -> {
            if (taskObject.getTimestamp() == timeStamp) {
                taskObject.setReadyToDelete(deleteStatusYes);
                log.info("Check to delete task by timestamp: " + taskObject);
            }
        });
    }
}
