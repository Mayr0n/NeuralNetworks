package xyz.neural.matrices;

import java.util.Arrays;
import java.util.LinkedList;

public class Matrix {
    private LinkedList<LinkedList<Float>> matrix;
    /*
        [[],
         [],
         [],
         [],
         []]
     */

    public Matrix(int rows, int columns){
        this.matrix = new LinkedList<>();
        for(int r = 0 ; r < rows ; r++){
            LinkedList<Float> line = new LinkedList<>();
            for(int c = 0 ; c < columns ; c++){
                line.add(0f);
            }
            this.matrix.add(line);
        }
    }

    public Matrix(LinkedList<LinkedList<Float>> matrix){
        this.matrix = matrix;
    }

    public Matrix(Float[][] matrix){
        this.matrix = new LinkedList<>();
        for (Float[] floats : matrix) {
            LinkedList<Float> line = new LinkedList<>(Arrays.asList(floats).subList(0, matrix[0].length));
            this.matrix.add(line);
        }
    }


    public LinkedList<Float> getLine(int index){
        return this.matrix.get(index);
    }

    public LinkedList<Float> getColumn(int index){
        LinkedList<Float> column = new LinkedList<>();
        for(LinkedList<Float> line : this.matrix){
            column.add(line.get(index));
        }
        return column;
    }

    public void setElement(int x, int y, float element){
        assert this.matrix.get(y).size() > y;
        this.matrix.get(y).set(x, element);
    }

    public float getElement(int x, int y){
        return this.matrix.get(y).get(x);
    }

    public LinkedList<LinkedList<Float>> getMatrix() {
        return this.matrix;
    }

    public int getNumberColumns(){
        return this.matrix.get(0).size();
    }

    public int getNumberLines(){
        return this.matrix.size();
    }

    public static Matrix multiply(Matrix m1, Matrix m2){
        assert m1.getNumberColumns() == m2.getNumberLines();
        Matrix m3 = new Matrix(m1.getNumberColumns(), m2.getNumberLines());
        for(int l = 0 ; l < m1.getNumberLines() ; l++){
            LinkedList<Float> line = m1.getLine(l);
            for(int c = 0 ; c < m2.getNumberColumns() ; c++) {
                LinkedList<Float> column = m2.getColumn(c);
                float r = 0;
                for (int c1 = 0 ; c1 < column.size() ; c1++) {
                    r += column.get(c1) * line.get(c1);
                }
                m3.setElement(c, l, r);
            }
        }
        return m3;
    }

    public static Matrix multiply(float lambda, Matrix m){
        Matrix m1 = new Matrix(m.getNumberLines(), m.getNumberColumns());
        for(int i = 0 ; i < m.getNumberLines() ; i++){
            for(int j = 0 ; j < m.getNumberColumns() ; j++){
                m1.setElement(i, j, lambda * m.getElement(i, j));
            }
        }
        return m1;
    }

    public static Matrix add(Matrix m1, Matrix m2){
        assert m1.getNumberLines() == m2.getNumberLines() && m1.getNumberColumns() == m2.getNumberColumns();
        Matrix m3 = new Matrix(m1.getNumberLines(), m1.getNumberColumns());
        for(int i = 0 ; i < m1.getNumberLines() ; i++){
            for(int j = 0 ; j < m1.getNumberColumns() ; j++){
                m3.setElement(i, j, m1.getElement(i, j) + m2.getElement(i, j));
            }
        }
        return m3;
    }

    public static float sum(LinkedList<Float> list){
        float s = 0f;
        for(float f : list){
            s += f;
        }
        return s;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i = 0 ; i < this.getNumberLines() ; i++){
            sb.append("[");
            for(int j = 0 ; j < this.getNumberColumns() ; j++){
                sb.append(this.getElement(j, i)).append(",");
            }
            sb.deleteCharAt(sb.toString().length() - 1);
            sb.append("]\n");
        }
        sb.deleteCharAt(sb.length()-1).append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof Matrix){
            return ((Matrix) obj).getMatrix().containsAll(this.getMatrix());
        }
        return false;
    }
}
