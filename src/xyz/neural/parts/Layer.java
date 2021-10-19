package xyz.neural.parts;

import xyz.neural.matrices.Vector;

import java.util.LinkedList;
import java.util.function.Function;

public class Layer {
    private LinkedList<Neuron> neurons = new LinkedList<>();
    private float a = 1f;
    private Function<Float, Float> function = x -> 1/(1+(float) Math.exp(-a*x));
    private Function<Float, Float> derivative = x -> (float) (Math.exp(-a*x) * a)/(float) Math.pow(Math.exp(-a*x) + 1, 2);

    public Layer(int nbEntries, int nbNeurons){
        for(int i = 0 ; i < nbNeurons ; i++){
            this.neurons.add(new Neuron(nbEntries, this.function, this.derivative));
        }
    }

    public Vector vectorialDsigmoid(Vector v){
        LinkedList<Float> coos = new LinkedList<>();
        for(int i = 0 ; i < v.size() ; i ++){
            coos.add(this.derivative.apply(v.get(i)));
        }
        return Vector.vectorialize(coos);
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof Layer){
            return ((Layer) obj).getNeurons().containsAll(this.getNeurons());
        }
        return false;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.neurons.size()).append("/");
        return sb.toString();
    }

    public Layer(LinkedList<Neuron> neurons){
        this.neurons = neurons;
    }

    public int size(){
        return this.neurons.size();
    }

    public Neuron getNeuron(int index){
        return this.neurons.get(index);
    }

    public LinkedList<Neuron> getNeurons() {
        return neurons;
    }

    public Vector feedForward(Vector entries){
        Vector outs = new Vector(this.size());
        for(int i = 0 ; i < this.size() ; i++){
            outs.set(i, this.getNeuron(i).feedForward(entries));
        }
        return outs;
    }

    public Vector getWeightsConnected(int index){
        LinkedList<Float> weights = new LinkedList<>();
        for (Neuron neuron : this.neurons) {
            weights.add(neuron.getWeight(index));
        }
        return Vector.vectorialize(weights);
    }

    public Vector sumForward(Vector entries){
        Vector outs = new Vector(this.size());
        for(int i = 0 ; i < this.size() ; i++){
            outs.set(i, this.getNeuron(i).weightedSum(entries));
        }
        return outs;
    }
}
