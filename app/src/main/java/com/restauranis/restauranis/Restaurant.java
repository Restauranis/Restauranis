package com.restauranis.restauranis;

/**
 * Created by Joan on 23/11/2017.
 */

public class Restaurant {
    public int id;
    public String nombre;
    public String urlImage;
    public int precio;
    public String cocina;
    public double valoracion;

    public Restaurant(int id, String nombre, String urlImage, int precio, String cocina, double valoracion){
        this.id = id;
        this.nombre = nombre;
        this.urlImage = urlImage;
        this.precio = precio;
        this.cocina = cocina;
        this.valoracion = valoracion;
    }

    public Restaurant(int id, String nombre){
        this.id = id;
        this.nombre = nombre;
    }

}
