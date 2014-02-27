package Main;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;
import java.io.IOException;
import java.net.URL;

public class SoundClip {
    
    private AudioInputStream sample;
    private Clip clip;
    private boolean looping = false;
    private int repeat = 0;
    private String filename = "";
    
    //Constructor sin parametros
    public SoundClip(){
        
        try{
            clip = AudioSystem.getClip();
        }catch(LineUnavailableException e){
            System.out.println("Error en: " +e.toString());
        }
    }
    //Constructor con parametros
    public SoundClip(String filename) {
        this();
        load(filename);
    }
    
    //Metodos modificadores
    public void setLooping(boolean looping){
        this.looping = looping;
    }
    
    public void setRepeat(int repeat){
        this.repeat = repeat;
    }
    
    public void setFilename(String filename){
        this.filename = filename;
    }
    
    //Metodos de acceso
    public Clip getClip(){
        return clip;
    }
    
    public boolean getLooping(){
        return looping;
    }
    
    public int getRepeat(){
        return repeat;
    }
    
    public String getFilename(){
        return filename;
    }
    
    public URL getURL(String filename){
        URL url = null;
        try{
            url = this.getClass().getResource(filename);
        }catch(Exception e){
            System.out.println("Error en: " +e.toString());
        }
        return url;
    }
    
    //Verificar si el archivo de audio esta cargado
    public boolean isLoaded(){
        return (boolean) (sample != null);
    }
    
    public boolean load(String audiofile){
        try{
            setFilename(audiofile);
            sample = AudioSystem.getAudioInputStream(getURL(filename));
            clip.open(sample);
            return true;
        }catch(IOException e){
            System.out.println("Error en: " +e.toString());
            return false;
        }catch(UnsupportedAudioFileException e){
            System.out.println("Error en: " +e.toString());
            return false;
        }catch(LineUnavailableException e){
            System.out.println("Error en: " +e.toString());
            return false;
        }
    }
    
    //Reproducir el archivo de audio
    public void play(){
        if(!isLoaded())
            return;
        clip.setFramePosition(0);
        if(looping)
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        else
            clip.loop(repeat);
    }
    
    //Parar el archivo de audio
    public void stop(){
        clip.stop();
    }
}
