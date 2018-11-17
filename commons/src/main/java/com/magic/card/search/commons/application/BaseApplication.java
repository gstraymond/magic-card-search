package com.magic.card.search.commons.application;

import android.app.Application;

//import com.crashlytics.android.Crashlytics;
//import com.crashlytics.android.answers.Answers;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.ToJson;

import java.util.Date;

//import io.fabric.sdk.android.Fabric;

public class BaseApplication extends Application {

    private Moshi moshi;

    @Override
    public void onCreate() {
        super.onCreate();
        //Fabric.with(this, new Crashlytics(), new Answers());
        this.moshi = new Moshi.Builder().add(getDateAdapter()).build();
    }

    private Object getDateAdapter() {
        return new Object() {
            @ToJson
            Long dateToJson(Date d) {
                return d.getTime();
            }

            @FromJson
            Date dateFromJson(Long millis) {
                return new Date(millis);
            }
        };
    }

    public Moshi getObjectMapper() {
        return moshi;
    }
}
