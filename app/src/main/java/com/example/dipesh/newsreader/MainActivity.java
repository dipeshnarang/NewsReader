package com.example.dipesh.newsreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static ArrayList<String> titles=new ArrayList<>();
    static ArrayList<String> urls=new ArrayList<>();
    static ArrayAdapter<String> titlesAdapter;
    ListView titlesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titlesView=findViewById(R.id.titlesView);
        titlesAdapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,titles);
        titlesView.setAdapter(titlesAdapter);

        DownloadID task=new DownloadID();
        task.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");
        titlesAdapter.notifyDataSetChanged();


        titlesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(getApplicationContext(),MainActivity2.class);
                intent.putExtra("value",i);
                startActivity(intent);
            }
        });
    }

//    public static void updateView(){
//        titlesAdapter.notifyDataSetChanged();
//    }

    class DownloadID extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            String result="";
            URL url;
            HttpURLConnection httpURLConnection;

            try{
                url=new URL(strings[0]);
                httpURLConnection= (HttpURLConnection) url.openConnection();
                InputStream in=httpURLConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();
                while(data!=-1){
                    char current=(char)data;
                    result+=current;
                    data=reader.read();

                }

                Log.i("Result",result);

                JSONArray jsonArray=new JSONArray(result);
                int jsonArrayLength=20;
                if(jsonArray.length()<20){
                    jsonArrayLength=jsonArray.length();
                }

                for(int i=0;i<jsonArrayLength;i++){
                    Log.i("Array Item", jsonArray.getString(i));
                    String getTitles="https://hacker-news.firebaseio.com/v0/item/"+jsonArray.get(i)+".json?print=pretty";
//                    Log.i("Get title",getTitles);
//                    titles.add(getTitles);
                    String news="";
                    url=new URL(getTitles);
                    httpURLConnection= (HttpURLConnection) url.openConnection();
                    InputStream inputStream=httpURLConnection.getInputStream();
                    InputStreamReader readerTitle=new InputStreamReader(inputStream);
                    data=readerTitle.read();
                    while(data!=-1){
                        char c=(char)data;
                        news+=c;
                        data=readerTitle.read();
                    }
                    Log.i("News",news);

                    JSONObject jsonObject=new JSONObject(news);

                    if(jsonObject.has("title") && jsonObject.has("url")){
                        String title=jsonObject.getString("title");
                        titles.add(title);
                        String gotourl=jsonObject.getString("url");
                        urls.add(gotourl);
                    }

//                    updateView();


//                    titlesAdapter.notifyDataSetChanged();


                }




            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            titlesAdapter.notifyDataSetChanged();

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            titlesAdapter.notifyDataSetChanged();
        }
    }
}