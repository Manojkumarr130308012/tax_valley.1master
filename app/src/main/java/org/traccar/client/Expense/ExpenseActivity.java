package org.traccar.client.Expense;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.traccar.client.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ExpenseActivity extends AppCompatActivity {

    Spinner spin, AccLocationspinner;
    CardView AccommodationCard, TravelCard, myCard, misCard, courierCard;
    TextInputEditText IndateText, OutdateText;
    private DatePickerDialog mDatePickerDialog;
    private DatePickerDialog mDatePickerDialog2;
    String[] country = { "Select Expense","Accommodation", "Travel", "Food", "Courier", "Miscellaneous"};
    String[] ALocation = { "Select Location","Erode", "Tripur", "Salem"};
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Expenses");

        spin = (Spinner) findViewById(R.id.spinner);
        AccLocationspinner = (Spinner) findViewById(R.id.AccLocationspinner);
        TravelCard = findViewById(R.id.TravelCard);
        courierCard = findViewById(R.id.courierCard);
        misCard = findViewById(R.id.misCard);
        AccommodationCard = findViewById(R.id.AccommodationCard);
        myCard = findViewById(R.id.myCard);
        IndateText = findViewById(R.id.indateText);
        OutdateText = findViewById(R.id.outdateText);

        setDateTimeField();
        IndateText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDatePickerDialog.show();
                return false;
            }
        });

        setDateTimeField2();
        OutdateText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDatePickerDialog2.show();
                return false;
            }
        });

        spin.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                       long id) {
                ((TextView) view).setTextColor(Color.BLACK);

                if (spin.getSelectedItem().equals("Accommodation")) {
                    AccommodationCard.setVisibility(View.VISIBLE);
                    TravelCard.setVisibility(View.GONE);
                    myCard.setVisibility(View.GONE);
                    courierCard.setVisibility(View.GONE);
                    misCard.setVisibility(View.GONE);
                    Toast.makeText(ExpenseActivity.this, "1", Toast.LENGTH_SHORT).show();
                } else if (spin.getSelectedItem().equals("Travel")) {
                    AccommodationCard.setVisibility(View.GONE);
                    TravelCard.setVisibility(View.VISIBLE);
                    courierCard.setVisibility(View.GONE);
                    misCard.setVisibility(View.GONE);
                    myCard.setVisibility(View.GONE);
                    Toast.makeText(ExpenseActivity.this, "2", Toast.LENGTH_SHORT).show();
                }  else if (spin.getSelectedItem().equals("Food")) {
                    AccommodationCard.setVisibility(View.GONE);
                    TravelCard.setVisibility(View.GONE);
                    myCard.setVisibility(View.VISIBLE);
                    courierCard.setVisibility(View.GONE);
                    misCard.setVisibility(View.GONE);
                    Toast.makeText(ExpenseActivity.this, "3", Toast.LENGTH_SHORT).show();
                } else if (spin.getSelectedItem().equals("Courier")) {
                    AccommodationCard.setVisibility(View.GONE);
                    TravelCard.setVisibility(View.GONE);
                    myCard.setVisibility(View.GONE);
                    courierCard.setVisibility(View.VISIBLE);
                    misCard.setVisibility(View.GONE);
                    Toast.makeText(ExpenseActivity.this, "4", Toast.LENGTH_SHORT).show();
                } else if (spin.getSelectedItem().equals("Miscellaneous")) {
                    AccommodationCard.setVisibility(View.GONE);
                    TravelCard.setVisibility(View.GONE);
                    myCard.setVisibility(View.GONE);
                    courierCard.setVisibility(View.GONE);
                    misCard.setVisibility(View.VISIBLE);
                    Toast.makeText(ExpenseActivity.this, "5", Toast.LENGTH_SHORT).show();
                }

            }
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

        AccLocationspinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                       long id) {
                ((TextView) view).setTextColor(Color.BLACK);

            }
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aaa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,ALocation);
        aaa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        AccLocationspinner.setAdapter(aaa);
    }

    private void setDateTimeField() {

        Calendar newCalendar = Calendar.getInstance();
        mDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                final Date startDate = newDate.getTime();
                String fdate = sd.format(startDate);

                IndateText.setText(fdate);

            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
//        mDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
//        mDatePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
//        mDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }

    private void setDateTimeField2() {

        Calendar newCalendar = Calendar.getInstance();
        mDatePickerDialog2 = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                final Date startDate = newDate.getTime();
                String fdate = sd.format(startDate);

                OutdateText.setText(fdate);

            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
//        mDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
//        mDatePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
//        mDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}