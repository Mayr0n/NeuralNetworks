package xyz.neural.matrices;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Vector extends Matrix {

    public Vector(int rows) {
        super(rows, 1);
    }

    public Vector(Float[][] coos){
        super(coos);
    }

    public static Vector vectorialize(LinkedList<Float> elements){
        Vector m = new Vector(elements.size());
        for(int i = 0; i < elements.size() ; i++){
            m.set(i, elements.get(i));
        }
        return m;
    }

    public void set(int y, float element){
        assert this.getMatrix().size() > y;
        this.getMatrix().get(y).set(0, element);
    }

    public float get(int y){
        assert this.getMatrix().size() > y;
        return this.getMatrix().get(y).get(0);
    }
    
    public int size(){
        return this.getMatrix().size();
    }

    public static float scalaire(Vector v1, Vector v2){
        assert v1.size() == v2.size();
        return Vector.hadamard(v1, v2).sum();
    }
    
    public float sum(){
        float sum = 0;
        for(int i = 0 ; i < this.size() ; i++){
            sum += this.get(i);
        }
        return sum;
    }
    
    public Vector multiply(float f){
        Vector v2 = new Vector(this.size());
        for(int i = 0 ; i < this.size() ; i++){
            v2.set(i, this.get(i)*f);
        }
        return v2;
    }
    
    public static Vector addVector(Vector v1, Vector v2){
        assert v1.size() == v2.size();
        Vector v3 = new Vector(v1.size());
        for(int i = 0 ; i < v3.size() ; i++){
            v3.set(i, v1.get(i) + v2.get(i));
        }
        return v3;
    }

    public static Vector substract(Vector v1, Vector v2){
        assert v1.size() == v2.size();
        Vector v3 = new Vector(v1.size());
        for(int i = 0 ; i < v3.size() ; i++){
            v3.set(i, v1.get(i) - v2.get(i));
        }
        return v3;
    }
    
    @Override
    public boolean equals(Object obj){
        if(obj instanceof Vector && ((Vector) obj).size() == this.size()){
            Vector v2 = (Vector) obj;
            for(int i = 0 ; i < this.size() ; i++){
                if(this.get(i) != v2.get(i)){
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static Vector hadamard(Vector v1, Vector v2){
        assert v1.size() == v2.size();
        Vector m3 = new Vector(v1.getNumberLines());
        for(int i = 0 ; i < v1.getNumberLines() ; i++){
            m3.set(i, v1.get(i) * v2.get(i));
        }
        return m3;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("\n(");
        for(int i = 0 ; i < this.size() ; i++){
            sb.append(this.get(i)).append(",\n");
        }
        sb.delete(sb.length() - 2, sb.length()).append(")\n");
        return sb.toString();
    }
}
