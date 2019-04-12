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
import javafx.application.Platform;
import timeutil.TimeStamp;

public class KochManager {

    private ArrayList<Edge> edges;
    private FUN3KochFractalFX application;
    private TimeStamp tsCalc;
    private TimeStamp tsDraw;
    private KochFractal leftCalculator;
    private KochFractal bottomCalculator;
    private KochFractal rightCalculator;
    private ExecutorService executorService;
    private CountDownLatch threadCountDown;

    public KochManager(FUN3KochFractalFX application) {
        this.edges = new ArrayList<Edge>();
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
        application.clearKochPanel();
        tsCalc.init();
        threadCountDown = new CountDownLatch(3);

        // TODO: Implement observer pattern so that KochFractal follows SOLID
        leftCalculator = new KochFractal(this);
        bottomCalculator = new KochFractal(this);
        rightCalculator = new KochFractal(this);

        leftCalculator.setLevel(nxt);
        bottomCalculator.setLevel(nxt);
        rightCalculator.setLevel(nxt);

        tsCalc.setBegin("Begin calculating");

        executorService.execute(new Thread(new Runnable() {
            @Override
            public void run() {
                leftCalculator.generateLeftEdge();
                countDown();
            }
        }));
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                bottomCalculator.generateBottomEdge();
                countDown();
            }
        });
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                rightCalculator.generateRightEdge();
                countDown();
            }
        });
    }

    private void countDown() {
        threadCountDown.countDown();
        if (threadCountDown.getCount() == 0) {
            try {
                tsCalc.setEnd("End calculating");
                edges.addAll(leftCalculator.getEdges());
                edges.addAll(bottomCalculator.getEdges());
                edges.addAll(rightCalculator.getEdges());
            } catch (Exception e) {
                e.printStackTrace();
            }

            application.requestDrawEdges();
            Platform.runLater(() -> {
                application.setTextNrEdges(String.valueOf(leftCalculator.getNrOfEdges()));
                application.setTextCalc(tsCalc.toString());
            });
        }
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

}
