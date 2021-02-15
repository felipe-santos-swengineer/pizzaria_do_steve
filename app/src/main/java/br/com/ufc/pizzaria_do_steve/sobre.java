package br.com.ufc.pizzaria_do_steve;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;
import com.xwray.groupie.ViewHolder;

public class sobre extends AppCompatActivity {

    GroupAdapter adapter;
    RecyclerView rv_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);
        getSupportActionBar().setTitle("Sobre nós");

        rv_info = (RecyclerView) findViewById(R.id.rv_info);
        adapter = new GroupAdapter();
        rv_info.setAdapter(adapter);
        rv_info.setLayoutManager(new LinearLayoutManager(this));

        Info estabelecimento = new Info("Pizzaria do Steve","Inaugurada em 2021, oferecemos diversos tipos de pizzas para delivery");
        Info funcionamento = new Info("Funcionamento", "Segunda a domingo (Exceto feriados nacionais)");
        Info horario = new Info("Horário","18:00 às 23:00");
        Info entrega = new Info("Entrega", "Bairro 1, Bairro 2, Bairro 3 e Bairro 4");
        Info email = new Info("Email", "pzsteveoficial@gmail.com");
        Info contato = new Info("Contato","(88) 99837-8028");
        adapter.add(new InfoItem(estabelecimento));
        adapter.add(new InfoItem(funcionamento));
        adapter.add(new InfoItem(horario));
        adapter.add(new InfoItem(entrega));
        adapter.add(new InfoItem(email));
        adapter.add(new InfoItem(contato));


        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Item item, @NonNull View view) {
                InfoItem infoItem = (InfoItem) item;

            }
        });

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