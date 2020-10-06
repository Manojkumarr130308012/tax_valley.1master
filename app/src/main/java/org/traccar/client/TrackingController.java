/*
 * Copyright 2015 - 2019 Anton Tananaev (anton@traccar.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TrackingController implements PositionProvider.PositionListener, NetworkManager.NetworkHandler {

    private static final String TAG = TrackingController.class.getSimpleName();
    private static final int RETRY_DELAY = 30 * 1000;
    private static final int WAKE_LOCK_TIMEOUT = 120 * 1000;

    private boolean isOnline;
    private boolean isWaiting;

    private Context context;
    private Handler handler;
    private SharedPreferences preferences;

    private String url;
    private boolean buffer;

    private PositionProvider positionProvider;
    private DatabaseHelper databaseHelper;
    private NetworkManager networkManager;
    String add;

    DBHelper dbHelper;
    String id,na,pa;
    public TrackingController(Context context) {
        this.context = context;
        handler = new Handler();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        positionProvider = PositionProviderFactory.create(context, this);
        databaseHelper = new DatabaseHelper(context);
        networkManager = new NetworkManager(context, this);
        isOnline = networkManager.isOnline();
        url = preferences.getString(MainFragment.KEY_URL, context.getString(R.string.settings_url_default_value));
        buffer = preferences.getBoolean(MainFragment.KEY_BUFFER, true);

        dbHelper=new DBHelper(context);
        Cursor res = dbHelper.GetSQLiteDatabaseRecords();

        while (res.moveToNext()) {
            id = res.getString(0);
            na = res.getString(1);
            pa = res.getString(2);
        }
    }


    public void start() {
        if (isOnline) {
            read();
        }
        try {
            positionProvider.startUpdates();
        } catch (SecurityException e) {
            Log.w(TAG, e);
        }
        networkManager.start();
    }

    public void stop() {
        networkManager.stop();
        try {
            positionProvider.stopUpdates();
        } catch (SecurityException e) {
            Log.w(TAG, e);
        }
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onPositionUpdate(Position position) {
        StatusActivity.addMessage(context.getString(R.string.status_location_update));
        if (position != null) {
            if (buffer) {
                write(position);
            } else {
                send(position);
            }
        }
    }

    @Override
    public void onPositionError(Throwable error) {
    }

    @Override
    public void onNetworkUpdate(boolean isOnline) {
        int message = isOnline ? R.string.status_network_online : R.string.status_network_offline;
        StatusActivity.addMessage(context.getString(message));
        if (!this.isOnline && isOnline) {
            read();
        }
        this.isOnline = isOnline;
    }

    //
    // State transition examples:
    //
    // write -> read -> send -> delete -> read
    //
    // read -> send -> retry -> read -> send
    //

    private void log(String action, Position position) {
        if (position != null) {
            action += " (" +
                    "id:" + position.getId() +
                    " deviceid:" + position.getDeviceId() +
                    " time:" + position.getTime().getTime() / 1000 +
                    " lat:" + position.getLatitude() +
                    " lon:" + position.getLongitude() +
                    " speed:" + position.getSpeed() +
                    " course:" + position.getCourse() +
                    " accuracy:" + position.getAccuracy() +
                    " battry:" + position.getBattery() +
                    " mock:" + position.getMock() +")";
        }
        Log.d(TAG, action);
    }

    private void write(Position position) {
        log("writgggge", position);

        databaseHelper.insertPositionAsync(position, new DatabaseHelper.DatabaseHandler<Void>() {
            @Override
            public void onComplete(boolean success, Void result) {
                if (success) {
                    if (isOnline && isWaiting) {
                        read();
                        isWaiting = false;
                    }
                }
            }
        });
    }

    private void read() {
        log("read", null);
        databaseHelper.selectPositionAsync(new DatabaseHelper.DatabaseHandler<Position>() {
            @Override
            public void onComplete(boolean success, Position result) {
                if (success) {
                    if (result != null) {
                        if (result.getDeviceId().equals(preferences.getString(MainFragment.KEY_DEVICE, null))) {
                            send(result);
                        } else {
                            delete(result);
                        }
                    } else {
                        isWaiting = true;
                    }
                } else {
                    retry();
                }
            }
        });
    }

    private void delete(Position position) {
        log("delete", position);
        databaseHelper.deletePositionAsync(position.getId(), new DatabaseHelper.DatabaseHandler<Void>() {
            @Override
            public void onComplete(boolean success, Void result) {
                if (success) {
                    read();
                } else {
                    retry();
                }
            }
        });
    }

    private void send(final Position position) {
        log("sefdgdfgfdgdfnd", position);


        postData(position);

        String request = ProtocolFormatter.formatRequest(url, position);
        RequestManager.sendRequestAsync(request, new RequestManager.RequestHandler() {
            @Override
            public void onComplete(boolean success) {
                if (success) {
                    if (buffer) {
                        delete(position);
                    }
                } else {
                    StatusActivity.addMessage(context.getString(R.string.status_send_fail));
                    if (buffer) {
                        retry();
                    }
                }
            }
        });
    }

    private void retry() {
        log("retry", null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isOnline) {
                    read();
                }
            }
        }, RETRY_DELAY);
    }




    // Post Request For JSONObject
    public void postData(Position position) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject object = new JSONObject();
        try {
            SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
            Date todayDate = new Date();
            String thisDate = currentDate.format(todayDate);
            String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

            String action = null;
            if (position != null) {
               action= " (" +
                       "id:" + position.getId() +
                       " deviceid:" + position.getDeviceId() +
                       " time:" + position.getTime().getTime() / 1000 +
                       " lat:" + position.getLatitude() +
                       " lon:" + position.getLongitude() +
                       " speed:" + position.getSpeed() +
                       " course:" + position.getCourse() +
                       " accuracy:" + position.getAccuracy() +
                       " battry:" + position.getBattery() +
                       " mock:" + position.getMock() +")";


                try {
                    Geocoder myLocation = new Geocoder(context, Locale.getDefault());
                    List<Address> myList = myLocation.getFromLocation(position.getLatitude(),position.getLongitude(), 1);
                    Address address = (Address) myList.get(0);
                     add = "";
                    add += address.getAddressLine(0) + ", ";
                    add += address.getAddressLine(1) + ", ";
                    add += address.getAddressLine(2);
//optional
          /*  add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();*/

                    Log.e("Location", "Address" + add);

                }
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
//                    Toast.makeText(context, ""+add, Toast.LENGTH_SHORT).show();
                }
            }
            object.put("url",""+action);
            object.put("login_id",""+na);
            object.put("username",""+pa);
            object.put("currenttime",""+currentTime);
            object.put("deviceid",""+position.getDeviceId());
            object.put("time",""+ position.getTime().getTime() / 1000 );
            object.put("lat",""+position.getLatitude());
            object.put("lon",""+ position.getLongitude());
            object.put("locname",""+add);
            object.put("speed",""+ position.getSpeed());
            object.put("course",""+position.getCourse());
            object.put("accuracy",""+position.getAccuracy());
            object.put("battery",""+position.getBattery());
            object.put("mock",""+position.getMock());
            object.put("datetime",""+thisDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = null;
        if (position != null) {
//            url ="https://location-data.herokuapp.com/locationdata/add";
            url ="https://b2b.texvalleyb2b.in/api_dcr/send_location.php";
        }
        // Enter the correct url for your api service site

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("xddddd",""+ response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("xddddd",""+error);

            }
        });
        requestQueue.add(jsonObjectRequest);
    }


}
