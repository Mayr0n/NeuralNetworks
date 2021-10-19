package xyz.neural;

import xyz.neural.matrices.Vector;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Random;

public class Forminator implements PatternManager {
    private Hashtable<Vector, Vector> pattern;
    private LinkedList<Vector> keys;

    public Forminator(){
        File[] files = new File("figures_tests/").listFiles();
        Hashtable<File, Integer> prepp = new Hashtable<>();
        for(File file : files){
            prepp.put(file, Integer.valueOf(file.getName().split("_")[0]));
        }

        Hashtable<Vector, Vector> pattern = new Hashtable<>();
        LinkedList<File> filess = new LinkedList<>(prepp.keySet());

        for(File file : filess){
            try {
                BufferedImage img = ImageIO.read(file);
                LinkedList<Float> pixels = new LinkedList<>();
                for(int y = 0 ; y < img.getHeight() ; y++){ // balaie les pixels ligne par ligne
                    for(int x = 0 ; x < img.getWidth() ; x++){
                        pixels.add((float) new Color(img.getRGB(x, y)).getRed()/255);
                    }
                }
                LinkedList<Float> target = new LinkedList<>();
                target.add(prepp.get(file) == 1 ? 1f : 0f);
                target.add(prepp.get(file) == 2 ? 1f : 0f);
                target.add(prepp.get(file) == 3 ? 1f : 0f);

                pattern.put(Vector.vectorialize(pixels), Vector.vectorialize(target));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.pattern = pattern;
        this.keys = new LinkedList<>(pattern.keySet());
    }

    @Override
    public Hashtable<Vector, Vector> getPattern(){
        return this.pattern;
    }

    @Override
    public Vector getRandomKey(){
        return this.keys.get(new Random().nextInt(this.keys.size()));
    }
}
