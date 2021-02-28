package br.com.ufc.pizzaria_do_steve;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;
import com.xwray.groupie.ViewHolder;

import java.util.ArrayList;

public class sobre extends AppCompatActivity {

    GroupAdapter adapter;
    RecyclerView rv_info;
    ListaInfo listaInfo;
    ArrayList<Info> infos;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);
        getSupportActionBar().setTitle("Sobre nós");

        rv_info = (RecyclerView) findViewById(R.id.rv_info);
        adapter = new GroupAdapter();
        rv_info.setAdapter(adapter);
        rv_info.setLayoutManager(new LinearLayoutManager(this));

        String doing = "Getting Pizzaria Info";
        new GetInfosInFirebase().execute(doing);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Item item, @NonNull View view) {
                InfoItem infoItem = (InfoItem) item;

            }
        });

    }

    public class GetInfosInFirebase extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = progressDialog.show(sobre.this, "Aguarde",
                    "Carregando informações...", true, false);

        }

        @Override
        protected String doInBackground(String... strings) {
            FirebaseFirestore.getInstance().collection("/pz_info")
                    .document("infos")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                listaInfo = documentSnapshot.toObject(ListaInfo.class);
                                infos = listaInfo.getInfos();
                                for(int i = 0; i < infos.size(); i = i + 1){
                                    adapter.add(new InfoItem(infos.get(i)));
                                }
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

    private class InfoItem extends Item<ViewHolder> {

        private Info info;

        private InfoItem(Info info){
            this.info = info;
        }

        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {

            TextView view_titulo = viewHolder.itemView.findViewById(R.id.view_titulo);
            TextView view_descricao = viewHolder.itemView.findViewById(R.id.view_descricao);

            view_titulo.setText(info.getTitulo());
            view_descricao.setText(info.getDescricao());
        }

        @Override
        public int getLayout() {
            return R.layout.item_sobre;
        }
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

    public void OnClickBtnConta(View v) {
        Intent intent;
        intent = new Intent(this, conta.class);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0,0);
    }
}