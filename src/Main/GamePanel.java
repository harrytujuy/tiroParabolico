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


public class GamePanel extends JFrame implements Runnable, KeyListener, MouseListener{
    
    //Objetos
    private final Cubeta cubeta;    //Se declara objeto cubeta
    private final Bola bola;        //Se declara objeto bola
    private final Pared pared;      //Se declara objeto pared
    private final SoundClip cheer;  //Se declara sonido de atrapada
    private final SoundClip boo;    //Se declara sonido de perdida
    private Vector vec;             //Se declara objeto vector
    private Graphics dbg;           //Se declara el objeto dbg
    private Image dbImage;          //Se declara el objeto dbImage
    
    //Variables bola
    private int x_init;             //Se declara posicion inicial en x de bola
    private int y_init;             //Se declara posicion inicial en y de bola
    private int x_tiro;             //Se declara aumento en x de bola
    private int y_tiro;             //Se declara aumento en y de bola
    private int vxi;                //Se declara velocidad en x de bola
    private int hmax;               //Se declara altura maxima de bola
    private int dif;                //Se declara dificultad de atrapada
    
    //Variables cubeta
    private final int x_pos;        //Se declara posicion inicial en x de cubeta
    private final int y_pos;        //Se declara posicion inicial en y de cubeta
    private int dx;                 //Se declara aumento en x de cubeta
    private int direccion;          //Se declara la direccion que tendra la cubeta
    private boolean choqueDer;      //Se declara el boleano que controle las colisiones derechas de la cubeta
    private boolean choqueIzq;      //Se declara el boleano que controle las colisiones izquierdas de la cubeta
    
    //Variables generales
    private boolean pausa;          //Se declara la variable pausa
    private boolean running;        //Se declara la variable running
    private boolean sonidos;        //Se declara la variable sonidos
    private boolean tiro;           //Se declara la variable tiro
    private boolean instrucciones;  //Variable de verifciacion para mostrar las instrucciones
    private int score;              //Se declara la variable score
    private int combo;              //Se declara la variable combo
    private int vidas;              //Se declara la variable vidas
    private int fail;               //Se decalara la variable de conteo de veces que no se atrapa la pelota
    private final int fps;          //Se declara la variable fps (frames per second)
    private long targetTime;        //Se declara la variable targetTime
    private final int height;       //Se declara la variable height
    private final int width;        //Se declara la variable width
    private String nombreArchivo;   //Se declara la variable nombreArchivo
    private String[] arr;           //Se declara el arreglo arr
    private Image imagenFondo;      //Se declara la variable que contendra la imagen de fondo
    private Image imagenIns;        //Se declara la variable que contendra la imagen de las instrucciones
    
    public GamePanel(){
        
        //Se inicializan variables generales
        fps = 60;
        targetTime = 1000/fps;
        score = 0;                          //Se inicializa el score en 0
        combo = 0;                          //Se inicializa el combo en 0
        vidas = 5;                          //Se definen las 5 vidas que tendra el usuario
        fail = 0;                           //Se inicializa en 0 la variable que cuenta las veces que no atrapa la pelota
        width = 640;                        //Se define el ancho del jFrame en 640
        height = 480;                       //Se define el alto del jFrame en 480
        pausa = false;                      //Se define falsa la pausa (para no iniciar pausado)
        sonidos = true;                     //Se define verdadero a los sonidos (para que se escuchen)
        nombreArchivo = "Puntaje.txt";      //Se crea el archivo de puntaje
        vec = new Vector();
        imagenFondo = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("images/cuarto.png"));       //Se carga la imagen de fondo
        imagenIns = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("images/instrucciones.png"));  //Se carga la imagen de instrucciones
        
        //Se da el tamaño de la ventana, se agregan los listeners de teclado y mouse
        setSize(width,height);
        addKeyListener(this);
        addMouseListener(this);
        
        //Se inicializa el objeto cubeta
        x_pos = 550;
        y_pos = 400;
        direccion = 0;
        dx = 0;
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
            //Si no esta pausado el juego ni mostrando las instrucciones se actualiza y checa colisiones
            if(!pausa || !instrucciones){
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
        switch(direccion){
            case 0:{                                    //Si la direccion es cero la cubeta se queda estática
                cubeta.setPosX(cubeta.getPosX());
                break;
            }
            case 1:{                                    //Si la direccion es uno la cubeta se mueve a la izquierda 
                cubeta.setPosX(cubeta.getPosX() - dx);
                break;
            }
            case 2:{                                    //Si la direccion es 2 la cubeta se mueve a la derecha
                cubeta.setPosX(cubeta.getPosX() + dx);
            }
        }
        
        //Movimiento de bola
        if(tiro){
            calculaParabola();
            bola.setPosX(bola.getPosX()+x_tiro);
            bola.setPosY(bola.getPosY()+y_tiro);
        }
    }
    
    public void checaColision(){
        
        //Verifica si la cubeta choca contra las paredes
        if(cubeta.intersecta(pared)){                       //Verifica si la cubeta choca contra la pared
            direccion = 0;                                  //Evita que la cubeta se siga moviendo
            choqueIzq = true;                               //Evita que la cubeta se siga moviendo a la izquierda
        }
        if(cubeta.getPosX() + cubeta.getAncho() >= width){  //Verifica si la cubeta choca contra el mueble
            direccion = 0;                                  //Evita que la cubeta se siga moviendo
            choqueDer = true;                               //Evita que la cubeta se siga moviendo a la derecha
        }
        
        //Verifica si la cubeta toca la bola
        if(cubeta.intersecta(bola)){
            bola.setPosX(x_init);
            bola.setPosY(y_init);
            score += 2;
            combo++;
            tiro = false;
            if(sonidos)
                cheer.play(); //Se reproduce el sonido de victoria
        }
        //Verifica si la bola cae al suelo
        if(bola.getPosY() + bola.getAlto() >= height){
            bola.setPosX(x_init);
            bola.setPosY(y_init);
            fail++;                                 //Se aumenta la cantidad de veces que ha fallado
            if(fail >= 3){                          //Se comprueba que haya fallado 3 veces
                vidas--;                            //Se disminuye una vida
                fail = 0;                           //El contador de fallas se reinicia
            }
            combo = 0;
            dif++;                                  //Aumenta la dificultad
            tiro = false;
            if(sonidos)
                boo.play();                         //Se reproduce el sonido de derrota
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
        //Se pinta el cuarto en el fondo
        g.drawImage(imagenFondo, 0, 0, getSize().width, getSize().height, this);
        //Pintamos cubeta, bola y pared
        if(cubeta != null && bola != null && pared != null){
            g.drawImage(cubeta.getImagenI(), cubeta.getPosX(), cubeta.getPosY(), this);
            g.drawImage(bola.getImagenI(), bola.getPosX(), bola.getPosY(), this);
            g.drawImage(pared.getImagenI(), pared.getPosX(), pared.getPosY(), this);
            g.setColor(Color.BLACK);
            g.setFont(new Font("Times New Roman",Font.PLAIN,18));
            g.drawString("Score: "+score, 520, 60);
            g.drawString("Combo: "+combo, 520, 80);
            g.drawString("Sonidos: "+ (sonidos ? "Si":"No"), 520, 100);
            g.drawString("Vidas: " +vidas,40,60);
            if(pausa){
                g.setColor(Color.BLACK);
                g.setFont(new Font("Times New Roman",Font.PLAIN,26));
                g.drawString("Juego Pausado", 320, 218);
            }
            if(instrucciones){
                g.drawImage(imagenIns, 100, 90, rootPane);
            }
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
        if(e.getKeyCode() == KeyEvent.VK_LEFT){
            if(!choqueIzq){
                direccion = 1;
                dx=3;
                choqueDer = false;
            }
        }
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
            if(!choqueDer){
                direccion = 2;
                dx=3;
                choqueIzq = false;
            }
        }
        else if(e.getKeyCode() == KeyEvent.VK_P)
            pausa = !pausa;
        else if(e.getKeyCode() == KeyEvent.VK_S)
            sonidos = !sonidos;
        else if(e.getKeyCode() == KeyEvent.VK_I)
            instrucciones = !instrucciones;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //Casos para cada vez que se suelta cierta tecla
        if(e.getKeyCode() == KeyEvent.VK_LEFT){
            direccion = 0;
            dx=0;
        }
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
            direccion = 0;
            dx=0;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //Si se da un click con el mouse
        vxi = (int) (Math.random()*(3-1+1)+1); //Velocidad en x es aleatoria
        hmax = (int) (Math.random()*(80-50+1)+50); //Altura maxima es aleatoria
        tiro = true; //Inicia el proceso de tiro parabolico
        //*(80-50+1)+50
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
