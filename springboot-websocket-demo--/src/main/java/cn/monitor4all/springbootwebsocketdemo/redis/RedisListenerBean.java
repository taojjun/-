package cn.monitor4all.springbootwebsocketdemo.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

import java.net.Inet4Address;
import java.net.InetAddress;

/**
 * Redis订阅频道属性类
 * @author yangzhendong01
 */
@Component
public class RedisListenerBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisListenerBean.class);

    @Value("${server.port}")
    private String serverPort;

    public String msgToMeeting="";
    public static MessageListenerAdapter messageListenerAdapter;
    private String userStatus;
    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    /**
     * redis消息监听器容器
     * 可以添加多个监听不同话题的redis监听器，只需要把消息监听器和相应的消息订阅处理器绑定，该消息监听器
     * 通过反射技术调用消息订阅处理器的相关方法进行一些业务处理
     * @param connectionFactory
     * @param listenerAdapter
     * @return
     */
    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {

        container.setConnectionFactory(connectionFactory);
        // 监听msgToMeeting
        messageListenerAdapter = listenerAdapter;
        container.addMessageListener(listenerAdapter, new PatternTopic(msgToMeeting));
        container.addMessageListener(listenerAdapter, new PatternTopic(msgToMeeting+".userStatus"));
        LOGGER.info("Subscribed Redis channel: " + msgToMeeting);
        return container;
    }


    public void SubscribeChannel(){

    }
}
