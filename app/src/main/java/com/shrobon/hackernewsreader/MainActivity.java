package com.shrobon.hackernewsreader;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> titles = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView)findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,titles);
        listView.setAdapter(arrayAdapter);

        DownloadTask task = new DownloadTask();

        task.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");


    }

    public class DownloadTask extends AsyncTask<String,Void,String>
    {
        //https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty
        @Override
        protected String doInBackground(String... params) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null ;

            try {
                url = new URL(params[0]);
                urlConnection = (HttpURLConnection)url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while(data != -1)
                {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                Log.i("ÜRLContent",result);

                //creating a parsable JSONArray
                JSONArray jsonArray = new JSONArray(result);

                int numberOfItems = 20;
                if(jsonArray.length() < 20)
                {
                    numberOfItems = jsonArray.length();
                }

                for(int i = 0; i < numberOfItems; i++)
                {
                    String articleId = jsonArray.getString(i);
                    url = new URL("https://hacker-news.firebaseio.com/v0/item/"+articleId+".json?print=pretty");
                    urlConnection = (HttpURLConnection)url.openConnection();

                    in = urlConnection.getInputStream();
                    reader = new InputStreamReader(in);
                    data = reader.read();

                    String articleInfo = "";
                    while( data != -1)
                    {
                        char current =(char)data;
                        articleInfo += current;
                        data = reader.read();
                    }

                    JSONObject jsonObject = new JSONObject(articleInfo);

                    if(!jsonObject.isNull("title") && !jsonObject.isNull("url")){
                        String articleTitle = jsonObject.getString("title");
                        String articleURL = jsonObject.getString("url");
                        Log.i("Info",articleTitle + articleURL);
                    }


                    //Log.i("JsonIte",jsonArray.getString(i));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
