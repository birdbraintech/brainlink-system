package edu.cmu.ri.createlab.brainlink.applications;

import edu.cmu.ri.createlab.brainlink.robots.BrainLinkSolo;

/**
 * Simple utility that tells you what serial port your Brainlink is on
 */
public class PortFinder {
       public static void main(final String[] args)
      {
        final BrainLinkSolo brainLinkSolo = new BrainLinkSolo();
        System.out.println("BrainLink is on serial port: " + brainLinkSolo.getBrainLink().getPortName());
      }
}
