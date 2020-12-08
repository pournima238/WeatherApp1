package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    EditText cityName;
    TextView resultTextView;
    String message="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityName=(EditText)findViewById(R.id.cityName);
        resultTextView=findViewById(R.id.resultTextView);




    }



    public void myToast() {
        Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
    }
    public void findWeather(View view) {
        InputMethodManager mgr=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(),0);

        try {
            String encodedCityName= URLEncoder.encode(cityName.getText().toString(),"UTF-8");
            DownloadTask task= new DownloadTask();
            task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=a75aa25237530fef5dac85427c644390");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            resultTextView.setText("");
            myToast();
        }

    }

    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            String result="";
            URL url;
            HttpURLConnection urlConnection=null;

            try{
                url= new URL(strings[0]);
                urlConnection =(HttpURLConnection)url.openConnection();
                InputStream in=urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data= reader.read();
                while(data!=-1){
                    char current= (char) data;
                    result+=current;
                    data=reader.read();

                }
                return result;

            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObject= new JSONObject(result);
                String weatherInfo=jsonObject.getString("weather");
                Log.i("weather content", weatherInfo);
                JSONArray arr= new JSONArray(weatherInfo);
                for(int i=0; i<arr.length(); i++){
                    JSONObject jsonPart=arr.getJSONObject(i);
                    String main="";
                    String description="";
                    main=jsonPart.getString("main");
                    description=jsonPart.getString("description");
                    if(main!="" && description!=""){
                        message= main +":" + description +"\n";
                    }

                }

                if(message!=""){
                    resultTextView.setText(message);
                }else{
                    myToast();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch(Exception e){
                resultTextView.setText("");
                myToast();
            }

        }
    }
}