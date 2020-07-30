package cn.monitor4all.springbootwebsocketdemo.controller;

import cn.monitor4all.springbootwebsocketdemo.model.ChatMessage;
import cn.monitor4all.springbootwebsocketdemo.redis.RedisListenerBean;
import cn.monitor4all.springbootwebsocketdemo.redis.RedisListenerHandle;
import cn.monitor4all.springbootwebsocketdemo.service.ChatService;
import cn.monitor4all.springbootwebsocketdemo.util.JsonUtil;
import cn.monitor4all.springbootwebsocketdemo.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

@Controller
public class ChatController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatController.class);


    @Value("${redis.channel.userStatus}")
    private String userStatus;

    @Autowired
    private ChatService chatService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RedisListenerBean redisListenerBean;
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        try {
            redisTemplate.convertAndSend(String.valueOf(chatMessage.getmId()), JsonUtil.parseObjToJson(chatMessage));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) throws ClassNotFoundException {
        RedisMessageListenerContainer container = SpringUtil.getBean(RedisMessageListenerContainer.class);

//        messageListenerAdapter.afterPropertiesSet();
        container.addMessageListener(RedisListenerBean.messageListenerAdapter, new PatternTopic(chatMessage.getmId().toString()));
        //container.addMessageListener(listenerAdapter, new PatternTopic(msgToMeeting));
        container.addMessageListener(RedisListenerBean.messageListenerAdapter, new PatternTopic(chatMessage.getmId()+".userStatus"));
        LOGGER.info("User added in Chatroom:" + chatMessage.getSender());
        try {
            headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
            headerAccessor.getSessionAttributes().put("mId", chatMessage.getmId());
            String adapter = chatMessage.getAdapter();
            if (!StringUtils.isEmpty(adapter)){
                headerAccessor.getSessionAttributes().put("adapter", chatMessage.getAdapter());
            }
            redisTemplate.opsForSet().add(chatMessage.getmId()+".onlineUsers", chatMessage.getSender());
            redisTemplate.convertAndSend(chatMessage.getmId().toString(), JsonUtil.parseObjToJson(chatMessage));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
