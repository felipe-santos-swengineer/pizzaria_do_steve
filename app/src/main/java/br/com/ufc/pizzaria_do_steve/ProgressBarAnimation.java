package br.com.ufc.pizzaria_do_steve;

import android.content.Context;
import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressBarAnimation extends Animation {
    private Context context;
    private ProgressBar bar_loading;
    private TextView txt_loading;
    private float from;
    private float to;


    public ProgressBarAnimation(Context context, ProgressBar progressBar, TextView textView, float from, float to){
        this.context = context;
        this.bar_loading = progressBar;
        this.txt_loading = textView;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to - from) + interpolatedTime;
        bar_loading.setProgress((int) value);
        txt_loading.setText((int) value+" %");

        if(value == to){
            context.startActivity(new Intent(context, login.class));
        }
    }
}
