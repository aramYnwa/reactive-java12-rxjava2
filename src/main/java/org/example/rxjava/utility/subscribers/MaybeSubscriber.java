package org.example.rxjava.utility.subscribers;

import io.reactivex.MaybeObserver;
import io.reactivex.disposables.Disposable;
import org.example.rxjava.utility.GateBasedSynchronization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// This subscriber will get 0 or 1 event.
// OnSuccess will called if there is no item and onSuccess if there is the one
public class MaybeSubscriber<TEvent> implements MaybeObserver<TEvent> {
  private static final Logger log = LoggerFactory.getLogger(MaybeSubscriber.class);
  private final GateBasedSynchronization gate;
  private final String errorGateName;
  private final String successGateName;
  private final String completeGateName;

  public MaybeSubscriber() {
    this.gate = new GateBasedSynchronization();
    this.errorGateName = "OnError";
    this.successGateName = "OnSuccess";
    this.completeGateName = "OnComplete";
  }

  @Override
  public void onSubscribe(Disposable disposable) {
    log.info("On subscribe");
  }

  @Override
  public void onSuccess(TEvent tEvent) {
    log.info("onSuccess - {}", tEvent);
    gate.openGate(successGateName);
  }

  @Override
  public void onError(Throwable e) {
    log.error("onError - {}", e.getMessage());
    gate.openGate(errorGateName);
  }

  @Override
  public void onComplete() {
    log.info("onSuccess");
    gate.openGate(completeGateName);
  }
}
