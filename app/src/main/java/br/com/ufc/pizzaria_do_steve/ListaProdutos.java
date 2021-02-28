package br.com.ufc.pizzaria_do_steve;

import java.util.ArrayList;

public class ListaProdutos {
    ArrayList<Produto> cardapio;

    public ListaProdutos(){}

    public ListaProdutos(ArrayList<Produto> cardapio) {
        this.cardapio = cardapio;
    }

    public ArrayList<Produto> getCardapio() {
        return cardapio;
    }

    public void setCardapio(ArrayList<Produto> cardapio) {
        this.cardapio = cardapio;
    }
}
