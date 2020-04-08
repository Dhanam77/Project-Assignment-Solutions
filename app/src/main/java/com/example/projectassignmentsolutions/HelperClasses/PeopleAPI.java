package com.example.projectassignmentsolutions.HelperClasses;

import android.app.Person;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.text.TextUtils;
import android.util.Log;

import com.example.projectassignmentsolutions.R;
import com.facebook.appevents.ml.Utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.Birthday;
import com.google.api.services.people.v1.model.Date;
import com.google.api.services.people.v1.model.Gender;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class PeopleAPI {
    public static final String BIRTHDAY_SCOPE = "https://www.googleapis.com/auth/user.birthday.read";
    private static PeopleService mInstance;

    private static Context context;

    private static PeopleService getService() {
        if (mInstance == null) mInstance = initializeService();
        return mInstance;
    }

    private static PeopleService initializeService() {
        GoogleAccountCredential credential =
                GoogleAccountCredential.usingOAuth2(context, Arrays.asList(BIRTHDAY_SCOPE));
        credential.setSelectedAccount(GoogleSignIn.getLastSignedInAccount(context).getAccount());

        return new PeopleService.Builder(AndroidHttp.newCompatibleTransport(), JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName(context.getString(R.string.app_name)).build();
    }

    public static com.google.api.services.people.v1.model.Person getProfile() {
        try {
            return getService().people().get("people/me")
                    .setPersonFields("birthdays")
                    .execute();
        } catch (Exception e) {
            Log.d("HAHAHAHHAHAHA","");
            return null;
        }
    }
/*
    public static String getBirthday(Person person) {
        try {
            List<Birthday> birthdayList = person.getName();
            if (birthdayList == null)
                return "";
            Date date = null;
            for (Birthday birthday : birthdayList) {
                date = birthday.getDate();
                if (date != null && date.size() >= 3) break;
                else date = null;
            }
            if (date == null) return "";
            Calendar calendar = Calendar.getInstance();
            calendar.set(date.getYear(), date.getMonth() - 1, date.getDay());
            String s = String.valueOf(calendar).replace("/","").replace("\"","");
            return s;
        } catch (Exception e) {
            return "";
        }
    }
*/

}