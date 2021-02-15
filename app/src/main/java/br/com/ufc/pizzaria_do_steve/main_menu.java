package br.com.ufc.pizzaria_do_steve;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;
import com.xwray.groupie.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public class main_menu extends AppCompatActivity {

    GroupAdapter adapter;
    RecyclerView produtos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifyAuth();
        setContentView(R.layout.activity_main_menu);
        getSupportActionBar().setTitle("Cardápio");

        produtos = (RecyclerView) findViewById(R.id.produtos);
        adapter = new GroupAdapter();
        produtos.setAdapter(adapter);
        produtos.setLayoutManager(new LinearLayoutManager(this));

        Produto pizza_mista = new Produto("pizza_mista","Mista","Orégano, azeitona, mussarela, catupiry, milho, tomate e presunto",
                3, "https://firebasestorage.googleapis.com/v0/b/pizzaria-steve.appspot.com/o/images%2Fproduct%2Fmista.jpg?alt=media&token=c6b21498-7538-47f5-b5d2-43d7a3f3ab03",0);
        adapter.add(new ProdutoItem(pizza_mista));
        Produto pizza_francatur = new Produto("pizza_francatur","Francatur","Mussarela, frango, milho, catupiry e orégano",
                3, "https://firebasestorage.googleapis.com/v0/b/pizzaria-steve.appspot.com/o/images%2Fproduct%2Ffrancatur.jpg?alt=media&token=94aacda5-c01c-467a-8c87-b5a9a0800ac8",0);
        adapter.add(new ProdutoItem(pizza_francatur));
        Produto pizza_camarao = new Produto("pizza_camarao","Camarão","Orégano, mussarela,  milho, tomate e camarão",
                4, "https://firebasestorage.googleapis.com/v0/b/pizzaria-steve.appspot.com/o/images%2Fproduct%2Fcamarao.jpg?alt=media&token=510ab513-ee8b-46f4-ab0f-e5bccbc92d66",0);
        adapter.add(new ProdutoItem(pizza_camarao));
        Produto pizza_calabresa = new Produto("pizza_calabresa","Calabresa","Orégano, azeitona, mussarela, calabresa e cebola",
                3, "https://firebasestorage.googleapis.com/v0/b/pizzaria-steve.appspot.com/o/images%2Fproduct%2Fcalabresa.jpg?alt=media&token=e7e31551-2aa5-42cb-9a3c-0c6f08fb6dd8",0);
        adapter.add(new ProdutoItem(pizza_calabresa));
        Produto pizza_doce = new Produto("pizza_doce","Doce","Mussarela e chocolate",
                4, "https://firebasestorage.googleapis.com/v0/b/pizzaria-steve.appspot.com/o/images%2Fproduct%2Fdoce.jpg?alt=media&token=82fc1b3b-931e-4a76-ac5e-357196111d36",0);
        adapter.add(new ProdutoItem(pizza_doce));
        Produto refri_coca2l = new Produto("refri_coca2l","Coca-Cola","Refrigerante de 2L",
                10, "https://firebasestorage.googleapis.com/v0/b/pizzaria-steve.appspot.com/o/images%2Fproduct%2Fcoca2l.jpg?alt=media&token=554a22d4-df32-463d-aa6f-140f70edcfc3",0);
        adapter.add(new ProdutoItem(refri_coca2l));
        Produto refri_fanta2l = new Produto("refri_fanta2l","Fanta","Refrigerante de 2L",
                6, "https://firebasestorage.googleapis.com/v0/b/pizzaria-steve.appspot.com/o/images%2Fproduct%2Ffanta2l.jpg?alt=media&token=93ba4e23-dd7b-462b-a3dc-4ef8a3524b08",0);
        adapter.add(new ProdutoItem(refri_fanta2l));
        Produto refri_cajuina2l = new Produto("refri_cajuina2l","Cajuina","Refrigerante de 2L",
                6, "https://firebasestorage.googleapis.com/v0/b/pizzaria-steve.appspot.com/o/images%2Fproduct%2Fcajuina2l.jpg?alt=media&token=76a4d4b0-6c16-4658-b32b-33be83f8fe06",0);
        adapter.add(new ProdutoItem(refri_cajuina2l));


        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Item item, @NonNull View view) {
                ProdutoItem produtoItem = (ProdutoItem) item;
                //inflar o pop up
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup_add_in_car,null);
                //criar a janela popup
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // toques fora do popup fecham ele
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
                //show popup
                ImageView selected_foto = (ImageView) popupView.findViewById(R.id.selected_img);
                Picasso.get()
                        .load(produtoItem.produto.getFoto_url())
                        .into(selected_foto);
                TextView selected_name = (TextView) popupView.findViewById(R.id.selected_name);
                selected_name.setText(produtoItem.produto.getName());
                TextView selected_describe = (TextView) popupView.findViewById(R.id.selected_describe);
                selected_describe.setText(produtoItem.produto.getIngredients());
                EditText edt_quantidade = (EditText) popupView.findViewById(R.id.edt_quantidade);
                Button cancelar = (Button) popupView.findViewById(R.id.btn_cancel_add);
                Button add = (Button) popupView.findViewById(R.id.btn_add);
                popupWindow.showAtLocation(view, Gravity.CENTER, 0,0);
                //eventos
                //add
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            String quant_string = edt_quantidade.getText().toString();
                            if(quant_string != null && quant_string.isEmpty() == false) {
                                try {
                                    float quantidade = Float.parseFloat(quant_string);
                                    if(quantidade <= 0) {
                                        toast("Quantidade invalida");
                                    }
                                    else if(quantidade >= 1) {
                                        toast("Adicionou " + quant_string + " " + produtoItem.produto.getName() + " ao carrinho");
                                        popupWindow.dismiss();
                                        updateCarrinhoInFirebase(produtoItem.produto, quantidade);
                                    }

                                }
                                catch (Exception e){
                                    toast(e.getMessage());
                                }
                            }
                        }
                });
                cancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

            }
        });
    }

    public void toast(String e){
        Toast.makeText(this, e,Toast.LENGTH_SHORT).show();
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

    private class ProdutoItem extends Item<ViewHolder>{

        private Produto produto;

        private ProdutoItem(Produto produto){
            this.produto = produto;
        }

        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {

            TextView view_name = viewHolder.itemView.findViewById(R.id.view_name);
            TextView view_describe = viewHolder.itemView.findViewById(R.id.view_describe);
            TextView view_rs = viewHolder.itemView.findViewById(R.id.view_rs);
            TextView view_price = viewHolder.itemView.findViewById(R.id.view_price);
            ImageView view_img = viewHolder.itemView.findViewById(R.id.view_img);

            Picasso.get()
                    .load(produto.getFoto_url())
                    .into(view_img);

            view_name.setText(produto.getName());
            view_describe.setText(produto.getIngredients());
            view_rs.setText("R$");
            view_price.setText(Float.toString(produto.getValor()));
        }

        @Override
        public int getLayout() {
            return R.layout.item_cardapio;
        }
    }


    public void OnClickBtnSobre(View v) {
        Intent intent;
        intent = new Intent(this, sobre.class);
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

    public void updateCarrinhoInFirebase(Produto produto, float quantidade) {

        String uid = FirebaseAuth.getInstance().getUid();
        //adicionando quantidade do produto selecionado
        produto.setQuantidade(quantidade);
        //lista de produtos do carrinho
        ArrayList<Produto> produtos = new ArrayList<>();
        produtos.add(produto);
        //save or update carrinho in firebase
        Pedido carrinho = new Pedido(uid, produtos);

        FirebaseFirestore.getInstance().collection("carrinho")
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            //get copy and update
                            Pedido existent_carrinho = documentSnapshot.toObject(Pedido.class);
                            //verificar se produto ja existe
                            boolean added = false;
                            for(int i = 0; i < existent_carrinho.getProdutos().size(); i = i + 1){
                                if(existent_carrinho.getProdutos().get(i).getName().equals(produto.getName())){
                                    existent_carrinho.getProdutos().get(i).setQuantidade(existent_carrinho.getProdutos().get(i).getQuantidade() + quantidade);
                                    added = true;
                                    break;
                                }
                            }
                            //se não existe
                            if(added == false) {
                                existent_carrinho.getProdutos().add(produto);
                            }

                            existent_carrinho.atualizarValor();
                            //update in firebase
                            FirebaseFirestore.getInstance().collection("carrinho")
                                    .document(uid)
                                    .set(existent_carrinho);
                        }
                        else {
                            FirebaseFirestore.getInstance().collection("carrinho")
                                    .document(uid)
                                    .set(carrinho);
                        }
                    }
                });
    }
}