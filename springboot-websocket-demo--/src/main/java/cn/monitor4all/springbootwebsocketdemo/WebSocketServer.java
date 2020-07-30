package cn.monitor4all.springbootwebsocketdemo;


import com.alibaba.fastjson.JSON;

import io.netty.handler.timeout.IdleStateEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.yeauty.annotation.*;
import org.yeauty.pojo.Session;

import javax.xml.soap.MessageFactory;
import java.io.IOException;
import java.util.Date;
@ServerEndpoint(path = "/ws/{meetingId}/{senderId}", host = "${ws.host}", port = "${ws.port}")
public class WebSocketServer {

    @BeforeHandshake
    public void handshake(Session session) {
        session.setSubprotocols("stomp");
        //token验证
    }
    /**
     * 入会
     *
     * @param session
     * @param meetingId 会议室id
     * @param senderId  用户ID
     */
    @OnOpen
    public void onOpen(Session session, @PathVariable("meetingId") String meetingId,
                       @PathVariable("senderId") String senderId,@RequestParam("meetingPwd") String meetingPwd) {

    }

    @OnClose
    public void onClose(Session session, @PathVariable("meetingId") String meetingId, @PathVariable("senderId") String senderId) throws IOException {

    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }

//    @OnMessage
//    public void onMessage(Session session, String message) {
//
//    }

    @OnBinary
    public void onBinary(Session session, byte[] bytes) {

    }

    @OnEvent
    public void onEvent(Session session, Object evt) {}


}
