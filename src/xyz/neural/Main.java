package xyz.neural;

import xyz.neural.parts.NeuralNetwork;

import java.util.LinkedList;

public class Main {

    public static void main(String[] args) {
        LinkedList<Integer> compo = new LinkedList<>();
        compo.add(3);

        NeuralNetwork nn = new NeuralNetwork(compo, 64);
        nn.learningRate = 0.1f;
        nn.inertie = 0;
        System.out.println("Pré-entraînement :" + nn.test(false) + "% de réussite");
        nn.startOnlineTrainingProgram(new Forminator(), 100000, true, false);
        System.out.println("Post-entraînement :" + nn.test(true) + "% de réussite");
        nn.save("wtf.txt");

    }
}
