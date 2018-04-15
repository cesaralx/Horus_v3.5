package com.example.alexi.horus_v35;

public class ItemData {


    private String title;
    private String fecha;

    public ItemData(String title,String fecha){

        this.title = title;
        this.fecha = fecha;
    }

    // getters & setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

}
