package org.traccar.client;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Splash extends AppCompatActivity {
    private static int SPLASH_SCREEN = 5000;

    ImageView image;
    TextView logo;
    TextView slogan;

    DBHelper dbHelper;
    String id,na,pa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        image = findViewById(R.id.imageView);
        logo = findViewById(R.id.textView);
        slogan = findViewById(R.id.textView2);
        //Animations
        Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        Animation bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
//Set animation to elements
        image.setAnimation(topAnim);
        logo.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);



        dbHelper=new DBHelper(this);
        Cursor res = dbHelper.GetSQLiteDatabaseRecords();

        while (res.moveToNext()) {
            id = res.getString(0);
            na = res.getString(1);
            pa = res.getString(2);
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i=new Intent(Splash.this,Login.class);
                //Call next screen
                // Attach all the elements those you want to animate in design
                Pair[]pairs=new Pair[2];pairs[0]=new Pair<View, String>(image,"logo_image");pairs[1]=new Pair<View, String>(logo,"logo_text");
                //wrap the call in API level 21 or higher
                if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.LOLLIPOP)
                {
                    Log.e("ddddddddddddddddd",""+id);
                    Log.e("ddddddddddddddddd",""+na);
                    Log.e("ddddddddddddddddd",""+pa);

                    if (na!=null){
                        startActivity(new Intent(Splash.this,Start.class));
                        finish();
                    }else {
                        ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(Splash.this,pairs);
                        startActivity(i,options.toBundle());
                    }

                }
            }
        },SPLASH_SCREEN);

    }
}