package rmsf.picam;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DisplayActivity extends AppCompatActivity {

    VideoView streamView;
    Button button_up;
    Button button_down;
    Button button_right;
    Button button_left;
    MediaController mediaController;
    String name;
    String move;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        /*Since we can use the stream only in a local connection, as a temporary solution
        * it is fixed here*/

        String s = "rtsp://192.168.1.194:8080/";
        String s2 = "rtsp://192.168.1.194:8554/";
        button_up = findViewById(R.id.button_up);
        button_down = findViewById(R.id.button_down);
        button_right = findViewById(R.id.button_right);
        button_left = findViewById(R.id.button_left);
        streamView = findViewById(R.id.videoView);
        Intent intent = getIntent();

        if (intent != null) {
            name = intent.getStringExtra("name");
        }

        Uri UriSrc = Uri.parse(s);
        if(UriSrc == null){
            Toast.makeText(DisplayActivity.this,
                        "UriSrc == null", Toast.LENGTH_LONG).show();
        }else{
            streamView.setVideoURI(UriSrc);
            mediaController = new MediaController(this);
            mediaController.setAnchorView(streamView);
            streamView.setMediaController(mediaController);
            streamView.start();
            Toast.makeText(DisplayActivity.this,
                        "Connect: " + s, Toast.LENGTH_LONG).show();
        }

        button_up.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                URL url;
                move = "up";
                try {
                    url = new URL("http://web.tecnico.ulisboa.pt/ist181216/rmsf-project-server/movement.php?move=" + move+
                            "&name=" + name);
                    new camera_movement().execute(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });

        button_down.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                URL url;
                move = "down";
                try {
                    url = new URL("http://web.tecnico.ulisboa.pt/ist181216/rmsf-project-server/movement.php?move=" + move+
                            "&name=" + name);
                    new camera_movement().execute(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });

        button_right.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                URL url;
                move = "right";
                try {
                    url = new URL("http://web.tecnico.ulisboa.pt/ist181216/rmsf-project-server/movement.php?move=" + move+
                            "&name=" + name);
                    new camera_movement().execute(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });

        button_left.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                URL url;
                move = "left";
                try {
                    url = new URL("http://web.tecnico.ulisboa.pt/ist181216/rmsf-project-server/movement.php?move=" + move+
                            "&name=" + name);
                    new camera_movement().execute(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static class camera_movement extends AsyncTask<URL, Integer, String> {

        int res = 0;
        @Override
        protected String doInBackground(URL... url) {
            HttpURLConnection urlConnection = null;

            try {
                urlConnection = (HttpURLConnection) url[0].openConnection();
                urlConnection.connect();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader isr = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(isr);
                StringBuilder stringB = new StringBuilder();

                String s = null;
                while((s = br.readLine()) != null) {
                    stringB.append(s);
                }

                String r = new String();
                Pattern ptrn = Pattern.compile("\\{(.*?)\\}");
                Matcher matcher = ptrn.matcher(stringB.toString());
                while(matcher.find()) {
                    r += matcher.group(0);
                }

                JSONObject jsn = new JSONObject(r);
                res = jsn.getInt("success");

                System.out.print("\n\nresult " + res);


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            //Toast.makeText(getApplicationContext(),
            //        "Result: " + res + " !!", Toast.LENGTH_SHORT).show();

            res = 0;
        }
    }
}