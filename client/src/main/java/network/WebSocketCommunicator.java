package network;

import com.google.gson.Gson;
import ui.Client;
import ui.ServerMessageObserver;
import websocket.commands.ConnectCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;

public class WebSocketCommunicator extends Endpoint {
    private Session session;

    public WebSocketCommunicator(String serverUrl, ServerMessageObserver observer) throws Exception {
        serverUrl = serverUrl.replace("http", "ws");
        URI uri = new URI(serverUrl + "ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    switch (serverMessage.getServerMessageType()) {
                        case NOTIFICATION -> observer.notify(new Gson().fromJson(message, NotificationMessage.class));
                        case ERROR -> observer.notify(new Gson().fromJson(message, ErrorMessage.class));
                        case LOAD_GAME -> observer.notify(new Gson().fromJson(message, LoadGameMessage.class));
                    }
                }
        });
    }

    public void send(String msg) throws Exception {this.session.getBasicRemote().sendText(msg);}
    public void onOpen(Session session, EndpointConfig endpointConfig) {}
}
