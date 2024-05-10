package com.bknote71.websocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class WebSocketTest {

    @Test
    void websocket_부하테스트() throws URISyntaxException, InterruptedException, JSONException {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 100; ++i) {
            Thread thread = new Thread(() -> {
                try {
                    System.out.println("??");
                    Map<String, String> headers = new HashMap<>();
                    WebSocketClient client = new WebSocketClient(new URI("ws://localhost:8080/battle/1"), headers) {
                        @Override
                        public void onOpen(ServerHandshake handshakedata) {
                            System.out.println("on open");
                        }

                        @Override
                        public void onMessage(String message) {
                            System.out.println("message? " + message);
                        }

                        @Override
                        public void onClose(int code, String reason, boolean remote) {

                        }

                        @Override
                        public void onError(Exception ex) {
                            ex.printStackTrace();
                        }
                    };

                    client.connectBlocking();

                    JSONObject obj = new JSONObject();
                    obj.put("type", "centerbattle");
                    obj.put("protocol", "C_EnterBattle");
                    obj.put("specIndex", 0);
                    String message = obj.toString();
                    client.send(message);

                } catch (InterruptedException | JSONException e) {
                    throw new RuntimeException(e);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
                while (true) {

                    System.out.println("hi");

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

    }
}
