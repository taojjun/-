# chat-cluster
集群聊天室
在大佬的基础上实现多个会议室聊天，正在开发私聊功能。。。
遇到的问题：私聊时，频道规则为：sendid_to_adaptid；发送者发送时，主动订阅相反的频道，即adaptid_to_sendid;但是怎么才能让消息接收者收到消息，或者订阅发送者发布的频道
localhost:0>PUBLISH BB_to_AA '{"type":"CHAT","content":"你好啊AA，我是BB", "sender":"BB", "adapter":"AA", "mId":"null"}'
