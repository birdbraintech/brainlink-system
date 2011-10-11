package edu.cmu.ri.createlab.brainlink.applications;

import edu.cmu.ri.createlab.brainlink.BrainLink;
import edu.cmu.ri.createlab.brainlink.BrainLinkInterface;

/**
 * Simple utility that tells you what serial port your Brainlink is on
 */
@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
public final class PortFinder
   {
   public static void main(final String[] args)
      {
      final BrainLinkInterface brainLink = new BrainLink();
      System.out.println("BrainLink is on serial port: " + brainLink.getPortName());
      }

   private PortFinder()
      {
      // private to prevent instantiation
      }
   }
