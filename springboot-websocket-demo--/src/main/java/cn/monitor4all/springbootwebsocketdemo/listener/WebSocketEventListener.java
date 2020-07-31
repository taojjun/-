package cn.monitor4all.springbootwebsocketdemo.listener;

import cn.monitor4all.springbootwebsocketdemo.model.ChatMessage;
import cn.monitor4all.springbootwebsocketdemo.service.ChatService;
import cn.monitor4all.springbootwebsocketdemo.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.net.Inet4Address;
import java.net.InetAddress;


@Component
public class WebSocketEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Value("${server.port}")
    private String serverPort;


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        InetAddress localHost;
        try {
            localHost = Inet4Address.getLocalHost();
            LOGGER.info("Received a new web socket connection from:" + localHost.getHostAddress() + ":" + serverPort);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String sender = (String) headerAccessor.getSessionAttributes().get("sender");
        String mId ;
        String adapter;
        if(sender != null) {
            /**
             * 通过判断mid和adapter是否为空，来区分公共聊天还是私聊
             */
            if (null != headerAccessor.getSessionAttributes().get("adapter")){
                adapter = (String) headerAccessor.getSessionAttributes().get("adapter");
                LOGGER.info("User Disconnected : " + sender);
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(ChatMessage.MessageType.LEAVE);
                chatMessage.setSender(sender);
                try {
                    //redisTemplate.opsForSet().remove(adapter+".onlineUsers", username);
                    redisTemplate.convertAndSend(chatMessage.getSender()+"_to_"+chatMessage.getAdapter(), JsonUtil.parseObjToJson(chatMessage));
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            } else {
            mId = (String) headerAccessor.getSessionAttributes().get("mId").toString();
            LOGGER.info("User Disconnected : " + sender);
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setSender(sender);
            try {
                redisTemplate.opsForSet().remove(mId+".onlineUsers", sender);
                redisTemplate.convertAndSend(mId+".userStatus", JsonUtil.parseObjToJson(chatMessage));
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
            }
        }
    }
}
