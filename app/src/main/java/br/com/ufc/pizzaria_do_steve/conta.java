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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

public class conta extends AppCompatActivity {

    GroupAdapter adapter;
    RecyclerView rv_pessoais;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    Button btn_update_name;
    Button btn_update_address;
    Button btn_update_ref;
    EditText edt_new_data;
    User me;
    String action;
    ProgressDialog progressDialog;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conta);
        getSupportActionBar().setTitle("Conta");

        rv_pessoais = (RecyclerView) findViewById(R.id.rv_pessoais);
        adapter = new GroupAdapter();
        rv_pessoais.setAdapter(adapter);
        rv_pessoais.setLayoutManager(new LinearLayoutManager(this));

        btn_update_name = (Button) findViewById(R.id.btn_update_name);
        btn_update_address = (Button) findViewById(R.id.btn_update_address);
        btn_update_ref = (Button) findViewById(R.id.btn_update_ref);
        edt_new_data = (EditText) findViewById(R.id.edt_new_data);

        //Adquirindo User no firebase e preenchendo RV
        String doing = "Getting User info";
        new GetUserInFirebase().execute(doing);

        btn_update_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if(edt_new_data.getText().toString().isEmpty()){
                    Toast.makeText(conta.this,"Campo vazio", Toast.LENGTH_SHORT).show();
                    return;
                }

                action = "name";
                Log.d("teste", "cliquei" + action);
                new UpdateUserInFirebase().execute(action);

            }
        });

        btn_update_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if(edt_new_data.getText().toString().isEmpty()){
                    Toast.makeText(conta.this,"Campo vazio", Toast.LENGTH_SHORT).show();
                    return;
                }
                action = "address";
                new UpdateUserInFirebase().execute(action);

            }
        });

        btn_update_ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if(edt_new_data.getText().toString().isEmpty()){
                    Toast.makeText(conta.this,"Campo vazio", Toast.LENGTH_SHORT).show();
                    return;
                }
                action = "ref";
                new UpdateUserInFirebase().execute(action);

            }
        });
    }

    public class GetUserInFirebase extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = progressDialog.show(conta.this, "Aguarde",
                    "Carregando informações...", true, false);
        }

        @Override
        protected String doInBackground(String... strings) {
            FirebaseFirestore.getInstance().collection("/users")
                    .document(FirebaseAuth.getInstance().getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()) {
                                me = documentSnapshot.toObject(User.class);
                                adapter.add(new UserItem(me));
                            }
                        }
                    });
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }
    }

    public class UpdateUserInFirebase extends AsyncTask<String, String, String>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = progressDialog.show(conta.this, "Aguarde",
                    "atualizando campo...", true, false);
        }

        @Override
        protected String doInBackground(String... strings) {

            if (strings[0].equals("name")) {
                me.setName(edt_new_data.getText().toString());
            } else if (strings[0].equals("address")) {
                me.setEndereco(edt_new_data.getText().toString());
            } else if (strings[0].equals("ref")) {
                me.setRef(edt_new_data.getText().toString());
            }
            Log.d("teste", "editei");
            //update in firebase

            FirebaseFirestore.getInstance().collection("users")
                    .document(me.getId())
                    .set(me)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(conta.this);
                            builder.setCancelable(false);
                            builder.setTitle(Html.fromHtml("<font color='#509324'>Sucesso</font>"));
                            builder.setMessage("Campo atualizado!");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent;
                                    intent = new Intent(conta.this, conta.class);
                                    intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    overridePendingTransition(0,0);
                                }
                            });
                            builder.show();
                        }

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(conta.this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                verifyAuth();

        }
        return super.onOptionsItemSelected(item);
    }

    public void verifyAuth(){
        //testa se o usuario não está logado
        if(FirebaseAuth.getInstance().getUid() == null){
            Intent intent = new Intent(this, login.class);
            intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(0,0);
        }
    }

    private class UserItem extends Item<ViewHolder> {

        private User user;

        private UserItem(User user){
            this.user = user;
        }

        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {

            TextView acc_name = viewHolder.itemView.findViewById(R.id.acc_name);
            TextView acc_address = viewHolder.itemView.findViewById(R.id.acc_address);
            TextView acc_ref = viewHolder.itemView.findViewById(R.id.acc_ref);
            TextView acc_email = viewHolder.itemView.findViewById(R.id.acc_email);
            ImageView acc_img = viewHolder.itemView.findViewById(R.id.acc_img);

            Picasso.get()
                    .load(user.getFotoUrl())
                    .into(acc_img);

            acc_name.setText("Nome: "+ user.getName());
            acc_address.setText("Endereço: " + user.getEndereco());
            acc_ref.setText("Referência: " + user.getRef());
            acc_email.setText("Email: " + user.getEmail());
        }

        @Override
        public int getLayout() {
            return R.layout.item_user_conta;
        }
    }


    public void OnClickBtnSobre(View v) {
        Intent intent;
        intent = new Intent(this, sobre.class);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    public void OnClickBtnCardapio(View v) {
        Intent intent;
        intent = new Intent(this, main_menu.class);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    public void OnClickBtnCarrinho(View v) {
        Intent intent;
        intent = new Intent(this, carrinho.class);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    public void OnClickBtnPedidos(View v){
        Intent intent;
        intent = new Intent(this, conta_pedidos.class);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0,0);
    }


}