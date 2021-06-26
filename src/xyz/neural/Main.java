package xyz.neural;

import xyz.neural.parts.NeuralNetwork;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        try {
            SQLManager.setup("neural.db");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        LinkedList<Integer> compo = new LinkedList<>();
        compo.add(2);
        compo.add(4);
        compo.add(2);
        compo.add(1);
        NeuralNetwork nn = new NeuralNetwork(compo, 4);
        nn.save("test");
        NeuralNetwork nn2 = NeuralNetwork.load("test");

        LinkedList<Float> entries = new LinkedList<>();
        entries.add(0.4f);
        entries.add(0.1f);
        entries.add(0.5f);
        entries.add(0.9f);

        System.out.println(nn.feedForward(entries));
        System.out.println(nn2.feedForward(entries));
    }
}
