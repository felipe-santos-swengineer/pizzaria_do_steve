package br.com.ufc.pizzaria_do_steve;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class User implements Parcelable {

    private String id;
    private String name;
    private String fotoUrl;
    private String endereco;
    private String ref;
    private String email;
    private String senha;

    public User(){}

    public User(String id, String name, String fotoUrl, String endereco, String ref, String email, String senha) {
        this.id = id;
        this.name = name;
        this.fotoUrl = fotoUrl;
        this.endereco = endereco;
        this.ref = ref;
        this.email = email;
        this.senha = senha;

    }

    protected User(Parcel in) {
        id = in.readString();
        name = in.readString();
        fotoUrl = in.readString();
        endereco = in.readString();
        ref = in.readString();
        email = in.readString();
        senha = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(fotoUrl);
        dest.writeString(endereco);
        dest.writeString(ref);
        dest.writeString(email);
        dest.writeString(senha);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

}
