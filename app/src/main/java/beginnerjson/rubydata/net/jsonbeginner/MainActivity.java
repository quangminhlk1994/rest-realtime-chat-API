package beginnerjson.rubydata.net.jsonbeginner;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class MainActivity extends AppCompatActivity {

    public static String url = "http://192.168.56.1:3000/users.json";
    private Button btnClick;
    private TextView txtResult;
    private EditText edt1, edt2;

    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnClick = (Button) findViewById(R.id.btnClick);
        txtResult = (TextView) findViewById(R.id.txt);

        edt1 = (EditText) findViewById(R.id.editAcc);
        edt2 = (EditText) findViewById(R.id.editPwd);

        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edt1.getText().toString();
                String message = edt2.getText().toString();
                JSONObject messageJson = new JSONObject();
                try {
                    messageJson.put("name", name);
                    messageJson.put("message", message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    socket.emit("messages.create", messageJson.toString());
                    System.out.println(messageJson.toString());
                } catch (Exception e) {
                    System.out.println("Have errors: " + e.toString());
                }

            }
        });

        System.out.println("Start app");

        try {
            socket = IO.socket("http://192.168.56.1:5001/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        socket.on("new_message", onNewMessage);

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("Socket ON");
            }
        });

        socket.connect();

    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject jsonObject = new JSONObject(args[0].toString());
                        System.out.println("Have event!");
                        txtResult.setText(jsonObject.getString("name") + ": " + jsonObject.getString("message"));
                    } catch (Exception e) {
                        System.out.println("Have errors: " + e.toString());
                    }
                }
            });
        }
    };

    public Activity getActivity() {
        return this;
    }

    /*
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("happen");
                }
            });
        }
    };

    public Activity getActivity() {
        return this.getActivity();
    }
    */

    /*
    private String postData(String urlString) throws IOException, JSONException, FileNotFoundException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-type", "application/json");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.connect();
            JSONObject data = new JSONObject();
            JSONObject user = new JSONObject();
            user.put("name", "Ngoc 1");
            user.put("avatar", "avatar 1");
            data.put("user", user);
            user.put("name", "Ngoc 2");
            user.put("avatar", "avatar 2");
            data.put("user", user);

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(data.toString().getBytes());
            outputStream.close();

            InputStream in = null;
            switch (connection.getResponseCode()) {
                case 200:
                    in = new BufferedInputStream(connection.getInputStream());
                    if (in != null) System.out.println(readInput(in));
                    break;
                case 422:
                    in = new BufferedInputStream(connection.getErrorStream());
                    if (in != null) System.out.println(readInput(in));
                    break;
                default:
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }

        File f = new File("/storage/emulated/0/file.png");
        try {
            MultipartUtility multipartUtility = new MultipartUtility(urlString, "UTF-8");
            multipartUtility.addHeaderField("Accept", "application/json");
            multipartUtility.addFilePart("img[avatar]", f);
            List<String> response = multipartUtility.finish();
            System.out.println("SERVER REPLIED:");
            for (String line : response) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }

        return "null";
    }
    */

    /*
    private String readInput(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        in.close();
        return sb.toString();
    }

    private String readAll(String urlString) throws IOException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        StringBuilder sb = new StringBuilder();
        try {
            connection.setRequestMethod("GET");
            connection.setDoOutput(false);
            connection.setDoInput(true);
            connection.connect();
            InputStream in = null;
            switch (connection.getResponseCode()) {
                case 200:
                    in = new BufferedInputStream(connection.getInputStream());
                    break;
                case 401:
                    in = null;
                    sb.append("Unauthorized");
                    break;
                default:
                    in = null;
                    sb.append("Unknown response code");
                    break;
            }
            if (in != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return sb.toString();
    }
    */

}

