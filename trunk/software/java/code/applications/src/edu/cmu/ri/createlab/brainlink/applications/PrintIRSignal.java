package edu.cmu.ri.createlab.brainlink.applications;

import edu.cmu.ri.createlab.brainlink.robots.BrainLinkSolo;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: tlauwers
 * Date: May 22, 2011
 * Time: 11:00:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class PrintIRSignal {
       public static void main(final String[] args)
      {
        final BrainLinkSolo brainLinkSolo = new BrainLinkSolo("COM91");
        brainLinkSolo.getBrainLink().setFullColorLED(255,0,0);
        int[] values = brainLinkSolo.getBrainLink().recordIR();
        XYSeries series = new XYSeries("Average Size");
        series.add(-1000,0);
        series.add(-1,0);
        series.add(0,1);
        int line_value = 1;
        int signalTime=0;
        for(int i = 0; i < values.length; i++) {
            signalTime+= values[i];
            System.out.print(values[i] + "us ");
            series.add(signalTime,line_value);
            line_value = line_value^1;
            series.add(signalTime+1, line_value);
        }
        XYDataset xyDataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYAreaChart
                     ("IR Signal",  // Title
                      "Time (us)",           // X-Axis label
                      "Signal",           // Y-Axis label
                      xyDataset,          // Dataset
                      PlotOrientation.VERTICAL, false, false, false);
        ChartPanel chartPanel = new ChartPanel( chart );
        JPanel panel = new JPanel();
        panel.add( chartPanel );
        JFrame frame = new JFrame();
        frame.getContentPane().add( panel );
        frame.setSize(800, 600);
        frame.show();
        sleep(10000);
        brainLinkSolo.getBrainLink().disconnect();
      }
    private static void sleep(final int millis)
      {
      try
         {
         Thread.sleep(millis);
         }
      catch (InterruptedException e)
         {
         System.out.println("Error while sleeping: " + e);
         }
      }
}
