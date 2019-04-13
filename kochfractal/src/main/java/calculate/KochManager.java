/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
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
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public KochManager(FUN3KochFractalFX application) {
        this.edges = new ArrayList<Edge>();
        this.application = application;
        this.tsCalc = new TimeStamp();
        this.tsDraw = new TimeStamp();
        executorService = Executors.newFixedThreadPool(3);
    }
    
    public void changeLevel(int nxt){
        if(leftCalculator != null && bottomCalculator != null && rightCalculator != null){
            leftCalculator.cancelCalculation();
            bottomCalculator.cancelCalculation();
            rightCalculator.cancelCalculation();
        }

        edges.clear();
        application.clearKochPanel();
        tsCalc.init();
        threadCountDown = new CountDownLatch(3);

        leftCalculator = new KochFractal(propertyChangeSupport,this);
        bottomCalculator = new KochFractal(propertyChangeSupport, this);
        rightCalculator = new KochFractal(propertyChangeSupport, this);

        application.bindLeftProgressProperty(leftCalculator);
        application.bindLeftProgressProperty(bottomCalculator);
        application.bindLeftProgressProperty(rightCalculator);

        leftCalculator.addChangeListener("doneLeft", this::countDown);
        bottomCalculator.addChangeListener("doneBottom", this::countDown);
        rightCalculator.addChangeListener("doneRight", this::countDown);

        leftCalculator.setLevel(nxt);
        bottomCalculator.setLevel(nxt);
        rightCalculator.setLevel(nxt);

        tsCalc.setBegin("Begin calculating");

        executorService.execute(new Thread((Runnable) () -> leftCalculator.generateLeftEdge()));
        executorService.execute(new Thread((Runnable) () -> leftCalculator.generateBottomEdge()));
        executorService.execute(new Thread((Runnable) () -> leftCalculator.generateRightEdge()));
    }

    private void countDown(PropertyChangeEvent propertyChangeEvent) {
        threadCountDown.countDown();
        if (threadCountDown.getCount() == 0) {
            try {
                tsCalc.setEnd("End calculating");
                edges.addAll((ArrayList<Edge>)leftCalculator.call());
                edges.addAll((ArrayList<Edge>)bottomCalculator.call());
                edges.addAll((ArrayList<Edge>)rightCalculator.call());
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
