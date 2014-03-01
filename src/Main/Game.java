/*
    Sergio Alejandro López Ruiz A00813432
    Fernando Gamboa López A00813314
*/

package Main;

import javax.swing.JFrame;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Szerch
 */
public class Game extends JFrame{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        GamePanel panel = new GamePanel();                      //Se crea el objeto GamePanel
        
        panel.setTitle("Tiro Parabolico");                      //Se crea titulo para ventana
        panel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   //Se cierra si se hace click en tacha
        panel.setVisible(true);                                 //Permite que la ventana sea visible
    }
    
}
