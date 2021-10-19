package xyz.neural.parts;

import com.sun.java.accessibility.util.Translator;
import xyz.neural.Forminator;
import xyz.neural.PatternManager;
import xyz.neural.matrices.Vector;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class NeuralNetwork {
    public float learningRate = 1f;
    public float inertie = 0.5f;
    private LinkedList<Layer> layers = new LinkedList<>();

    public NeuralNetwork(LinkedList<Integer> compo, int nbEntries) {
        for (int i = 0; i < compo.size(); i++) {
            if (i == 0) {
                this.layers.add(new Layer(nbEntries, compo.get(i)));
            } else {
                this.layers.add(new Layer(this.layers.get(i - 1).size(), compo.get(i)));
            }
        }
    }

    public static NeuralNetwork load(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        List<String> lines = br.lines().collect(Collectors.toList()); // ordre ?
        LinkedList<Layer> nne = new LinkedList<>();
        for (String line : lines) {
            LinkedList<Neuron> la = new LinkedList<>();
            for (String neu : line.split(" \\| ")) {
                float bias = Float.parseFloat(neu.split("/")[1]);
                LinkedList<Float> weights = new LinkedList<>();
                for (String we : neu.split(" / ")[0].split(" ")) {
                    if(!we.equals(" ")) weights.add(Float.parseFloat(we));
                }
                la.add(new Neuron(weights, bias, null, null));
            }
            nne.add(new Layer(la));
        }
        return new NeuralNetwork(nne);
    }

    public NeuralNetwork(LinkedList<Layer> compo) {
        this.layers = compo;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    public Layer getLayer(int i) {
        return this.layers.get(i);
    }

    public Neuron getNeuron(int x, int y) {
        return this.layers.get(x).getNeuron(y);
    }

    public LinkedList<Layer> getLayers() {
        return layers;
    }

    public int size() {
        return this.layers.size();
    }

    public Vector feedForward(LinkedList<Float> entries) {
        return this.getFeedResults(Vector.vectorialize(entries)).getLast();
    }

    public Vector feedForward(Vector entries) {
        return this.getFeedResults(entries).getLast();
    }

    public LinkedList<Vector> getFeedResults(Vector ents) { // permet de récupérer tous les a : sorties des neurones
        assert ents.size() == this.layers.get(0).getNeuron(0).getWeights().size();
        LinkedList<Vector> results = new LinkedList<>();
        results.add(this.layers.get(0).feedForward(ents));
        for (int i = 1; i < this.size(); i++) {
            results.add(this.layers.get(i).feedForward(results.get(i - 1)));
        }
        return results;
    }

    public LinkedList<Vector> getWeightedSums(Vector ents) { // permet de récupérer tous les z : sommes pondérées
        assert ents.size() == this.layers.get(0).getNeuron(0).getWeights().size();
        LinkedList<Vector> results = new LinkedList<>();
        results.add(this.layers.get(0).sumForward(ents));
        for (int i = 1; i < this.size(); i++) {
            results.add(this.layers.get(i).sumForward(results.get(i - 1)));
        }
        return results;
    }

    public float cost(Vector entries, Vector targets) {
        Vector els = Vector.substract(this.feedForward(entries), targets);
        return Vector.hadamard(els, els).sum() / els.getNumberLines();
    }

    public Vector dcost(Vector y, Vector t) {
        return Vector.substract(y, t).multiply(2);
    }

    private Vector getRandomKey(Hashtable<Vector, Vector> pattern) {
        LinkedList<Vector> keys = new LinkedList<>(pattern.keySet());
        return keys.get(new Random().nextInt(keys.size()));
    }

    public void startOnlineTrainingProgram(PatternManager t, int nb, boolean counter, boolean debugger) {
        System.out.println("Début de l'entrainement...");
        LinkedList<LinkedList<Vector>> deltaWIJL = new LinkedList<>();
        for (int l = 0; l < this.size(); l++) {
            LinkedList<Vector> li = new LinkedList<>();
            for(int n = 0 ; n < this.getLayer(l).size() ; n++){
                Neuron ne = this.getLayer(l).getNeuron(n);
                li.add(Vector.addVector(ne.getWeights(), ne.getWeights().multiply(-1)));
            }
            deltaWIJL.add(li);
        }

        for (int i = 1; i <= nb; i++) {
            if(counter) System.out.println(i);
            Vector key = t.getRandomKey();
            LinkedList<Vector> deltaWeights = train(key, t.getPattern().get(key), debugger);

            //deltaWIJL = LinkedList<LinkedList<Vector>>
            for (int l = 0; l < this.size(); l++) {
                LinkedList<Vector> li = deltaWIJL.get(l);
                for(int n = 0 ; n < this.getLayer(l).size() ; n++){
                    Neuron ne = this.getLayer(l).getNeuron(n);
                    li.set(n, Vector.addVector(li.get(n), ne.getWeights().multiply(-1)));
                }
                deltaWIJL.set(l, li);
            }

            this.changeWeights(key, deltaWeights, deltaWIJL);
            if(debugger) {
                System.out.println("Obtenu : " + this.feedForward(key));
                System.out.println("Voulu : " + t.getPattern().get(key));
            }
        }
        System.out.println("Entrainement terminé.");
    }

    public int test(boolean debugger){
        Forminator t = new Forminator();
        int nb = 0;
        for(int i = 0 ; i < 100 ; i++){
            Vector key = t.getRandomKey();
            float c = cost(key, t.getPattern().get(key));
            if(c < 0.01){
                nb++;
            }
            if(debugger){
                System.out.println("Théorie : " + t.getPattern().get(key));
                System.out.println("Obtenu : " + this.feedForward(key));
            }
        }
        return nb;
    }

    public void save(String path) {
        File file = new File(path);
        try (FileWriter fw = new FileWriter(file)) {
            StringBuilder sb = new StringBuilder();
            for (Layer l : this.layers) {
                sb.append("\n");
                for (Neuron n : l.getNeurons()) {
                    sb.append("{");
                    for (float w : n.getWeights().getColumn(0)) {
                        sb.append(w).append(" ");
                    }
                    sb.deleteCharAt(sb.length() - 1).append("} ");
                }
            }
            fw.write(sb.toString());
        } catch (IOException ignored) {
        }
    }

    public LinkedList<Vector> train(Vector entries, Vector targets, boolean debugger) {
        LinkedList<Vector> aS = this.getFeedResults(entries);
        LinkedList<Vector> zS = this.getWeightedSums(entries);

        LinkedList<Vector> deltas = new LinkedList<>();
        for (int i = 0; i < this.size(); i++) {
            deltas.add(new Vector(this.getLayer(i).size()));
        }

        for (int l = this.size() - 1; l >= 0; l--) {
            Layer layer = this.getLayer(l);
            Vector deltalv = layer.vectorialDsigmoid(zS.get(l));
            if(debugger) System.out.println(deltalv);

            if (l == this.size() - 1) {
                deltas.set(l, Vector.hadamard(deltalv, this.dcost(aS.getLast(), targets)));
            } else {
                LinkedList<Float> ws = new LinkedList<>();
                for (int i = 0; i < layer.size(); i++) {
                    ws.add(Vector.scalaire(this.layers.get(l + 1).getWeightsConnected(i), deltas.get(l + 1)));
                }
                deltas.set(l, Vector.hadamard(deltalv, Vector.vectorialize(ws)));
            }
        }
        return deltas;
    }

    public void changeWeights(Vector ents, LinkedList<Vector> deltas, LinkedList<LinkedList<Vector>> deltaWIJL) { // rappel : 1 delta = 1 neurone
        LinkedList<Vector> aS = this.getFeedResults(ents);

        for (int l = 0; l < this.size(); l++) { // défile toutes les couches
            Layer layer = this.getLayer(l); // couche actuelle
            for (int i = 0; i < layer.size(); i++) { // défile tous les neurones de la couche actuelle
                Neuron neuron = layer.getNeuron(i); // neurone actuel
                float deltali = deltas.get(l).get(i); // delta du neurone actuel
                Vector deltaW;
                if (l == 0) {
                    deltaW = ents.multiply(this.learningRate * deltali);
                } else {
                    deltaW = aS.get(l - 1).multiply(this.learningRate * deltali);
                }
                deltaW = Vector.addVector(deltaW, deltaWIJL.get(l).get(i).multiply(1-this.inertie));
                neuron.setWeights(Vector.substract(neuron.getWeights(), deltaW));
                neuron.setBias(neuron.getBias() - this.learningRate * deltali);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Layer l : this.getLayers()) {
            sb.append("--------------------------------------\n");
            for (Neuron n : l.getNeurons()) {
                sb.append("{");
                for (int w = 0; w < n.getWeights().getNumberLines(); w++) {
                    sb.append(n.getWeight(w)).append(",\n");
                }
                sb.append("}\n");
            }
            sb.append("--------------------------------------\n\n");
        }
        return sb.toString();
    }
}
