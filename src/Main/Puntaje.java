/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Main;

/**
 *
 * @author Szerch
 */
public class Puntaje {
    
    private String nombre;
    private int puntaje;
    
    public Puntaje(){
        nombre = "";
        puntaje = 0;
    }
    
    public Puntaje(String nombre, int puntaje){
        this.nombre = nombre;
        this.puntaje = puntaje;
    }
    
    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    
    public void setPuntaje(int puntaje){
        this.puntaje = puntaje;
    }
    
    public String getNombre(){
        return nombre;
    }
    
    public int getPuntaje(){
        return puntaje;
    }
    
    public String toString(){
        return "" + getPuntaje() + "," + getNombre();
    }
}
