package org.traccar.client;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Login extends AppCompatActivity {
    Button callSignUp, login_btn;
    ImageView image;
    TextView logoText, sloganText;
    TextInputLayout username, password;
    TextInputEditText username1, password1;
    DBHelper dbHelper;
    String Storeuser;
    String Storemob;
    String message;
    String checkusername,checkpassword;
    ProgressBar pbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        dbHelper=new DBHelper(this);
        //Hooks
        callSignUp = findViewById(R.id.signup_screen);
        image = findViewById(R.id.logo_image);
        logoText = findViewById(R.id.logo_name);
        sloganText = findViewById(R.id.slogan_name);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        username1 = findViewById(R.id.username1);
        password1 = findViewById(R.id.password1);
        login_btn = findViewById(R.id.login_btn);
        pbar = (ProgressBar) findViewById(R.id.log_progress);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkusername = username1.getText().toString();
                checkpassword = password1.getText().toString();
                //validate form
                if(validateLogin(checkusername, checkpassword)){
                    //do loginhj

                    Log.e("ffffffffffffffff",""+checkusername);
                    Log.e("ffffffffffffffff",""+checkpassword);

                    fetchData fetchData = new fetchData();
                    fetchData.execute();

                }

            }
        });

        callSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                Pair[] pairs = new Pair[7];
                pairs[0] = new Pair<View, String>(image, "logo_image");
                pairs[1] = new Pair<View, String>(logoText, "logo_text");
                pairs[2] = new Pair<View, String>(sloganText, "logo_desc");
                pairs[3] = new Pair<View, String>(username, "username_tran");
                pairs[4] = new Pair<View, String>(password, "password_tran");
                pairs[5] = new Pair<View, String>(login_btn, "button_tran");
                pairs[6] = new Pair<View, String>(callSignUp, "login_signup_tran");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login.this, pairs);
                    startActivity(intent, options.toBundle());
                }
            }
        });
    }


    public boolean validateLogin(String username, String password){
        if(username == null || username.trim().length() == 0){
            Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password == null || password.trim().length() == 0){
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    public class fetchData extends AsyncTask<Void,Void,Void> {
        String data = "";
        String dataParsed = "";
        String singleParsed = "";

        @Override
        protected void onPreExecute() {
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (checkusername == null || checkpassword == null) {

//                Toast.makeText(LoginActivity.this, " Fill the Fields", Toast.LENGTH_SHORT).show();

            } else {

                try {

                    URL url = new URL("https://b2b.texvalleyb2b.in/api_dcr/login.php?username="+checkusername+"&password="+checkpassword);

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line = "";
                    while (line != null) {
                        line = bufferedReader.readLine();
                        data = data + line;
                    }

                    Log.e("dddddddddddddd", "" + data);
                    JSONObject jsonobj = new JSONObject(data);
//                    JSONObject jObject = jsonobj.getJSONObject("data");

                    singleParsed = (String) jsonobj.get("login_name");
                    Storeuser = (String) jsonobj.get("login_id");
                    Storemob = (String) jsonobj.get("login_mobile");
                    message = (String) jsonobj.get("message");
                    Log.e("ddddddddd", "" + singleParsed);
                    Log.e("ddddddddd", "" + Storeuser);
                    Log.e("ddddddddd", "" + message);
                    dataParsed = dataParsed + singleParsed + "\n";

              /*      if (message != "Welcome !!") {
                        dbHelper.insertData(Storeuser,singleParsed);
//                        pbar.setVisibility(View.INVISIBLE);

                        Intent i=new Intent(ActivitySignin.this,Student_details.class);
                        startActivity(i);
                    }else{
                        Toast.makeText(ActivitySignin.this, ""+message, Toast.LENGTH_SHORT).show();
//                        pbar.setVisibility(View.INVISIBLE);
                    }*/


                } catch (MalformedURLException e) {
                    e.printStackTrace();
//                    WriteFile(e);
                } catch (IOException e) {
                    e.printStackTrace();
//                    WriteFile(e);
                } catch (JSONException e) {
                    e.printStackTrace();
//                    WriteFile(e);
                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!singleParsed.equals(null)) {
//                        pbar.setVisibility(View.INVISIBLE);
                dbHelper.insertData(Storeuser,singleParsed);
                pbar.setVisibility(View.GONE);
                Intent i=new Intent(Login.this,Start.class);
                i.putExtra("user",""+Storeuser);
                startActivity(i);

            }else{
                Toast.makeText(Login.this, ""+message, Toast.LENGTH_SHORT).show();
            }

        }
    }
}