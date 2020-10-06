package org.traccar.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Register extends AppCompatActivity {
    TextInputLayout username, password,phone,Email;
    TextInputEditText username1, password1,phone1,Email1;
    Button go;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        username1=findViewById(R.id.username1);
        password1=findViewById(R.id.password1);
        phone1=findViewById(R.id.phone1);
        Email1=findViewById(R.id.Email1);

        go=findViewById(R.id.go);

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String usernamestr=username1.getText().toString();
                String passstr=password1.getText().toString();
                String phonestr=phone1.getText().toString();
                String emailstr=Email1.getText().toString();

                postData(usernamestr,passstr,phonestr,emailstr);

            }
        });


    }


    // Post Request For JSONObject
    public void postData(String usernamestr, String passstr, String phonestr, String emailstr) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject object = new JSONObject();
        try {
            SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
            Date todayDate = new Date();
            String thisDate = currentDate.format(todayDate);
            Log.e("xddddd",""+ usernamestr);Log.e("xddddd",""+ passstr);Log.e("xddddd",""+ phonestr);Log.e("xddddd",""+ emailstr);
            object.put("username",""+usernamestr);
            object.put("password",""+passstr);
            object.put("phone",""+phonestr);
            object.put("email",""+emailstr);

        } catch (JSONException e) {
            e.printStackTrace();
        }


            String url ="https://location-data.herokuapp.com/user/register";

        // Enter the correct url for your api service site

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url,object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("xdddddddddddd",""+ response.toString());
                        Toast.makeText(Register.this, "User Created Sucessfully", Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(Register.this,Login.class);
                        startActivity(i);
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