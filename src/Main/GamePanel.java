/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Main;

import Objetos.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.JFrame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.Math.pow;
import java.util.Vector;
import javax.swing.JOptionPane;


/**
 *
 * @author Szerch
 */
public class GamePanel extends JFrame implements Runnable, KeyListener, MouseListener{
    
    //Objetos
    private final Cubeta cubeta;
    private final Bola bola;
    private final Pared pared;
    private final SoundClip cheer;
    private final SoundClip boo;
    private Vector vec;
    
    //Variables bola
    private int x_init;
    private int y_init;
    private int x_tiro;
    private int y_tiro;
    private int vxi;
    private int hmax;
    private int dif;
    
    //Variables cubeta
    private int x_pos;
    private int y_pos;
    private int dx;
    private int dy;
    private boolean tiro;
    
    //Variables generales
    private boolean pausa;
    private boolean running;
    private boolean sonidos;
    private int score;
    private int combo;
    private int vidas;
    private Graphics dbg;
    private Image dbImage;
    private final int fps;
    private long targetTime;
    private final int height;
    private final int width;
    private String nombreArchivo;
    private String[] arr;
    
    
    public GamePanel(){
        
        //Se inicializan variables generales
        fps = 60;
        targetTime = 1000/fps;
        score = 0;
        combo = 0;
        vidas = 5;
        width = 640;
        height = 480;
        pausa = false;
        sonidos = true;
        nombreArchivo = "Puntaje.txt";
        vec = new Vector();
        
        //Se da el tamaño y color de la ventana, se agregan los listeners de teclado y mouse
        setSize(width,height);
        setBackground(Color.WHITE);
        addKeyListener(this);
        addMouseListener(this);
        
        //Se inicializa el objeto cubeta
        x_pos = 550;
        y_pos = 400;
        dx = dy = 0;
        cubeta = new Cubeta(x_pos, y_pos, Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("images/cubeta.png")));
        
        //Se inicializa el objeto bola
        x_init = 60;
        y_init = 255;
        dif = 3;
        tiro = false;
        bola = new Bola(x_init, y_init, Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("images/bola.png")));
        
        //Se inicializa el objeto pared
        pared = new Pared(0,294, Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("images/pared.png")));
        
        //Se inicializan los sonidos
        cheer = new SoundClip("sounds/great.WAV");
        boo = new SoundClip("sounds/boo.wav");
        
        //Se crea el hilo e inicia
        Thread th = new Thread(this);
        th.start();
        running = true;
    }

    @Override
    public void run() {
        //Variables para controlar el tiempo de espera
        long start, elapsed, wait;
        while(running){
            start = System.nanoTime();
            elapsed = System.nanoTime() - start;
            wait = targetTime - elapsed/1000000;
            if(wait < 0)
                wait = 5;
            //Si no esta pausado el juego se actualiza y checa colisiones
            if(!pausa){
                actualiza();
                checaColision();
            }
            repaint();
            try{
                Thread.sleep(wait);
            }catch(Exception e){
                e.printStackTrace();
            }
            if(vidas == 0)
                running = false;
        }
        String nombre = JOptionPane.showInputDialog("Cual es tu nombre?");
        JOptionPane.showMessageDialog(null, "El puntaje de " + nombre + " es: " + score, "PUNTAJE", JOptionPane.PLAIN_MESSAGE);
        try{
            leeArchivo();
            vec.add(new Puntaje(nombre,score));
            grabaArchivo();
        }catch(Exception e){
            e.printStackTrace();
        }
        System.exit(0);
    }
    
    public void actualiza(){
        
        //Movimiento de cubeta
        cubeta.setPosX(cubeta.getPosX()+dx);
        
        //Movimiento de bola
        if(tiro){
            calculaParabola();
            bola.setPosX(bola.getPosX()+x_tiro);
            bola.setPosY(bola.getPosY()+y_tiro);
        }
    }
    
    public void checaColision(){
        
        //Verifica si la cubeta choca contra las paredes
        if(cubeta.intersecta(pared)){
            dx = 3;
        }
        if(cubeta.getPosX() + cubeta.getAncho() >= width)
            dx = -3;
        //Verifica si la cubeta toca la bola
        if(cubeta.intersecta(bola)){
            bola.setPosX(x_init);
            bola.setPosY(y_init);
            score++;
            combo++;
            tiro = false;
            if(sonidos)
                cheer.play(); //Se reproduce el sonido de victoria
        }
        //Verifica si la bola cae al suelo
        if(bola.getPosY() + bola.getAlto() >= height){
            bola.setPosX(x_init);
            bola.setPosY(y_init);
            vidas--;
            combo = 0;
            dif++; //Aumenta la dificultad
            tiro = false;
            if(sonidos)
                boo.play(); //Se reproduce el sonido de derrota
        }           
    }
    
    public void calculaParabola(){
        //Se crea el movimiento parabolico de la bola
        x_tiro = vxi;
        if(hmax > 30){
            y_tiro = -3;
            hmax--;
        }
        else if(hmax > 12){
            y_tiro = -2;
            hmax--;
        }
        else if(hmax > 0){
            y_tiro = -1;
            hmax--;
        }
        else
            y_tiro = dif;
    }
    
    public void paint1(Graphics g){
        //Pintamos cubeta, bola y pared
        if(cubeta != null && bola != null && pared != null){
            g.drawImage(cubeta.getImagenI(), cubeta.getPosX(), cubeta.getPosY(), this);
            g.drawImage(bola.getImagenI(), bola.getPosX(), bola.getPosY(), this);
            g.drawImage(pared.getImagenI(), pared.getPosX(), pared.getPosY(), this);
            g.setColor(Color.BLACK);
            g.setFont(new Font("Times New Roman",Font.PLAIN,18));
            g.drawString("Score: "+score, 540, 60);
            g.drawString("Combo: "+combo, 540, 80);
            g.drawString("Sonidos: "+ (sonidos ? "Si":"No"), 540, 100);
            g.drawString("Vidas: " +vidas,40,60);
            if(pausa)
                g.drawString("Juego Pausado", 480, 20);
        }
        else{
            g.drawString("No se cargo la imagen..", 20, 20);
        }
    }
    
    public void paint(Graphics g) {
	// Inicializan el DoubleBuffer
	if (dbImage == null){
		dbImage = createImage (this.getSize().width, this.getSize().height);
		dbg = dbImage.getGraphics ();
	}

	// Actualiza la imagen de fondo.
	dbg.setColor(getBackground ());
	dbg.fillRect(0, 0, this.getSize().width, this.getSize().height);

	// Actualiza el Foreground.
	dbg.setColor(getForeground());
	paint1(dbg);

	// Dibuja la imagen actualizada
	g.drawImage (dbImage, 0, 0, this);
    }
    
    public void leeArchivo() throws IOException{
        BufferedReader fileIn;
        try{
            fileIn = new BufferedReader(new FileReader(nombreArchivo));
        }catch(FileNotFoundException e){
            File puntos = new File(nombreArchivo);
            PrintWriter fileOut = new PrintWriter(puntos);
            fileOut.println("100,demo");
            fileOut.close();
            fileIn = new BufferedReader(new FileReader(nombreArchivo));
        }
        String dato = fileIn.readLine();
        while(dato != null){
            arr = dato.split(",");
            int num = (Integer.parseInt(arr[0]));
            String nom = arr[1];
            vec.add(new Puntaje(nom,num));
            dato = fileIn.readLine();
        }
        fileIn.close();
    }
    
    public void grabaArchivo() throws IOException{
        PrintWriter fileOut = new PrintWriter(new FileWriter(nombreArchivo));
        for(int i=0; i<vec.size(); i++){
            Puntaje x;
            x = (Puntaje) vec.get(i);
            fileOut.println(x.toString());
        }
        fileOut.close();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //Casos para cada vez que se oprime cierta tecla
        if(e.getKeyCode() == KeyEvent.VK_LEFT)
            dx=-3;
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
            dx=3;
        else if(e.getKeyCode() == KeyEvent.VK_P)
            pausa = !pausa;
        else if(e.getKeyCode() == KeyEvent.VK_S)
            sonidos = !sonidos;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //Casos para cada vez que se suelta cierta tecla
        if(e.getKeyCode() == KeyEvent.VK_LEFT)
            dx=0;
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
            dx=0;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //Si se da un click con el mouse
        vxi = (int) (Math.random()*(3-1+1)+1); //Velocidad en x es aleatoria
        hmax = (int) (Math.random()*(80-50+1)+50); //Altura maxima es aleatoria
        tiro = true; //Inicia el proceso de tiro parabolico
    }

    @Override
    public void mousePressed(MouseEvent e) {
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }
}
