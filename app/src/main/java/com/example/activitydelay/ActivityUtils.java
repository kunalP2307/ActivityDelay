package com.example.activitydelay;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ActivityUtils {

    public static int getDelay(Activity activity){
        String delay = "";

        String start = activity.getStartDate();
        String finish = activity.getFinishDate();
        String duration = activity.getDuration();
        Log.d("", "getDelay: "+start+" "+finish+" "+duration);


        String dayDuration = duration.split(" ")[0];

        String startDate = start.split(" ")[1];
        String finishDate = finish.split(" ")[1];


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

        long dayDiff = 0;
        try {
            Date startD = simpleDateFormat.parse(startDate);
            Date finishD = simpleDateFormat.parse(finishDate);
            long timeDiff = finishD.getTime() - startD.getTime();
            dayDiff = TimeUnit.MILLISECONDS.toDays(timeDiff) % 365;
            dayDiff += 1;

        }catch(ParseException e) {

        }

        long delayInt = Integer.parseInt(dayDuration) - dayDiff;
        //System.out.println(delayInt);

        return (int) delayInt;
    }

    public static void main(String args[]){
        ActivityUtils activityUtils = new ActivityUtils();
        activityUtils.getDelay(new Activity(
                "id",
                "PDN Design,Estimate,DTP and Tender",
                "15 days",
                "Wed 07-09-22",
                "Tue 27-09-22",
                new String[]{}
        ));
    }
}
