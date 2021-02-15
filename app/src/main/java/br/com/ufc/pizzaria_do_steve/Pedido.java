package br.com.ufc.pizzaria_do_steve;

import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private String id;
    private ArrayList<Produto> produtos;
    private float valor;

    Pedido(){}

    public Pedido(String id, ArrayList<Produto> produtos) {
        this.id = id;
        this.produtos = produtos;

        for(int i = 0; i < produtos.size();i = i + 1){
            this.valor = this.valor + (produtos.get(i).getValor()*produtos.get(i).getQuantidade());
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Produto> getProdutos() {
        return produtos;
    }

    public void setProdutos(ArrayList<Produto> produtos) {
        this.produtos = produtos;
    }

    public float getValor() {
        return valor;
    }

    public void atualizarValor() {
        this.valor = 0;
        for(int i = 0; i < produtos.size();i = i + 1){
            this.valor = this.valor + (produtos.get(i).getValor()*produtos.get(i).getQuantidade());
        }
    }
}
