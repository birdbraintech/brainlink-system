package edu.cmu.ri.createlab.brainlink.robots;

import edu.cmu.ri.createlab.brainlink.BrainLinkInterface;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface BrainLinkControllable
   {
   BrainLinkInterface getBrainLink();

   void disconnect();
   }