package org.traccar.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import androidx.preference.TwoStatePreference;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.navigation.NavigationView;

import org.traccar.client.Expense.ExpenseActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Start extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout mdrawerlayout;
    private ActionBarDrawerToggle mToggle;
    DBHelper dbHelper;
    String na;

    int time;
    DatePickerDialog picker;
    String pa,student_id;
    Button goo,Stop;


    private static final int ALARM_MANAGER_INTERVAL = 15000;

    public static final String KEY_DEVICE = "id";
    public static final String KEY_URL = "url";
    public static final String KEY_INTERVAL = "interval";
    public static final String KEY_DISTANCE = "distance";
    public static final String KEY_ANGLE = "angle";
    public static final String KEY_ACCURACY = "accuracy";
    public static final String KEY_STATUS = "status";
    public static final String KEY_BUFFER = "buffer";

    private static final int PERMISSIONS_REQUEST_LOCATION = 2;

    private SharedPreferences sharedPreferences;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        BarChart chart = (BarChart) findViewById(R.id.chart);

        BarData data = new BarData(getXAxisValues(), getDataSet());
        chart.setData(data);
        chart.setDescription("My Chart");
        chart.animateXY(2000, 2000);
        chart.invalidate();

        mPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
        //mPreferences = getSharedPreferences("tabian.com.sharedpreferencestest", Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        goo=findViewById(R.id.start);
        Stop=findViewById(R.id.stop);

        dbHelper=new DBHelper(this);

        Intent intent = getIntent();

        dbHelper=new DBHelper(this);

        Cursor res = dbHelper.GetSQLiteDatabaseRecords();

        while (res.moveToNext()) {
//            id = res.getString(0);
            na = res.getString(1);
            pa = res.getString(2);

        }

        boolean hasLoggedIn = mPreferences.getBoolean("hasActive", false);
        if (hasLoggedIn) {
            goo.setEnabled(false);
            Stop.setEnabled(true);
            goo.setBackgroundResource(R.drawable.disable_btn);
            Stop.setBackgroundResource(R.drawable.btn_rounded_accent3);
        }
        else {
            stopTrackingService();
            Stop.setEnabled(false);
            goo.setEnabled(true);
            Stop.setBackgroundResource(R.drawable.disable_btn);
            goo.setBackgroundResource(R.drawable.btn_rounded_accent2);
        }


        goo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTrackingService(true, false);
                goo.setEnabled(false);
                Stop.setEnabled(true);

                mEditor.putBoolean("hasActive", true);
                mEditor.commit();

                goo.setBackgroundResource(R.drawable.disable_btn);
                Stop.setBackgroundResource(R.drawable.btn_rounded_accent3);
            }
        });
        Stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTrackingService();
                Stop.setEnabled(false);

                mEditor.putBoolean("hasActive", false);
                mEditor.commit();

                goo.setEnabled(true);
                Stop.setBackgroundResource(R.drawable.disable_btn);
                goo.setBackgroundResource(R.drawable.btn_rounded_accent2);
            }
        });
        alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(getApplicationContext(), AutostartReceiver.class), 0);

        if (sharedPreferences.getBoolean(KEY_STATUS, false)) {
            startTrackingService(true, false);
        }

        mdrawerlayout=(DrawerLayout)findViewById(R.id.drawerlayout);
        mToggle=new ActionBarDrawerToggle(this,mdrawerlayout,R.string.open,R.string.close);
        mdrawerlayout.addDrawerListener(mToggle);
        mToggle.syncState();




        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        NavigationView navigationView=findViewById(R.id.nav_view);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int id = menuItem.getItemId();

                //creating fragment object
                Fragment fragment = null;

                if (id == R.id.nav_profile) {

                    startActivity(new Intent(Start.this, MainActivity.class));
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

                } else if (id == R.id.nav_attendance) {


                    startActivity(new Intent(Start.this, Trackingloc.class));
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);



                } else if (id == R.id.nav_dash) {


                    startActivity(new Intent(Start.this, Start.class));
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

                }  else if (id == R.id.nav_expense) {


                    startActivity(new Intent(Start.this, ExpenseActivity.class));
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

                }  else if (id == R.id.nav_sale) {

                }



                else if (id == R.id.nav_map) {
                    Intent i = new Intent(Start.this, Maploc.class);
                    startActivity(i);

                    /*Fragment fragment1 = new Maps_Fragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.contain_frame, fragment1).commit();*/

                } /*else if (id == R.id.nav_timeline) {
                    Intent i = new Intent(MainActivity.this, Time_line_cal.class);
                   startActivity(i);

                    *//*Fragment fragment1 = new Maps_Fragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.contain_frame, fragment1).commit();*//*

                }*/
                else if (id == R.id.nav_signout) {
                    dbHelper.deleteRow();
                    startActivity(new Intent(Start.this, Login.class));

                }


                //replacing the fragment
                if (fragment != null) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.contain_frame, fragment);
                    ft.commit();
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerlayout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });


    }
    private ArrayList getDataSet() {

        ArrayList dataSets = null;

        ArrayList valueSet1 = new ArrayList();
        BarEntry v1e1 = new BarEntry(500f, 0); // Jan
        valueSet1.add(v1e1);
        BarEntry v1e2 = new BarEntry(600f, 1); // Feb
        valueSet1.add(v1e2);
        BarEntry v1e3 = new BarEntry(300f, 2); // Mar
        valueSet1.add(v1e3);

        ArrayList valueSet2 = new ArrayList();
        BarEntry v2e1 = new BarEntry(400f, 0); // Jan
        valueSet2.add(v2e1);
        BarEntry v2e2 = new BarEntry(300f, 1); // Feb
        valueSet2.add(v2e2);
        BarEntry v2e3 = new BarEntry(500f, 2); // Mar
        valueSet2.add(v2e3);

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Target");
        barDataSet1.setColor(Color.rgb(0, 155, 0));
        BarDataSet barDataSet2 = new BarDataSet(valueSet2, "Closed");
        barDataSet2.setColors(Collections.singletonList(Color.rgb(200, 100, 200)));

        dataSets = new ArrayList();
        dataSets.add(barDataSet1);
        dataSets.add(barDataSet2);

        return dataSets;

    }

    private ArrayList getXAxisValues() {
        ArrayList xAxis = new ArrayList();
        xAxis.add("Distributer");
        xAxis.add("Retailer");
        xAxis.add("Secondry Order");

        return xAxis;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);

    }


    private void startTrackingService(boolean checkPermission, boolean permission) {

        if (checkPermission) {
            Set<String> requiredPermissions = new HashSet<>();
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requiredPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                    && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requiredPermissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            }
            permission = requiredPermissions.isEmpty();
            if (!permission) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(requiredPermissions.toArray(new String[requiredPermissions.size()]), PERMISSIONS_REQUEST_LOCATION);
                }
                return;
            }

        }

        if (permission) {
//            setPreferencesEnabled(false);
            ContextCompat.startForegroundService(getApplicationContext(), new Intent(getApplicationContext(), TrackingService.class));
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    ALARM_MANAGER_INTERVAL, ALARM_MANAGER_INTERVAL, alarmIntent);
        } else {
            sharedPreferences.edit().putBoolean(KEY_STATUS, false).apply();
//            TwoStatePreference preference = findPreference(KEY_STATUS);
//            preference.setChecked(false);
        }
    }

    private void stopTrackingService() {
//        alarmManager.cancel(alarmIntent);
        getApplicationContext().stopService(new Intent(getApplicationContext(), TrackingService.class));
//        setPreferencesEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            boolean granted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    break;
                }
            }
            startTrackingService(false, granted);
        }
    }

    private boolean validateServerURL(String userUrl) {
        int port = Uri.parse(userUrl).getPort();
        if (URLUtil.isValidUrl(userUrl) && (port == -1 || (port > 0 && port <= 65535))
                && (URLUtil.isHttpUrl(userUrl) || URLUtil.isHttpsUrl(userUrl))) {
            return true;
        }
        Toast.makeText(getApplicationContext(), R.string.error_msg_invalid_url, Toast.LENGTH_LONG).show();
        return false;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}