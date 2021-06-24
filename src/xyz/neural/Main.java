package xyz.neural;

import xyz.neural.matrices.Matrix;
import xyz.neural.parts.NeuralNetwork;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        LinkedList<Integer> compo = new LinkedList<>();
        compo.add(2);
        compo.add(3);
        compo.add(1);
        NeuralNetwork nn = new NeuralNetwork(compo, 2);

        int nbEssais = 10000;

        for(int i = 0 ; i < nbEssais ; i++) {
            LinkedList<Float> entries = new LinkedList<>();
            entries.add(new Random().nextFloat());
            entries.add(new Random().nextFloat());

            LinkedList<Float> targets = new LinkedList<>();
            float x = entries.get(0);
            float y = entries.get(1);
            targets.add(x*y/(x+y));

            nn.train(entries, targets, nbEssais);
            System.out.println("Expected : " + targets.get(0));
            System.out.println("Got : " + nn.feedForward(entries));

        }
    }
}
