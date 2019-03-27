package com.application.sb.moodtacker.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;
import java.util.Date;

import static android.content.Context.ALARM_SERVICE;

public class MyAlarmManager {

    /**
     *We configure alarm time
     *
     * @param calendar is the current date that will be the alarm time
     */
    private static void setAlarmCalendar(Calendar calendar){
        Date date = new Date();
        // First calendar is now ...
        Calendar currentTime = Calendar.getInstance();
        currentTime.setTime(date);

        // ... Second calendar is the alarm config :
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);

        // If the alarm config is before now, we add one day to the alarm config
        if (calendar.before(currentTime)) {
            calendar.add(Calendar.DATE, 1);
        }
    }

    /**
     *That will start the alarm
     *
     * @param context is the context
     */
    public static void startAlarm(Context context) {

        Calendar calendar = Calendar.getInstance();

        // I create the alarm
         AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

        // I configure a calendar for the alarm
        setAlarmCalendar(calendar);

        // The alarm start according the calendar
        if (Build.VERSION.SDK_INT > 19){
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else{
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
}