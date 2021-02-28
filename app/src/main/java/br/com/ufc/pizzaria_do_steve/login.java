package br.com.ufc.pizzaria_do_steve;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
    ProgressDialog progressDialog;

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

    }

    public void OnClickBtnRegister(View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
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

        String email = edt_email.getText().toString();
        String password = edt_password.getText().toString();

        if(email.isEmpty() || email == null || password.isEmpty() || password == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(login.this);
            builder.setCancelable(false);
            builder.setTitle(Html.fromHtml("<font color='#cc0000'>Falha</font>"));
            builder.setMessage("Email e/ou senha invalidos e/ou vazios");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
            return;
        }

        String doing = "Verificando se o login é possivel";
        new LoginVerify().execute(doing);

    }

    public class LoginVerify extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = progressDialog.show(login.this, "Aguarde",
                    "Autentificando...", true, false);
        }

        @Override
        protected String doInBackground(String... strings) {
            String email = edt_email.getText().toString();
            String password = edt_password.getText().toString();
            Log.d("teste","entrei no asynctask");

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            progressDialog.dismiss();
                            Intent intent;
                            intent = new Intent(login.this, main_menu.class);
                            intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(login.this);
                            builder.setCancelable(false);
                            builder.setTitle(Html.fromHtml("<font color='#cc0000'>Falha</font>"));
                            builder.setMessage(e.getMessage());
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
                        }
                    });
            return null;
        }
    }

}