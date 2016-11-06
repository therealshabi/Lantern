package testing.example.com.lantern;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class recog extends AppCompatActivity
{

    //public static final String access_token ="";
    private String x;
    private TextView textView;
    private Button button1;
    private  Button button2;
    private ImageView imageView;
    //private ListView lv;
    //private List<VideoVO> data;

//    public String API_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=eminem&type=video&fields=items(id%2FvideoId%2Csnippet(description%2Cthumbnails%2Fdefault%2Furl%2Ctitle))&key=AIzaSyBUazLEbfOV-_Zla5bA8GbH4dgDi3fOJ2Y";



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recog);

        textView=(TextView)findViewById(R.id.textView44);
        button1=(Button)findViewById(R.id.case1Button);
        button2=(Button)findViewById(R.id.case2Button);
        imageView=(ImageView)findViewById(R.id.image);

        //String API_URL ="https://www.googleapis.com/youtube/v3/search?part=snippet&q="+x+"&type=video&fields=items(id%2FvideoId%2Csnippet(description%2Cthumbnails%2Fdefault%2Furl%2Ctitle))&maxResults=6&key=AIzaSyBUazLEbfOV-_Zla5bA8GbH4dgDi3fOJ2Y";

        final String API_URL="https://api.clarifai.com/v1/tag/?access_token=hTKjUZAoZtv4yeHlDUeTzcPYZmMTwy&url=https://samples.clarifai.com/metronorth.jpg";
        final  String API_URL_2="https://api.clarifai.com/v1/tag/?access_token=hTKjUZAoZtv4yeHlDUeTzcPYZmMTwy&url=https://samples.clarifai.com/travel.jpg\n";

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.metronorth));
                GetTask task = new GetTask();
                task.execute(API_URL);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.travel));
                GetTask task = new GetTask();
                task.execute(API_URL_2);
            }
        });


    }


    private class GetTask extends AsyncTask<String,Integer,String>
    {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog = new ProgressDialog(recog.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();

        }

        @Override
        protected String doInBackground(String... params)
        {

            String temp,response="";

            try
            {
                URL url1 = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept","application/json");

                if(connection.getResponseCode()!=200)
                {
                    //error
                    //   Toast.makeText(Search.this,"error in connwction ",Toast.LENGTH_SHORT).show();
                }
                else
                {

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while((temp = reader.readLine())!=null)
                    {
                        response += temp;
                    }

                }
            }

            catch(MalformedInputException e)
            {
                e.printStackTrace();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }


            return response;


        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            if(progressDialog!=null && progressDialog.isShowing())
            {
                progressDialog.dismiss();

            }
            try
            {
                textView.setText(s);
                JSONObject o=new JSONObject(s);
                //json parsing
                JSONArray results = o.getJSONArray("results");
                //Toast.makeText(getBaseContext(),"1st hai",Toast.LENGTH_SHORT).show();
                JSONObject object=results.getJSONObject(0);
               //  Toast.makeText(getBaseContext(),"2nd hai",Toast.LENGTH_SHORT).show();
                String text=object.getString("url");
               // Toast.makeText(getBaseContext(),"3rd hai",Toast.LENGTH_SHORT).show();
                JSONObject result = results.getJSONObject(5);
                JSONObject tag = result.getJSONObject("tag");

                String x =tag.getString("classes");
                // /JSONArray classes=tag.getJSONArray("classes");

               // arr= tag.get("classes");
                textView.setText(x);


            }
            catch(JSONException e)
            {
                e.printStackTrace();

            }

        }
    }


    public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if(json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
}
