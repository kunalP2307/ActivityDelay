package com.example.activitydelay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements SelectListener {

    String url = "https://script.google.com/macros/s/AKfycbx_K0A9LvT4swbA35bxV5y0ht4Lx6b0GGpNtQy_B9m9l3ZUka502zKjQMCbN1YinHsG/exec?";
    RecyclerView recyclerView;
    private ActivityAdapter activityAdapter;
    private ArrayList<ArrayList<Activity>> subActivities = new ArrayList<ArrayList<Activity>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activityAdapter = new ActivityAdapter(this, this);
        recyclerView = findViewById(R.id.recycler_activity_main);
        recyclerView.setAdapter(activityAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getData();

        subActivities.add(new ArrayList<>());
        subActivities.add(new ArrayList<>());
        subActivities.add(new ArrayList<>());

        subActivities.get(2).add(new Activity());
    }

    private void getData(){
        url += "action=get";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    JSONArray jsonArray = response.getJSONArray("activities");
                    //json = jsonArray.toString();
                    int j = -1;
                    for(int i=0; i<jsonArray.length(); i++){
                        JSONObject object = jsonArray.getJSONObject(i);

                        String id = object.getString("ID");
                        String taskName = object.getString("Task Name");
                        String duration = object.getString("Duration");
                        String startDate = object.getString("Start");
                        String finishDate = object.getString("Finish");
                        String Predecessors = object.getString("Predecessors");

                        Activity activity = new Activity(id.substring(0, id.length()-1), taskName, duration, startDate, finishDate, new String[]{});

                        int delay = ActivityUtils.getDelay(activity);
                        activity.setDelay(delay);
                        Log.d("", "onResponse: "+activity.toString());
                        if(id.charAt(id.length() - 1) == 'M') {
                            activityAdapter.addActivity(activity);
                            j += 1;
                            subActivities.add(new ArrayList<>());
                        }
                        subActivities.get(j).add(activity);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }

    public static int getDelay(String start, String finish, String duration){
        String delay = "";

        Log.d("", "getDelay: "+start+" "+finish+" "+duration);
        String dayDuration = duration.split(" ")[0];

        DateTimeFormatter inputFormatter = null,outputFormatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyy", Locale.ENGLISH);
        }

        long dayDiff = 0;
        try {
            LocalDate fStartDate = null, fFinishDate = null;
            String formattedStartD = "", formattedEndD = "";
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                fStartDate = LocalDate.parse(start,inputFormatter);
                formattedStartD = outputFormatter.format(fStartDate);
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                fFinishDate = LocalDate.parse(finish, inputFormatter);
                formattedEndD = outputFormatter.format(fFinishDate);
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

            Date startD = simpleDateFormat.parse(formattedStartD);
            Date finishD = simpleDateFormat.parse(formattedEndD);
            long timeDiff = startD.getTime() - finishD.getTime();
            dayDiff = TimeUnit.MILLISECONDS.toDays(timeDiff) % 365;
            dayDiff += 1;

        }catch(ParseException e) {

        }

        long delayInt = Integer.parseInt(dayDuration) - dayDiff;
        //System.out.println(delayInt);

        return (int) delayInt;
    }


    @Override
    public void onItemClick(View view,int position, Activity activity) {
        Toast.makeText(this, ""+subActivities.get(position).size(), Toast.LENGTH_SHORT).show();
    }
}