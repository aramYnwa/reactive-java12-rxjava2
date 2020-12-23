package org.example.rxjava.utility.subscribers;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import org.example.rxjava.utility.GateBasedSynchronization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// SingleSubscriber is waiting only for one event.
// That's why we do not have onNext calls. Moreover onSubscribe combines onComplete as well.

public class SingleSubscriber<TEvent> implements SingleObserver<TEvent> {

  private static final Logger log = LoggerFactory.getLogger(SingleSubscriber.class);
  private GateBasedSynchronization gate;
  private String errorGetName;
  private String successGateName;

  public SingleSubscriber(GateBasedSynchronization gate, String errorGetName, String successGateName) {
    this.gate = gate;
    this.errorGetName = errorGetName;
    this.successGateName = successGateName;
  }

  @Override
  public void onSubscribe(Disposable disposable) {
    log.info("onSubscribe");
  }

  @Override
  public void onSuccess(TEvent tEvent) {
    log.info("onSuccess - {}", tEvent);
    gate.openGate(successGateName);
  }

  @Override
  public void onError(Throwable e) {
    log.error("onError - {}", e.getMessage());
    log.error(e.getMessage(), e);
    gate.openGate(errorGetName);
  }
}
