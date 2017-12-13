package com.restauranis.restauranis;

/**
 * Created by Joan on 23/11/2017.
 */

public class Restaurant {
    public int id;
    public String nombre;
    public String urlImage;
    public String precio;
    public String cocina;
    public String valoracion;

    public Restaurant(int id, String nombre, String urlImage, String precio, String cocina, String valoracion){
        this.id = id;
        this.nombre = nombre;
        this.urlImage = urlImage;
        this.precio = precio;
        this.cocina = cocina;
        this.valoracion = valoracion;
    }
}
