package edu.cmu.ri.createlab.brainlink.robots;

import java.util.SortedSet;
import edu.cmu.ri.createlab.brainlink.BrainLinkProxy;
import edu.cmu.ri.createlab.device.CreateLabDeviceProxy;
import edu.cmu.ri.createlab.device.connectivity.BaseCreateLabDeviceConnectivityManager;
import edu.cmu.ri.createlab.serial.SerialPortEnumerator;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class BrainLinkConnectivityManager extends BaseCreateLabDeviceConnectivityManager
   {
   private static final Logger LOG = Logger.getLogger(BrainLinkConnectivityManager.class);

   protected CreateLabDeviceProxy scanForDeviceAndCreateProxy()
      {
      LOG.debug("BrainLinkConnectivityManager.scanForDeviceAndCreateProxy()");

      // check each available serial port for the target serial device, and connect to the first one found
      final SortedSet<String> availableSerialPorts = SerialPortEnumerator.getAvailableSerialPorts();
      if ((availableSerialPorts != null) && (!availableSerialPorts.isEmpty()))
         {
         for (final String portName : availableSerialPorts)
            {
            if (LOG.isDebugEnabled())
               {
               LOG.debug("BrainLinkConnectivityManager.scanForDeviceAndCreateProxy(): checking serial port [" + portName + "]");
               }

            final CreateLabDeviceProxy proxy = BrainLinkProxy.create(portName);

            if (proxy == null)
               {
               LOG.debug("BrainLinkConnectivityManager.scanForDeviceAndCreateProxy(): connection failed, returning null.");
               }
            else
               {
               LOG.debug("BrainLinkConnectivityManager.scanForDeviceAndCreateProxy(): connection established, returning proxy!");
               return proxy;
               }
            }
         }
      else
         {
         LOG.debug("BrainLinkConnectivityManager.scanForDeviceAndCreateProxy(): No available serial ports, returning null.");
         }

      return null;
      }
   }
