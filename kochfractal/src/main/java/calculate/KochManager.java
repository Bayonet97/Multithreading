/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fun3kochfractalfx.FUN3KochFractalFX;
import timeutil.TimeStamp;

public class KochManager {
    
    private KochFractal koch;
    private ArrayList<Edge> edges;
    private FUN3KochFractalFX application;
    private TimeStamp tsCalc;
    private TimeStamp tsDraw;
    // TODO: Instantiate each KochFractal instance in Thread from Threadpool. Countdown when a thread is done. Interrupt calculations when next level is pressed.
    private KochFractal leftCalculator;
    private KochFractal bottomCalculator;
    private KochFractal rightCalculator;
    private ExecutorService executorService;
    private CountDownLatch threadCountDown;

    public KochManager(FUN3KochFractalFX application) {
        this.edges = new ArrayList<Edge>();
        this.koch = new KochFractal(this);
        this.application = application;
        this.tsCalc = new TimeStamp();
        this.tsDraw = new TimeStamp();
        executorService = Executors.newFixedThreadPool(3);
    }
    
    public void changeLevel(int nxt){
        if(leftCalculator != null && bottomCalculator != null && rightCalculator != null){
            leftCalculator.cancel();
            bottomCalculator.cancel();
            rightCalculator.cancel();
        }

        edges.clear();
        koch.setLevel(nxt);
        tsCalc.init();
        CountDownLatch countDownLatch = new CountDownLatch(3);
        leftCalculator = new KochFractal(this);

        bottomCalculator = new KochFractal(this);

        rightCalculator = new KochFractal(this);


        tsCalc.setBegin("Begin calculating");

        executorService.execute(new Thread(new Runnable() {
            @Override
            public void run() {
                leftCalculator.generateLeftEdge();
            }
        }));
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                bottomCalculator.generateBottomEdge();
            }
        });
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                rightCalculator.generateRightEdge();
            }
        });
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
