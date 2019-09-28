package com.example.weather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    public class GetContent extends AsyncTask <String, Void, String>
    {
        @Override
        protected String doInBackground(String... urls)
        {
            StringBuilder content = new StringBuilder();
            URL url;
            HttpURLConnection connection;

            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while (data != -1)
                {
                    char current = (char) data;
                    content.append(current);
                    data = reader.read();
                }
                return content.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "No data";
        }

        @Override
        protected void onPostExecute(String content) {
            super.onPostExecute(content);
            try {
                JSONObject jsonObject = new JSONObject(content);
                String weather = jsonObject.getString("weather");

                JSONArray arr = new JSONArray(weather);

                for(int i=0;i<arr.length();i++)
                {
                    JSONObject jsonPart = arr.getJSONObject(i);
                    String main = jsonPart.getString("main");
                    String description  = jsonPart.getString("description");
                    //Log.i("Main",jsonPart.getString("main"));
                    //Log.i("Description",jsonPart.getString("description"));
                    TextView info = (TextView) findViewById(R.id.information);
                    info.setText(main+"\n"+description);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void getWeather(View view) throws UnsupportedEncodingException {
        GetContent gc = new GetContent();
        Log.i("Information","Downloading should start now");
        EditText editText = (EditText) findViewById(R.id.city);
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(editText.getWindowToken(),0);
        String city = editText.getText().toString();
        String encodedCity = URLEncoder.encode(city,"UTF-8");
        String url = "https://api.openweathermap.org/data/2.5/weather?q="+encodedCity+"&APPID=";
        String apiKey = "ee272aa83a89622f2f2100a2bce17307";
        gc.execute(url+apiKey);
    }
}
