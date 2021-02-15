package br.com.ufc.pizzaria_do_steve;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

public class conta_pedidos extends AppCompatActivity {

    GroupAdapter adapter;
    RecyclerView rv_historico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conta_pedidos);
        getSupportActionBar().setTitle("Conta");

        rv_historico = (RecyclerView) findViewById(R.id.rv_historico);
        adapter = new GroupAdapter();
        rv_historico.setAdapter(adapter);
        rv_historico.setLayoutManager(new LinearLayoutManager(this));

        String uid = FirebaseAuth.getInstance().getUid();
        FirebaseFirestore.getInstance().collection("historico")
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            Historico historico = documentSnapshot.toObject(Historico.class);
                            for(int i = 0; i < historico.pedidos.size(); i = i + 1){
                                adapter.add(new PedidoItem(historico.pedidos.get(i),Integer.toString(i)));
                            }
                        }
                    }
                });
    }

    private class PedidoItem extends Item<ViewHolder> {

        private Pedido pedido;
        private String number;

        private PedidoItem(Pedido pedido, String number){
            this.pedido = pedido;
            this.number = number;
        }

        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {

            TextView view_pedido = viewHolder.itemView.findViewById(R.id.hc_pedido);
            TextView view_itens = viewHolder.itemView.findViewById(R.id.hc_itens);
            TextView view_price = viewHolder.itemView.findViewById(R.id.hc_valor);

            view_pedido.setText("Pedido #" + number);

            String itens = "Itens: ";
            for(int i = 0; i < pedido.getProdutos().size(); i = i + 1){
                if(i == 0){
                    itens = pedido.getProdutos().get(i).getName() + " " + pedido.getProdutos().get(i).getQuantidade() + "x";
                }
                else {
                    itens = itens + ", " + pedido.getProdutos().get(i).getName() + " " + pedido.getProdutos().get(i).getQuantidade() + "x";
                }
            }
            view_itens.setText(itens);

            view_price.setText("Valor: " + pedido.getValor());

        }

        @Override
        public int getLayout() {
            return R.layout.item_user_conta_pedido;
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

    public void OnClickBtnPessoais(View v){
        Intent intent;
        intent = new Intent(this, conta.class);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

}