package ch.ethz.topobench.graph.utility;

import java.util.Random;

public class SeedsChoice {

    public static void main(String args[]) {

        Random r = new Random();
        for (int i = 0; i < 10; i++) {
            System.out.println(Math.abs(r.nextInt()));
        }
    }


}
