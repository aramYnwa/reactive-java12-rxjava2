package org.example.rxjava;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.example.rxjava.utility.GateBasedSynchronization;
import org.example.rxjava.utility.datasets.GreekAlphabet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventSequenceExample {

  private static Logger log = LoggerFactory.getLogger(EventSequenceExample.class);

  public static void main(String[] args) {

    // This sync tool is meant for keeping away from exiting
    // till all test code has been executed.
    GateBasedSynchronization gate = new GateBasedSynchronization();

    // Create an Observable<String> that contains greek alphabet
    // Create a simple observer and implement methods

    Observable.fromArray(GreekAlphabet.greekLetters)
      .subscribe(new Observer<>() {

        @Override
        public void onSubscribe(Disposable disposable) {
          log.info("onSubscribe");
        }

        @Override
        public void onNext(String nextLetter) {
          log.info("onNext - {}", nextLetter);
        }

        @Override
        public void onError(Throwable throwable) {
          log.error("onError - {}", throwable.getMessage());

          // To let main thread go on we gonna open gait for "onError"
          gate.openGate("onError");
        }

        // This is called when the Observable finishes emitting all events.
        // If onError is called this won't be called.
        // Likewise if this is called, onError won't happen.
        @Override
        public void onComplete() {
          log.info("onComplete");
          gate.openGate("onComplete");
        }
      });

    gate.waitForAny("onComplete", "onError");

    System.exit(0);

  }

}
