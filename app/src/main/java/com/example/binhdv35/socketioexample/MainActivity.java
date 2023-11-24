package com.example.binhdv35.socketioexample;

import androidx.appcompat.app.AppCompatActivity;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://10.0.2.2:3030");
        } catch (URISyntaxException e) {}
    }
    TextView messageView;
    Button sendButton;
    EditText messageInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        mSocket.connect();
        mSocket.on("new_message", onNewMessage);
        sendButton.setOnClickListener(v -> {
            attemptSend();
        });
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        String message = data.getString("content");
                        messageView.setText(""+message);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    // add the message to view

                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off("new_message");
    }

    private void attemptSend() {
        mSocket.connect();
        String message = messageInput.getText().toString().trim();

        JSONObject data = new JSONObject();
        try {
            data.put("sender_id", "65464b0147f9a5c3c2ae23ba");
            data.put("receiver_id", "12");
            data.put("content", message);
            data.put("image", "link image");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        if (TextUtils.isEmpty(message)) {
            return;
        }

        messageInput.setText("");
        mSocket.emit("data", data);
    }

    private void initView() {
        messageInput = findViewById(R.id.input);
        sendButton = findViewById(R.id.button_input);
        messageView =  findViewById(R.id.textView);
    }
}