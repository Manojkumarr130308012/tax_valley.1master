package org.traccar.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.baoyachi.stepview.VerticalStepView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Trackingloc extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout mdrawerlayout;
    private ActionBarDrawerToggle mToggle;


    List<String> list0;
    DBHelper dbHelper;
    String na;

    int time;
    DatePickerDialog picker;
    String pa,student_id;
    ArrayList<HashMap<String, Object>> locationlist;
    String thisDate;
    Button button1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trackingloc);


        mdrawerlayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        mToggle = new ActionBarDrawerToggle(this, mdrawerlayout, R.string.open, R.string.close);
        mdrawerlayout.addDrawerListener(mToggle);
        mToggle.syncState();

        button1=findViewById(R.id.button1);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        NavigationView navigationView = findViewById(R.id.nav_view);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int id = menuItem.getItemId();

                //creating fragment object
                Fragment fragment = null;

                if (id == R.id.nav_profile) {

                    startActivity(new Intent(Trackingloc.this, MainActivity.class));
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

                } else if (id == R.id.nav_attendance) {


                    startActivity(new Intent(Trackingloc.this, Trackingloc.class));
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);


                } else if (id == R.id.nav_map) {
                    Intent i = new Intent(Trackingloc.this, Maploc.class);
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

                }*/ else if (id == R.id.nav_signout) {
                    dbHelper.deleteRow();
                    startActivity(new Intent(Trackingloc.this, Login.class));
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
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


        dbHelper=new DBHelper(this);

        Intent intent = getIntent();
        student_id = intent.getStringExtra("student_id");
        dbHelper=new DBHelper(this);

        Cursor res = dbHelper.GetSQLiteDatabaseRecords();

        while (res.moveToNext()) {
//            id = res.getString(0);
            na = res.getString(1);
            pa = res.getString(2);

        }

        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
        Date todayDate = new Date();
        thisDate = currentDate.format(todayDate);
        new gethomework().execute();

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(Trackingloc.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                button1.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(year, monthOfYear, dayOfMonth);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                String dateString = dateFormat.format(calendar.getTime());
                                thisDate=dateString;
                                list0=null;
                                locationlist=null;
                                new gethomework().execute();
                            }
                        }, year, month, day);
                picker.show();
            }
        });
    }
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {

            int id = item.getItemId();

            if(mToggle.onOptionsItemSelected(item)){
                return true;
            }



            return super.onOptionsItemSelected(item);

        }

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            return false;
        }

    private class gethomework extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            locationlist = new ArrayList<>();

            locationlist.clear();
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
                        String time = c.getString("currenttime");
                        String locname = c.getString("locname");
                        HashMap<String,Object> students = new HashMap<>();
                        students.put("date",time);
                        students.put("homework", locname);
                        // adding contact to contact list
                        locationlist.add(students);

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

            Timeline();
        }
    }



    private void Timeline(){
        VerticalStepView mSetpview0 = (VerticalStepView) findViewById(R.id.step_view0);
        list0 = new ArrayList<>();
        list0.clear();


        Log.e("dfh1111111",""+list0.size());
        Log.e("dfh1111111",""+locationlist.size());



        for (int i=0;i<locationlist.size();i++){

            String Loc=""+locationlist.get(i).get("date")+"\n"+locationlist.get(i).get("homework");
            list0.add(""+Loc);

        }


        Log.e("1234ggggggggg",""+list0.toString());

        mSetpview0.setStepsViewIndicatorComplectingPosition(list0.size())
                .reverseDraw(false)
                .setStepViewTexts(list0)
                .setLinePaddingProportion(3f)
                .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(getApplicationContext(), android.R.color.black))
                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(getApplicationContext(), R.color.uncompleted_text_color))
                .setStepViewComplectedTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.black))
                .setStepViewUnComplectedTextColor(ContextCompat.getColor(getApplicationContext(), R.color.uncompleted_text_color))
                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_supervisor))
                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.default_icon))
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.attention));


      /*  String[] fromwhere = {"Date_time", "Geo_loc"};
        int[] viewswhere = {R.id.lblID, R.id.lblcountryname};

        ADAhere = new SimpleAdapter(getApplicationContext(), MyData, R.layout.listtemplate, fromwhere, viewswhere);

        LV_Country.setAdapter(ADAhere);*/

    }

}