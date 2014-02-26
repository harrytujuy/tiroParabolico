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
        GamePanel panel = new GamePanel();
        
        panel.setTitle("Tiro Parabolico");
        panel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel.setVisible(true);
    }
    
}
