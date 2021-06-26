package xyz.neural.parts;

import xyz.neural.matrices.Matrix;

import java.util.LinkedList;

public class Layer {
    private LinkedList<Neuron> neurons = new LinkedList<>();

    public Layer(int nbEntries, int nbNeurons){
        for(int i = 0 ; i < nbNeurons ; i++){
            this.neurons.add(new Neuron(nbEntries, null, null));
        }
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
        sb.append("--------------------\n");
        for(Neuron n : this.neurons){
            sb.append(n.toString()).append("\n");
        }
        sb.append("--------------------\n");
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

    public Matrix feedForward(Matrix entries){
        Matrix outs = new Matrix(this.size(), 1);
        for(int i = 0 ; i < this.size() ; i++){
            outs.setElement(0, i, this.getNeuron(i).feedForward(entries));
        }
        return outs;
    }

    public Matrix sumForward(Matrix entries){
        Matrix outs = new Matrix(this.size(), 1);
        for(int i = 0 ; i < this.size() ; i++){
            outs.setElement(0, i, this.getNeuron(i).weightedSum(entries));
        }
        return outs;
    }
}
