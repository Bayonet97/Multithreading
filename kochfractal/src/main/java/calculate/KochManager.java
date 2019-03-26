/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import fun3kochfractalfx.FUN3KochFractalFX;
import timeutil.TimeStamp;

/**
 *
 * @author Nico Kuijpers
 * Modified for FUN3 by Gertjan Schouten
 */
public class KochManager {
    
    private KochFractal koch;
    private ArrayList<Edge> edges;
    private FUN3KochFractalFX application;
    private TimeStamp tsCalc;
    private TimeStamp tsDraw;

    public KochManager(FUN3KochFractalFX application) {
        this.edges = new ArrayList<Edge>();
        this.koch = new KochFractal(this);
        this.application = application;
        this.tsCalc = new TimeStamp();
        this.tsDraw = new TimeStamp();
    }
    
    public void changeLevel(int nxt){
        edges.clear();
        koch.setLevel(nxt);
        tsCalc.init();
        tsCalc.setBegin("Begin calculating");
        final int[] count = {0};

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    koch.generateLeftEdge();
                    Thread.sleep(200);
                    count[0]++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    koch.generateBottomEdge();
                    Thread.sleep(200);
                    count[0]++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    koch.generateRightEdge();
                    Thread.sleep(200);
                    count[0]++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


        t1.start();
        t2.start();
        t3.start();
        /*
        try {
            synchronized (t1) {
                System.out.println("Waiting for t1 to complete...");
                t1.wait();
            }
            synchronized (t2) {
                System.out.println("Waiting for t2 to complete...");
                t2.wait();
            }
            synchronized (t3) {
                System.out.println("Waiting for t3 to complete...");
                t3.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
        do{}while(t1.isAlive() && t2.isAlive() && t3.isAlive());

            tsCalc.setEnd("End calculating");
            application.setTextNrEdges("" + koch.getNrOfEdges());
            application.setTextCalc(tsCalc.toString());

            application.requestDrawEdges();
    }

    public void drawEdges() {
        tsDraw.init();
        tsDraw.setBegin("Begin drawing");
        application.clearKochPanel();
        for (Edge e : edges) {
            application.drawEdge(e);
        }
        tsDraw.setEnd("End drawing");
        application.setTextDraw(tsDraw.toString());
    }
    
    public void addEdge(Edge e) {
        edges.add(e);
    }
}
