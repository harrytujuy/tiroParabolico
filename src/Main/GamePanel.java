/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Main;

import Objetos.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.JFrame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import java.util.Vector;


/**
 *
 * @author Szerch
 */
public class GamePanel extends JFrame implements Runnable, KeyListener, MouseListener{
    
    //Objetos
    private Cubeta cubeta;
    private Bola bola;
    private Pared pared;
    
    //Variables bola
    double x_init;
    double y_init;
    double acel;
    double gravedad;
    double x_tiro;
    double y_tiro;
    int angulo;
    double vx;
    double vy;
    double vxi;
    double vyi;
    double escalaX;
    double escalaY;
    long timeStart;
    
    //Variables cubeta
    int x_pos;
    int y_pos;
    int dx;
    int dy;
    private boolean tiro;
    
    //Variables generales
    private boolean pausa;
    private boolean running;
    private int score;
    private Graphics dbg;
    private Image dbImage;
    private int fps;
    private long targetTime;
    private int height;
    private int width;
    
    
    public GamePanel(){
        
        fps = 60;
        targetTime = 1000/fps;
        score = 0;
        width = 640;
        height = 480;
        pausa = false;
        
        setSize(width,height);
        setBackground(Color.WHITE);
        addKeyListener(this);
        addMouseListener(this);
        
        x_pos = 550;
        y_pos = 400;
        dx = dy = 0;
        cubeta = new Cubeta(x_pos, y_pos, Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("images/cubeta.png")));
        
        x_init = 60;
        y_init = 255;
        acel = 2;
        tiro = false;
        gravedad = 1;
        vxi = (int) (Math.random()*(5-3+1)+3);
        vyi = (int) (Math.random()*(5-3+1)+3);
        bola = new Bola((int)x_init, (int)y_init, Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("images/bola.png")));
        
        pared = new Pared(0,294, Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("images/pared.png")));
        
        Thread th = new Thread(this);
        th.start();
        running = true;
    }

    @Override
    public void run() {
        long start, elapsed, wait;
        while(running){
            start = System.nanoTime();
            elapsed = System.nanoTime() - start;
            wait = targetTime - elapsed/1000000;
            if(wait < 0)
                wait = 5;
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
        }
    }
    
    public void actualiza(){
        
        cubeta.setPosX(cubeta.getPosX()+dx);
        
        if(tiro){
            calculaParabola();
            bola.setPosX((int)(bola.getPosX()+x_tiro));
            bola.setPosY((int)(bola.getPosY()+y_tiro));
        }
    }
    
    public void checaColision(){
        
        if(cubeta.intersecta(pared)){
            dx = 3;
        }
        if(cubeta.getPosX() + cubeta.getAncho() >= width)
            dx = -3;
        if(cubeta.intersecta(bola)){
            bola.setPosX((int)x_init);
            bola.setPosY((int)y_init);
            score++;
        }
        if(bola.getPosY() + bola.getAlto() >= height){
            bola.setPosX((int)x_init);
            bola.setPosY((int)y_init);
            tiro = false;
        }           
    }
    
    public void calculaParabola(){
        double velocidad = 1;
        long timeElapsed;
        double timeSeconds;
        
        timeElapsed = System.nanoTime() - timeStart;
        timeSeconds = (timeElapsed/1000.0)*fps;
        
        System.out.println(timeSeconds);
        
        vx = (velocidad*escalaX);
        vy = (velocidad*escalaY) - gravedad*timeSeconds;
        
        x_tiro = vx * timeSeconds;
        y_tiro = (velocidad*escalaY*timeSeconds) - (gravedad*pow(timeSeconds,2)/2);
        
    }
    
    public void paint1(Graphics g){
        //Pintamos bueno y malos
        if(cubeta != null && bola != null && pared != null){
            g.drawImage(cubeta.getImagenI(), cubeta.getPosX(), cubeta.getPosY(), this);
            g.drawImage(bola.getImagenI(), bola.getPosX(), bola.getPosY(), this);
            g.drawImage(pared.getImagenI(), pared.getPosX(), pared.getPosY(), this);
            g.drawString("Score: "+score, 0, 0);
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

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_LEFT)
            dx=-3;
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
            dx=3;
        else if(e.getKeyCode() == KeyEvent.VK_P)
            pausa = !pausa;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_LEFT)
            dx=0;
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
            dx=0;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        angulo = 30;
        x_tiro = y_tiro = 0;
        escalaX = Math.cos(angulo);
        escalaY = Math.sin(angulo);
        timeStart = System.currentTimeMillis();
        tiro = true;
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
