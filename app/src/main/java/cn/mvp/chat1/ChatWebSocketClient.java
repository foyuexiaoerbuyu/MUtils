package cn.mvp.chat1;


/**
 * https://blog.huangyuanlove.com/2017/12/25/Android%E4%B8%AD%E4%BD%BF%E7%94%A8WebSocket/
 * implementation 'org.java-websocket:Java-WebSocket:1.5.3'
 * <dependency>
 * <groupId>org.java-websocket</groupId>
 * <artifactId>Java-WebSocket</artifactId>
 * <version>1.5.3</version>
 * </dependency>
 */
public class ChatWebSocketClient {
    private static ChatWebSocketClient instance;

    private ChatWebSocketClient() {

    }

    public static ChatWebSocketClient getInstance() {
        if (instance == null) {
            synchronized (ChatWebSocketClient.class) {
                if (instance == null) {
                    instance = new ChatWebSocketClient();
                }
            }
        }
        return instance;
    }

}