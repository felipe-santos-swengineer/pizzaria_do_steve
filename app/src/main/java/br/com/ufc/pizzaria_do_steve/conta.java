package br.com.ufc.pizzaria_do_steve;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conta);
        getSupportActionBar().setTitle("Conta");

        rv_pessoais = (RecyclerView) findViewById(R.id.rv_pessoais);
        adapter = new GroupAdapter();
        rv_pessoais.setAdapter(adapter);
        rv_pessoais.setLayoutManager(new LinearLayoutManager(this));

        FirebaseFirestore.getInstance().collection("/users")
                .document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            User me = documentSnapshot.toObject(User.class);
                            adapter.add(new UserItem(me));
                        }
                    }
                });
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