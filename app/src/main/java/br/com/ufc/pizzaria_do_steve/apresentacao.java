package br.com.ufc.pizzaria_do_steve;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class apresentacao extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 4000;
    ProgressBar bar_loading;
    TextView txt_loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apresentacao);

        bar_loading = findViewById(R.id.bar_loading);
        txt_loading = findViewById(R.id.txt_loading);

        bar_loading.setMax(100);
        bar_loading.setScaleY(3f);
        bar_loading.setVisibility(View.INVISIBLE);

        //progressAnimation();
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                iniciarApp();
            }
        },4000);
    }

    public void iniciarApp(){
           startActivity(new Intent(this,main_menu.class));
           overridePendingTransition(0,0);
           finish();
    }

    public void progressAnimation(){
        ProgressBarAnimation anim = new ProgressBarAnimation(this, bar_loading,txt_loading, 0f, 100f);
        anim.setDuration(8000);
        bar_loading.setAnimation(anim);

    }
}