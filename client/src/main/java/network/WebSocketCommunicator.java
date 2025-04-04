package network;

import com.google.gson.Gson;
import ui.Client;
import ui.ServerMessageObserver;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;

public class WebSocketCommunicator extends Endpoint {
    private Session session;
    //private ServerMessageObserver observer;

    public WebSocketCommunicator(String serverUrl, ServerMessageObserver observer) throws Exception {
        serverUrl = serverUrl.replace("http", "ws");
        URI uri = new URI(serverUrl + "ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
//            public void onMessage(String message) {
//                System.out.println(message);
//            }
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
//                    switch (serverMessage.getServerMessageType()) {
//                        case (ServerMessage.ServerMessageType.LOAD_GAME):
//                    }
//                    if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
//
//                    }
                    observer.notify(serverMessage);
                }
        });
    }

    public void send(String msg) throws Exception {this.session.getBasicRemote().sendText(msg);}
    public void onOpen(Session session, EndpointConfig endpointConfig) {}
}
