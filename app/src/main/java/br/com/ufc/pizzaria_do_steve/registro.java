package br.com.ufc.pizzaria_do_steve;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class registro extends AppCompatActivity {

    ImageView img_foto;
    Button btn_add_foto;
    Button btn_register;
    EditText edt_name;
    EditText edt_address;
    EditText edt_ref;
    EditText edt_reg_email;
    EditText edt_reg_password;
    Boolean has_photo;
    String error_msg;
    Uri uri_foto;
    private long mLastClickTime = 0;


    @Override
    public void onBackPressed() {
        //retornar ao menu limpando a pilha de intents
        Intent intent;
        intent = new Intent(this, login.class);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0,0);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        getSupportActionBar().setTitle("Registro");

        img_foto = findViewById(R.id.img_foto);
        btn_add_foto = findViewById(R.id.btn_add_foto);
        btn_register = findViewById(R.id.btn_register);
        edt_name = findViewById(R.id.edt_name);
        edt_address = findViewById(R.id.edt_address);
        edt_ref = findViewById(R.id.edt_ref);
        edt_reg_email = findViewById(R.id.edt_reg_email);
        edt_reg_password = findViewById(R.id.edt_reg_password);
        has_photo = false;

    }

    public void onClickBtnAddFoto(View v){

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //request 0 = solicitação de foto e conferindo se o usuario pegou uma foto mesmo ou cancelou o pick.
        if(requestCode == 0 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri_foto = data.getData();
            has_photo = true;
            try {
                Bitmap bitmap = null;
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri_foto);
                img_foto.setImageDrawable(new BitmapDrawable(bitmap));
                btn_add_foto.setAlpha(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void onClickBtnRegister(View v) {

        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        String name = edt_name.getText().toString();
        String address = edt_address.getText().toString();
        String ref = edt_ref.getText().toString();
        String email = edt_reg_email.getText().toString();
        String password = edt_reg_password.getText().toString();

        if (email.isEmpty() || email == null || password.isEmpty() || password == null
                || name.isEmpty() || name == null || address.isEmpty() || address == null || ref.isEmpty() || ref == null) {
            Toast.makeText(this, "Há dados invalidos ou faltantes", Toast.LENGTH_SHORT).show();
            return;
        }

        if (has_photo == false) {
            Toast.makeText(this, "Insira uma foto", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        error_msg = "Validando, aguarde um instante";
                        iniciar_act_messagens(false);
                        saveUserInFirebase();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //if(task.isSuccessful()){
                        //Log.d("Teste", "Sucesso");
                        //  error_msg = "Validando, aguarde um instante";
                        // saveUserInFirebase();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String message = e.getMessage();
                        error_msg = e.getMessage();
                        iniciar_act_messagens(false);
                    }
                });
    }

    public void saveUserInFirebase(){
        String filename = UUID.randomUUID().toString();
        final StorageReference ref = FirebaseStorage.getInstance().getReference("/user_img/" + filename);
        ref.putFile(uri_foto)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //Criação do usuário no firebase
                                String id = FirebaseAuth.getInstance().getUid();
                                String nome = edt_name.getText().toString();
                                String fotoUrl = uri.toString();
                                String endereco = edt_address.getText().toString();
                                String ref = edt_ref.getText().toString();
                                String email = edt_reg_email.getText().toString();
                                String senha = edt_reg_password.getText().toString();


                                User user = new User(id, nome, fotoUrl, endereco,ref, email, senha);
                                FirebaseFirestore.getInstance().collection("users")
                                        .document(id)
                                        .set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
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
                        });
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

    public void iniciar_act_messagens(boolean x){
        if(x == false){
            Toast.makeText(this,error_msg,Toast.LENGTH_SHORT).show();
            return;
        }
        //Log.d("Teste", "abrindo nova tela");
        Intent intent;
        intent = new Intent(this, main_menu.class);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}