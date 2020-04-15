package rmsf.picam;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    EditText name;
    Button button;
    int result = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name = findViewById(R.id.editText);
        button = findViewById(R.id.button_send);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String RaspName = name.getText().toString();
                URL url;

                try {
                    url = new URL("http://web.tecnico.ulisboa.pt/ist181216/rmsf-project-server/index.php?name=" + RaspName);
                    new nameCheck().execute(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public class nameCheck extends AsyncTask<URL, Integer, String> {

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
                while ((s = br.readLine()) != null) {
                    stringB.append(s);
                }

                String r = new String();
                Pattern ptrn = Pattern.compile("\\{(.*?)\\}");
                Matcher matcher = ptrn.matcher(stringB.toString());
                while (matcher.find()) {
                    r += matcher.group(0);

                }

                JSONObject jsn = new JSONObject(r);
                result = jsn.getInt("success");

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
        protected void onPostExecute(String res) {
            super.onPostExecute(res);

            if(result == 0){
                Toast.makeText(getApplicationContext(),
                        "Incorrect name!", Toast.LENGTH_SHORT).show();
            }else{
                Intent intent = new Intent(getApplicationContext(), DisplayActivity.class);
                intent.putExtra("name", name.getText().toString());
                startActivity(intent);
                finish();
            }
        }
    }

}