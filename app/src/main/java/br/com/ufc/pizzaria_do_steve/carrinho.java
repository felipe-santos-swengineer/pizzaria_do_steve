package br.com.ufc.pizzaria_do_steve;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;
import com.xwray.groupie.ViewHolder;

import java.util.ArrayList;

public class carrinho extends AppCompatActivity {

    GroupAdapter adapter;
    RecyclerView rv_carrinho;
    Pedido carrinho;
    String uid;
    TextView txt_valor_total;
    Button btn_finalizar;
    ImageView img_empty_cart;
    TextView txt_empty_cart;
    Boolean cart_empty = true;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho);
        getSupportActionBar().setTitle("Carrinho");

        txt_valor_total = (TextView) findViewById(R.id.txt_valor_total);
        btn_finalizar = (Button) findViewById(R.id.btn_finalizar);
        img_empty_cart = (ImageView) findViewById(R.id.img_empty_cart);
        txt_empty_cart = (TextView) findViewById(R.id.txt_empty_cart);

        rv_carrinho = (RecyclerView) findViewById(R.id.rv_carrinho);
        adapter = new GroupAdapter();
        rv_carrinho.setAdapter(adapter);
        rv_carrinho.setLayoutManager(new LinearLayoutManager(this));

        uid = FirebaseAuth.getInstance().getUid();

        //preenche pela primeira vez
        prencherRecyclerView();

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Item item, @NonNull View view) {
                ProdutoItem produtoItem = (ProdutoItem) item;
                //Toast("Cliquei no item" + produtoItem.produto.getName());
            }
        });

    }

    public void Toast(String s){
        Toast.makeText(this, s,Toast.LENGTH_SHORT).show();
    }

    private class ProdutoItem extends Item<ViewHolder> {

        private Produto produto;

        private ProdutoItem(Produto produto){
            this.produto = produto;
        }

        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {

            TextView view_name = viewHolder.itemView.findViewById(R.id.product_name);
            TextView view_quantidade = viewHolder.itemView.findViewById(R.id.product_quantidade);
            TextView view_price = viewHolder.itemView.findViewById(R.id.product_price);
            ImageView view_img = viewHolder.itemView.findViewById(R.id.product_img);
            Button btn_remove = viewHolder.itemView.findViewById(R.id.btn_remove);

            Picasso.get()
                    .load(produto.getFoto_url())
                    .into(view_img);

            view_name.setText("Item: " + produto.getName());
            view_quantidade.setText("Quantidade: " + produto.getQuantidade());
            view_price.setText("Valor unitário: R$ " + produto.getValor());

            btn_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast("cliquei em excluir " + produto.getName());
                    FirebaseFirestore.getInstance().collection("carrinho")
                            .document(uid)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()){
                                      Pedido aux_carrinho = documentSnapshot.toObject(Pedido.class);
                                      for(int i = 0; i < aux_carrinho.getProdutos().size(); i = i + 1){
                                          if(aux_carrinho.getProdutos().get(i).getName().equals(produto.getName())){
                                              aux_carrinho.getProdutos().remove(i);
                                              aux_carrinho.atualizarValor();
                                              FirebaseFirestore.getInstance().collection("carrinho")
                                                      .document(uid)
                                                      .set(aux_carrinho);
                                              //Atualizar recyclerView
                                              prencherRecyclerView();
                                              break;
                                          }
                                      }
                                    }
                                }
                            });
                }
            });
        }

        @Override
        public int getLayout() {
            return R.layout.item_carrinho;
        }
    }

    public void prencherRecyclerView(){
        cart_empty = true;
        adapter.clear();
        btn_finalizar.setVisibility(View.INVISIBLE);
        txt_valor_total.setVisibility(View.INVISIBLE);
        txt_empty_cart.setVisibility(View.INVISIBLE);
        img_empty_cart.setVisibility(View.INVISIBLE);

        //preenchendo rv
        FirebaseFirestore.getInstance().collection("carrinho")
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            carrinho = documentSnapshot.toObject(Pedido.class);
                            txt_valor_total.setText("R$: " + carrinho.getValor());
                            for(int i = 0; i < carrinho.getProdutos().size(); i = i + 1){
                                adapter.add(new ProdutoItem(carrinho.getProdutos().get(i)));
                                cart_empty = false;
                                txt_empty_cart.setVisibility(View.INVISIBLE);
                                img_empty_cart.setVisibility(View.INVISIBLE);
                                btn_finalizar.setVisibility(View.VISIBLE);
                                txt_valor_total.setVisibility(View.VISIBLE);

                            }
                        }
                        if(cart_empty){
                            txt_empty_cart.setVisibility(View.VISIBLE);
                            img_empty_cart.setVisibility(View.VISIBLE);
                        }
                    }
                });


    }

    public void onClickBtnFinalizar(View v){
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();


        if(adapter.getItemCount() < 1){
            Toast.makeText(this,"Não há itens no carrinho",Toast.LENGTH_SHORT).show();
            return;
        }
        //atualizar o carrinho
        prencherRecyclerView();

        ArrayList<Pedido> pedidos = new ArrayList<>();
        pedidos.add(carrinho);
        Historico new_historico = new Historico(uid, pedidos);

        FirebaseFirestore.getInstance().collection("historico")
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            //get historico
                            Historico old_historico = documentSnapshot.toObject(Historico.class);
                            //update historico
                            old_historico.pedidos.add(carrinho);
                            FirebaseFirestore.getInstance().collection("historico")
                                    .document(uid)
                                    .set(old_historico);
                            //clear cart
                            FirebaseFirestore.getInstance().collection("carrinho")
                                    .document(uid)
                                    .delete();

                            //Toast("Pedido realizado com sucesso!");
                            prencherRecyclerView();
                        }
                        else{
                            FirebaseFirestore.getInstance().collection("historico")
                                    .document(uid)
                                    .set(new_historico);

                            FirebaseFirestore.getInstance().collection("carrinho")
                                    .document(uid)
                                    .delete();

                            //Toast("Pedido realizado com sucesso!");
                            prencherRecyclerView();
                        }
                    }
                });
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

    public void OnClickBtnConta(View v) {
        Intent intent;
        intent = new Intent(this, conta.class);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0,0);
    }
}