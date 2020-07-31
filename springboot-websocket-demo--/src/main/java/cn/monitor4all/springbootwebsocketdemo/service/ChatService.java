package cn.monitor4all.springbootwebsocketdemo.service;

import cn.monitor4all.springbootwebsocketdemo.model.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatService.class);

    @Autowired
    private SimpMessageSendingOperations simpMessageSendingOperations;

    public void sendMsg(@Payload ChatMessage chatMessage) {

        /**
         * 判断是私聊还是公共聊天
         */
        if (null != chatMessage.getAdapter()){
            LOGGER.info("Send msg by simpMessageSendingOperations to user:" + chatMessage.toString());
            simpMessageSendingOperations.convertAndSendToUser(chatMessage.getAdapter(),"/user/"+chatMessage.getSender()+"_to_"+chatMessage.getAdapter(), chatMessage);
        }else {
            LOGGER.info("Send msg by simpMessageSendingOperations to user:" + chatMessage.toString());
            simpMessageSendingOperations.convertAndSend("/topic/"+chatMessage.getmId(), chatMessage);

        }
    }

    public void alertUserStatus(@Payload ChatMessage chatMessage) {
        LOGGER.info("Alert user online by simpMessageSendingOperations:" + chatMessage.toString());
        simpMessageSendingOperations.convertAndSend("/topic/public", chatMessage);
    }
}
