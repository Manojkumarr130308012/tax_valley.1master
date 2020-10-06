package org.traccar.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Maploc extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;



    DatePickerDialog datePickerDialog;
    int year;
    int month;
    int dayOfMonth;
    Calendar calendar;
    PolylineOptions polylineOptions;
    MarkerOptions mopt;

    java.util.Date date1,date12;
    double lat1,log1,location_lat2,location_long2;
    String Geo_loc,Dis_name;
    long Milli;
    String s;
    private int mYear, mMonth, mDay, mHour, mMinute;

    List<Map<String,String>> MyData;
    String[] fromwhere = { "Date","Cdate","Cevent" };

    TextView mab_fab;

    int kmInDec,kmInDec1;
    String tr1;

    ArrayList<LatLng> points;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    float tra;


    String id;

    FloatingActionButton btn;
    private static final String MAP_VIEW_BUNDLE_KEY = "AIzaSyA7RksHmgpXNyc655j-m1-70BEK-NVO3m4";

    List<String> list0;
    DBHelper dbHelper;
    String na;

    int time;
    DatePickerDialog picker;
    String pa,student_id;
    ArrayList<HashMap<String, String>> locationlist;
    String thisDate;
    Button button1;
    TextView fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maploc);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        dbHelper = new DBHelper(this);


        Cursor res = dbHelper.GetSQLiteDatabaseRecords();

        while (res.moveToNext()) {
//            id = res.getString(0);
            na = res.getString(1);
            pa = res.getString(2);

        }



        points = new ArrayList<LatLng>();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
        Date todayDate = new Date();
        thisDate = currentDate.format(todayDate);
        locationlist = new ArrayList<>();
        new gethomework().execute();


        fab=findViewById(R.id.mab_fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(Maploc.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                      fab.setText(day + "/" + (month + 1) + "/" + year);
                                mMap.clear();
                                points.clear();
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(year, month, day);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                String dateString = dateFormat.format(calendar.getTime());
                                thisDate=dateString;
                                new gethomework().execute();

                                onMapReady(mMap);

                            }
                        }, year, month, dayOfMonth);
//                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();


            }
        });


    }

 /*   @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        float zoomLevel = 16.0f; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel));
    }*/


    private class gethomework extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            locationlist = new ArrayList<>();
//            Toast.makeText(Student_details.this,"Json Data is downloading",Toast.LENGTH_LONG).show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();


            // Making a request to url and getting response
            String url = "https://location-data.herokuapp.com/locationdata/fetchdata?username="+pa+"&datetime="+thisDate;
            String jsonStr = sh.makeServiceCall(url);

            Log.e("", "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {


                    // Getting JSON Array node
                    JSONArray studentsjson =new JSONArray(jsonStr);

                    // looping through All Contacts
                    for (int i = 0; i < studentsjson.length(); i++) {
                        JSONObject c = studentsjson.getJSONObject(i);
                        String lat = c.getString("lat");
                        String lan= c.getString("lon");
                        String geoloc= c.getString("locname");
                        String datime= c.getString("currenttime");
                        HashMap<String,String> students = new HashMap<>();
                        students.put("Lati",lat);
                        students.put("Logi", lan);
                        students.put("Geo_loc", geoloc);
                        students.put("Date_time", datime);
                        // adding contact to contact list
                        locationlist.add(students);
                        Log.e("ddddddddddddd",""+locationlist);

                    }

                } catch (final JSONException e) {
                    Log.e("", "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }


            } else {
                Log.e("", "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            onMapReady(mMap);
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        float total=0;
        int i;
        Log.e("tdytdytdytdytdyt",""+ locationlist.size());

        for (i = 0; i < locationlist.size(); i++) {

//            Log.e("Errrrror", "" + MyData.get(i).get("Dis_name"));
            lat1 = Double.parseDouble(locationlist.get(i).get("Lati"));
            log1= Double.parseDouble(locationlist.get(i).get("Logi"));
            Double lati=lat1;
            Double logi=log1;
            Geo_loc=locationlist.get(i).get("Geo_loc");
            Dis_name= locationlist.get(i).get("Date_time");

            LatLng sydney = new LatLng(lati, logi);

            if(i+1 <= locationlist.size()-1) {

                for (int j = i + 1; j >i; j--) {

                    int Radius = 6371;
                    location_lat2 = Double.parseDouble(locationlist.get(j).get("Lati"));
                    location_long2 = Double.parseDouble(locationlist.get(j).get("Logi"));

                    Log.e("latitude2", "" + locationlist.get(j).get("Lati"));
                    Log.e("longitude2", "" + locationlist.get(j).get("Logi"));

                    double dLat = Math.toRadians(location_lat2 - lat1);
                    double dLon = Math.toRadians(location_long2 - log1);
                    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(location_lat2)) *
                                    Math.sin(dLon / 2) * Math.sin(dLon / 2);
                    double c = 2 * Math.asin(Math.sqrt(a));
                    double valueResult = Radius * c;
                    double km = valueResult / 1;


                    NumberFormat formatter = new DecimalFormat("#0.000");
                    formatter.format(km);

                    Log.e("vcdfcuydguydgiydgiyf",""+formatter.format(km));

//        BigDecimal bd = new BigDecimal(km);
                    tr1 =formatter.format(km);

                    String yourVal = ""+tr1;
                    tra = (Float.valueOf(yourVal)).floatValue();
//        kmInDec = Integer.parseInt(tr1);

                    Log.e("fbvdfghbfgnhfgnjfgn", "" + formatter.format(km));
                    Log.e("fbvdfghbfgnhfgnjfgn", "" +tr1);
//        Log.e("fbvdfghbfgnhfgnjfgn", "" + kmInDec);

                    double meter = kmInDec % 1000;
                    NumberFormat formatter1 = new DecimalFormat("0.###");
                    formatter.format(meter);
                    String tr11 =formatter.format(meter);
//        kmInDec1= Integer.parseInt(tr11);
                    Log.e("Radius Value", "" + valueResult + "   KM  " + kmInDec + " Meter   " + tr11);

                    Log.e("0052056205205205847", "" + c);


                }
            }

            total=total+tra;
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            float twoDigitsF = Float.valueOf(decimalFormat.format(total));
            fab.setText(""+twoDigitsF+"Km");

            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.blue_icon).copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(bm);
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setTextSize(26);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(""+i, canvas.getWidth()/2, canvas.getHeight()/2  , paint);// paint defines the text color, stroke width, size
            BitmapDrawable draw = new BitmapDrawable(getResources(), bm);
            Bitmap drawBmp = draw.getBitmap();

            mMap.addMarker(new MarkerOptions().position(sydney).title(""+Dis_name+"").snippet(""+tr1+"").icon(BitmapDescriptorFactory.fromBitmap(drawBmp)));



            polylineOptions = new PolylineOptions();

            // Setting the color of the polyline
            polylineOptions.color(Color.RED);
            // Setting the width of the polyline
            polylineOptions.width(5);



            Log.e("syntney",""+sydney);


            points.add(sydney);


            // Setting points of polyline
            polylineOptions.addAll(points);

            // Adding the polyline to the map
            googleMap.addPolyline(polylineOptions);



            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16),3000,null);
        }

        // Add a marker in Sydney and move the camera

    }




    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }
}