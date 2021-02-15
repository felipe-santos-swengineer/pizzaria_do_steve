package br.com.ufc.pizzaria_do_steve;

import java.util.ArrayList;
import java.util.logging.Handler;

public class Historico {
    String id;
    ArrayList<Pedido> pedidos;

    public Historico(){}

    public Historico(String id, ArrayList<Pedido> pedidos) {
        this.id = id;
        this.pedidos = pedidos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(ArrayList<Pedido> pedidos) {
        this.pedidos = pedidos;
    }
}

