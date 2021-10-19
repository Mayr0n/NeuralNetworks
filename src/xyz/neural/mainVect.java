package xyz.neural;

import xyz.neural.matrices.Vector;

public class mainVect {

    public static void main(String[] args){
        Vector v1 = new Vector(new Float[][]{{1f}, {2f}});
        Vector v2 = new Vector(new Float[][]{{0f}, {5f}});

        System.out.println(v1.getMatrix());
    }
}
