package br.com.ufc.pizzaria_do_steve;

public class Produto {
    private String id;
    private String name;
    private String ingredients;
    private float valor;
    private String foto_url;
    private float quantidade;

    public Produto(){ }

    public Produto(String id, String name, String ingredients, float valor, String foto_url, float quantidade) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.valor = valor;
        this.foto_url = foto_url;
        this.quantidade = quantidade;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public String getFoto_url() {
        return foto_url;
    }

    public void setFoto_url(String foto_url) {
        this.foto_url = foto_url;
    }

    public float getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(float quantidade) {
        this.quantidade = quantidade;
    }
}
