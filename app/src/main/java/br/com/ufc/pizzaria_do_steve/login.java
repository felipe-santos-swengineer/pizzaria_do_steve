package br.com.ufc.pizzaria_do_steve;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {

    private EditText edt_email;
    private EditText edt_password;
    private Button btn_login;
    private TextView text_create_acc;
    private ProgressBar bar_trying_enter;
    private String error_msg;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("Log in");

        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_password = (EditText) findViewById(R.id.edt_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        text_create_acc = (TextView) findViewById(R.id.text_create_acc);
        bar_trying_enter= (ProgressBar) findViewById(R.id.bar_trying_enter);

        String email = edt_email.getText().toString();
        String password = edt_password.getText().toString();



    }

    public void OnClickBtnRegister(View v) {
        Intent intent;
        intent = new Intent(this, registro.class);
        //intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);



    }

    public void OnClickBtnEnter(View v){
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        bar_trying_enter.setVisibility(View.VISIBLE);
        String email = edt_email.getText().toString();
        String password = edt_password.getText().toString();
        if(email.isEmpty() || email == null || password.isEmpty() || password == null) {
            Toast.makeText(this, "Email e/ou senha invalidos ou faltantes", Toast.LENGTH_SHORT).show();
            bar_trying_enter.setVisibility(View.INVISIBLE);
            return;
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        iniciar_act_messagens(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        error_msg = e.getMessage();
                        iniciar_act_messagens(false);
                    }
                });

    }

    public void iniciar_act_messagens(boolean decisao){
        bar_trying_enter.setVisibility(View.GONE);
        if(decisao == false){
            Toast.makeText(this, error_msg, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent;
        intent = new Intent(this, main_menu.class);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

}