package br.com.ufc.pizzaria_do_steve;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class main_menu extends AppCompatActivity {

    GroupAdapter adapter;
    RecyclerView produtos;
    private long mLastClickTime = 0;
    ProgressDialog progressDialog;
    ArrayList<Produto> cardapio;
    ListaProdutos listaProdutos;

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

        String doing = "Getting Main Menu and setting to adapter";
        new GetCardapioInFirebase().execute(doing);

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
                                        produtoItem.produto.setQuantidade(quantidade);
                                        new UpdateCarrinhoInFirebase().execute(produtoItem.produto);
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

    public class GetCardapioInFirebase extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = progressDialog.show(main_menu.this, "Aguarde",
                    "Carregando cardapio...", true, false);
        }

        @Override
        protected String doInBackground(String... strings) {
            FirebaseFirestore.getInstance().collection("/cardapio")
                    .document("itens")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                listaProdutos = documentSnapshot.toObject(ListaProdutos.class);
                                cardapio = listaProdutos.getCardapio();
                                for(int i = 0; i < cardapio.size(); i = i + 1) {
                                    adapter.add(new ProdutoItem(cardapio.get(i)));
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
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        Intent intent;
        intent = new Intent(this, carrinho.class);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    public void OnClickBtnConta(View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        Intent intent;
        intent = new Intent(this, conta.class);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0,0);
    }


    public class UpdateCarrinhoInFirebase extends AsyncTask<Produto, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = progressDialog.show(main_menu.this, "Aguarde",
                    "confirmando pedido...", true, false);
        }

        @Override
        protected String doInBackground(Produto... produtos) {
            String uid = FirebaseAuth.getInstance().getUid();
            //adicionando quantidade do produto selecionado
            //lista de produtos do carrinho
            ArrayList<Produto> produtos_ = new ArrayList<>();
            produtos_.add(produtos[0]);
            //save or update carrinho in firebase
            Pedido carrinho = new Pedido(uid, produtos_);

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
                                    if(existent_carrinho.getProdutos().get(i).getName().equals(produtos[0].getName())){
                                        existent_carrinho.getProdutos().get(i).setQuantidade(existent_carrinho.getProdutos().get(i).getQuantidade() + produtos[0].getQuantidade());
                                        added = true;
                                        break;
                                    }
                                }
                                //se não existe
                                if(added == false) {
                                    existent_carrinho.getProdutos().add(produtos[0]);
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
            return produtos[0].getName() + " " + produtos[0].getQuantidade() + "x ";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(main_menu.this);
            builder.setCancelable(false);
            builder.setTitle(Html.fromHtml("<font color='#509324'>Sucesso</font>"));
            builder.setMessage( s + " adicionado(s) ao carrinho!");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }

    }
}