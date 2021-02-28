package br.com.ufc.pizzaria_do_steve;

import java.util.ArrayList;

public class ListaInfo {
    ArrayList<Info> infos;

    public ListaInfo(){}

    public ListaInfo(ArrayList<Info> infos) {
        this.infos = infos;
    }

    public ArrayList<Info> getInfos() {
        return infos;
    }

    public void setInfos(ArrayList<Info> infos) {
        this.infos = infos;
    }
}
