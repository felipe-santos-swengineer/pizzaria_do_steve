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
import android.widget.ImageView;
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
import com.xwray.groupie.ViewHolder;

import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;



public class carrinho extends AppCompatActivity {

    GroupAdapter adapter;
    RecyclerView rv_carrinho;
    Pedido carrinho;
    String uid;
    TextView txt_valor_total;
    Button btn_finalizar;
    Boolean cart_empty = true;
    private long mLastClickTime = 0;
    User me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho);
        getSupportActionBar().setTitle("Carrinho");

        txt_valor_total = (TextView) findViewById(R.id.txt_valor_total);
        btn_finalizar = (Button) findViewById(R.id.btn_finalizar);

        rv_carrinho = (RecyclerView) findViewById(R.id.rv_carrinho);
        adapter = new GroupAdapter();
        rv_carrinho.setAdapter(adapter);
        rv_carrinho.setLayoutManager(new LinearLayoutManager(this));

        uid = FirebaseAuth.getInstance().getUid();
        FirebaseFirestore.getInstance().collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            me = documentSnapshot.toObject(User.class);
                        }
                    }
                });

        //preenche pela primeira vez
        prencherRecyclerView();

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
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
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
        //limpa rv
        cart_empty = true;
        adapter.clear();
        btn_finalizar.setVisibility(View.GONE);
        txt_valor_total.setVisibility(View.GONE);

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
                                btn_finalizar.setVisibility(View.VISIBLE);
                                txt_valor_total.setVisibility(View.VISIBLE);

                            }
                        }
                    }
                });
    }

    public void onClickBtnFinalizar(View v){
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        btn_finalizar.setVisibility(View.GONE);
        txt_valor_total.setVisibility(View.GONE);

        ArrayList<Pedido> pedidos = new ArrayList<>();
        pedidos.add(carrinho);
        Historico new_historico = new Historico(uid, pedidos);

        //salvar pedido no documento "historico" e deletar documento "carrinho"

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


        enviarEmail();

    }


    public void enviarEmail(){

        Properties props = new Properties();

        props.put("mail.smtp.host","smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable","true");

        String from = "pizzariadosteve2021@gmail.com";
        String pass = "stevelol";

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, pass);
            }
        });
        session.setDebug(true);

        try{
            Log.d("teste", "iniciando try ");

            //remetente
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));

            //destinario
            Log.d("teste",from + " to " + me.getEmail());
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(me.getEmail()));

            //corpo da msg
            String titulo = "Pedido recibido!";
            String texto = "a Pizzaria do Steve agradece sua preferência! Você pode consultar seu pedido direto no app.";
            message.setSubject(titulo);
            message.setText(texto);
            message.setSentDate(new Date());

            //enviar
            Log.d("teste", "indo enviar");
            new SendMail().execute(message);

         } catch (MessagingException e) {
                   Toast.makeText(this, e.getMessage(),Toast.LENGTH_SHORT).show();
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

    public void OnClickBtnConta(View v) {
        Intent intent;
        intent = new Intent(this, conta.class);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    private class SendMail extends AsyncTask<Message, String, String> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = progressDialog.show(carrinho.this, "Aguarde",
                    "confirmando pedido...", true, false);
        }

        @Override
        protected String doInBackground(Message... messages) {
            try{
                String from = "pizzariadosteve2021@gmail.com";
                String pass = "stevelol";

                Transport.send(messages[0]);
                return "Success";
            } catch (MessagingException e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if(s.equals("Success")){

                AlertDialog.Builder builder = new AlertDialog.Builder(carrinho.this);

                builder.setCancelable(false);
                builder.setTitle(Html.fromHtml("<font color='#509324'>Sucesso</font>"));
                builder.setMessage("Pedido confirmado, informamos por email(se email cadastrado exitir)");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
            else{
                Toast.makeText(carrinho.this, "Pedido confirmado, mas email não enviado",Toast.LENGTH_SHORT).show();

            }
        }
    }
}