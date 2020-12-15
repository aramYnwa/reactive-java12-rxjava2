package org.example.rxjava.utility;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GateBasedSynchronization {
  private final Logger LOG = LoggerFactory.getLogger(GateBasedSynchronization.class);

  private HashSet<String> openGateNames;

  public GateBasedSynchronization() {
    this.openGateNames = new HashSet<>();
  }

  // Block till a specific gate has been opened.
  public synchronized void waitForGate(String gateName) {
    try {
      while (!openGateNames.contains(gateName)) {
        wait();
      }
    } catch (InterruptedException e) {
      LOG.warn(String.format("InterruptException while waiting for gate %s", gateName), e);
    }
  }


  // Block till any of specified gates has been opened.
  public synchronized void waitForAny(String ... gateNames) {

    Set<String> searchGateNames = new HashSet<>(Arrays.asList(gateNames));

    try {
      while (searchGateNames.stream().noneMatch(gate -> openGateNames.contains(gate))) {
        wait();
      }
    } catch (InterruptedException e) {
      String gateNameList = generateGateNameList(searchGateNames);
      LOG.warn(String.format("InterruptException while waiting for gates %s", gateNameList), e);
    }
  }

  public synchronized void waitForAny(long duration, TimeUnit timeUnit, String ... gateNames) {

    Set<String> searchGateNames = new HashSet<>(Arrays.asList(gateNames));

    long millisecondsToWait = timeUnit.toMillis(duration);
    long exitTime = System.currentTimeMillis() + millisecondsToWait;

    try {
      while (searchGateNames.stream().noneMatch(gate -> openGateNames.contains(gate))) {

        if (System.currentTimeMillis() > exitTime) {
          break;
        }

        wait();
      }
    } catch (InterruptedException e) {
      String gateNameList = generateGateNameList(searchGateNames);
      LOG.warn(String.format( "InterruptedException while waiting for gate(s) '%s'", gateNameList), e);
    }
  }

  //Block untill all of the specified gates are opened
  public synchronized void waitForAll(String ... gateNames) {

    Set<String> searchGateNames = new HashSet<>(Arrays.asList(gateNames));

    try {
      while(!searchGateNames.stream().allMatch(gate -> openGateNames.contains(gate))) {
        wait();
      }
    } catch (InterruptedException e) {
      String gateNameList = generateGateNameList(searchGateNames);
      LOG.warn(String.format( "InterruptedException while waiting for all gates '%s'", gateNameList), e);
    }
  }

  public synchronized void openGate(String gateName) {
    openGateNames.add(gateName);
    notifyAll();
  }

  public synchronized void closeGate(String gateName) {
    openGateNames.remove(gateName);
  }

  public synchronized boolean isGateOpen(String gateName) {
    return openGateNames.contains(gateName);
  }

  public synchronized void resetAll() {
    openGateNames.clear();
  }

  public static void waitMultiple(String[] gateNames, GateBasedSynchronization ... gates) {

    for(GateBasedSynchronization nextGate : gates ) {
      nextGate.waitForAny(gateNames);
    }

  }

  private String generateGateNameList(Set<String> searchGateNames) {
    StringBuilder stringBuilder = new StringBuilder();
    searchGateNames.forEach(nextGateName -> {
      if (stringBuilder.length() > 0) {
        stringBuilder.append(", ");
      }
      stringBuilder.append(nextGateName);
    });
    return stringBuilder.toString();
  }

}
