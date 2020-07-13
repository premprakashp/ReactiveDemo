package org.pp.reactivedemo;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observables.ConnectableObservable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ReactiveDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveDemo.class);
    private static Integer[] numbers = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

    private static String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h", "i"};
    private static String[] titles = {"title"};
    private static List<String> titleList = Arrays.asList(titles);

    private static Integer subscriber1 = 0;
    private static Integer subscriber2 = 0;

    static Observable<String> getTitle() {
        return Observable.fromIterable(titleList);
    }
    static Flux<String> getFluxTitle() {
        return Flux.fromIterable(titleList);
    }

    public static void main(String[] args) {
        Observable<String> observable = Observable.just("Hello");
        //observable.subscribe(s -> LOGGER.info("s =>{}",s));
        observable.subscribe(s ->
                LOGGER.info("RxJava s =>{}",s), //onNext
                Throwable::printStackTrace, //onError
                () -> LOGGER.info("RxJava s completed")  //onCompleted
        );

        LOGGER.info("-------Reactor Hello Impl-----------");
        Mono.just("Hello")
                .doFinally(onFinally -> LOGGER.info("Reactor s completed"))
                .subscribe(s -> LOGGER.info("Reactor s =>{}",s));

        LOGGER.info("-------RxJava Single Impl-----------");
        Single<String> single = Observable.just("Hello")
                .single("DEFAULT")
                .doOnSuccess(s -> LOGGER.info("RxJava Single Impl =>{}",s))
                .doOnError(e -> {
                    throw new RuntimeException(e.getMessage());
                });
        single.subscribe();

        LOGGER.info("-------Reactor Single Impl-----------");
        Mono.just("Hello")
                .single()
                .doOnSuccess(success-> LOGGER.info("Reactor Single Impl =>{}",success))
                .doOnError(e -> {
                    throw new RuntimeException(e.getMessage());
                }).subscribe();

        LOGGER.info("-------RxJava Map-----------");
        Observable.fromArray(letters)
                .map(String::toUpperCase)
                .subscribe(s -> LOGGER.info("RxJava Map =>{}",s));

        LOGGER.info("-------Reactor Map-----------");
        Flux.fromArray(letters)
                .map(String::toUpperCase)
                .subscribe(s -> LOGGER.info("Reactor Map =>{}",s));

        LOGGER.info("-------RxJava FlatMap-----------");
        Observable.just("book1", "book2")
                .flatMap(s -> getTitle())
                .subscribe(s -> LOGGER.info("RxJava FlatMap =>{}",s));

        LOGGER.info("-------Reactor FlatMap-----------");
        Flux.just("book1", "book2")
                .flatMap(s -> getFluxTitle())
                .subscribe(s -> LOGGER.info("Reactor FlatMap =>{}",s));


        LOGGER.info("--------RxJava Scan----------");
        Observable.fromArray(letters)
                .scan(new StringBuilder(), StringBuilder::append)
                .subscribe(s -> LOGGER.info("RxJava Scan =>{}",s));


        LOGGER.info("--------Reactor Scan----------");
        Flux.fromArray(letters)
                .scan(new StringBuilder(), StringBuilder::append)
                .subscribe(s -> LOGGER.info("Reactor Scan =>{}",s));

        LOGGER.info("------RxJava GroupBy------------");
        Observable.fromArray(numbers)
                .groupBy(i -> 0 == (i % 2) ? "EVEN" : "ODD")
                .subscribe((group) -> group.subscribe((number) -> {
                    LOGGER.info("RxJava GroupBy group.getKey()=>{}, number=>{}",group.getKey(),number);
                }));

        LOGGER.info("------Reactor GroupBy------------");
        Flux.fromArray(numbers)
                .groupBy(i -> 0 == (i % 2) ? "EVEN" : "ODD")
                .subscribe((group) -> group.subscribe((number) -> {
                    LOGGER.info("Reactor GroupBy group.getKey()=>{}, number=>{}",group.key(),number);
                }));

        LOGGER.info("-------RxJava Filter-----------");
        Observable.fromArray(numbers)
                .filter(i -> (i % 2 == 1))
                .subscribe(s -> LOGGER.info("RxJava Filter =>{}",s));

        LOGGER.info("-------Reactor Filter-----------");
        Flux.fromArray(numbers)
                .filter(i -> (i % 2 == 1))
                .subscribe(s -> LOGGER.info("Reactor Filter =>{}",s));

        LOGGER.info("------RxJava DefaultIfEmpty------------");
        Observable.empty()
                .defaultIfEmpty("RxJava Observable is empty")
                .subscribe(s -> LOGGER.info("RxJava DefaultIfEmpty =>{}",s));

        LOGGER.info("------Reactor DefaultIfEmpty------------");
        Mono.empty()
                .defaultIfEmpty("Reactor Observable is empty")
                .subscribe(s -> LOGGER.info("Reactor DefaultIfEmpty =>{}",s));

        LOGGER.info("------RxJava DEFAULT------------");
        Observable.empty()
                .first("DEFAULT")
                .subscribe(s -> LOGGER.info("RxJava DEFAULT =>{}",s));

        LOGGER.info("------Reactor DEFAULT------------");
        Flux.empty()
                .take(1)
                .single("DEFAULT")
                .subscribe(s -> LOGGER.info("Reactor DEFAULT =>{}",s));

        LOGGER.info("------RxJava DefaultIfEmpty-2-----------");
        Observable.fromArray(letters)
                .defaultIfEmpty("Observable is empty")
                .first("DEFAULT")
                .subscribe(s -> LOGGER.info("RxJava DefaultIfEmpty2 =>{}",s));

        LOGGER.info("------Reactor DefaultIfEmpty-2-----------");
        Flux.fromArray(letters)
                .take(1)
                .defaultIfEmpty("Observable is empty")
                .single("DEFAULT")
                .subscribe(s -> LOGGER.info("Reactor DefaultIfEmpty2 =>{}",s));

        LOGGER.info("-------RxJava TakeWhile-----------");
        Observable.fromArray(numbers)
                .takeWhile(i -> i < 5)
                .subscribe(s -> LOGGER.info("RxJava TakeWhile =>{}",s));

        LOGGER.info("-------Reactor TakeWhile-----------");
        Flux.fromArray(numbers)
                .takeWhile(i -> i < 5)
                .subscribe(s -> LOGGER.info("Reactor TakeWhile =>{}",s));

        LOGGER.info("------RxJava Disposable-----------");
        try {

            Disposable d = Observable.just("Hello world!")
                    .delay(1, TimeUnit.SECONDS)
                    .subscribeWith(new DisposableObserver<String>() {
                        @Override
                        public void onStart() {
                            LOGGER.info("RxJava Start!");
                        }

                        @Override
                        public void onNext(String t) {
                            LOGGER.info(t);
                        }

                        @Override
                        public void onError(Throwable t) {
                            LOGGER.error("RxJava Disposable onError ", t);
                        }

                        @Override
                        public void onComplete() {
                            LOGGER.info("RxJava Done!");
                        }
                    });

            Thread.sleep(500);
            // the sequence can now be disposed via dispose()
            d.dispose();
        }catch (Exception e){
            LOGGER.error("RxJava Disposable Exception ", e);
        }

        LOGGER.info("------Reactor Disposable-----------");
        try {

            reactor.core.Disposable d = Mono.just("Hello world!")
                    .delayElement(Duration.ofSeconds(1))
                    .doOnNext(s->LOGGER.info(s))
                    .doOnError(t->LOGGER.error("Reactor Disposable onError ", t))
                    .doOnSuccess(f->LOGGER.info("Reactor Done!"))
                    .doOnSubscribe(os -> LOGGER.info("Reactor Start!"))
                    .subscribe();

            Thread.sleep(500);
            // the sequence can now be disposed via dispose()
            d.dispose();
        }catch (Exception e){
            LOGGER.error("Reactor Disposable Exception ", e);
        }

        try{
            LOGGER.info("------RxJava ConnectableObservable-----------");
            ConnectableObservable<Long> connectable
                    = Observable.interval(200, TimeUnit.MILLISECONDS).publish();
            connectable.subscribe(s->LOGGER.info("RxJava ConnectableObservable =>{}",s));

            LOGGER.info("RxJava Connect");
            connectable.connect();

            Thread.sleep(500);
            LOGGER.info("RxJava Sleep");
        }catch (Exception e){
            LOGGER.error("RxJava Disposable Exception ", e);
        }

        try{
            LOGGER.info("------Reactor ConnectableObservable-----------");
            ConnectableFlux<Long> connectableReactor
                    = Flux.interval(Duration.ofMillis(200)).publish();
            connectableReactor.subscribe(s->LOGGER.info("Reactor ConnectableObservable =>{}",s));

            LOGGER.info("Reactor Connect");
            connectableReactor.connect();

            Thread.sleep(500);
            LOGGER.info("Reactor Sleep");
        }catch (Exception e){
            LOGGER.error("Reactor Disposable Exception ", e);
        }

        LOGGER.info("------RxJava Resource mgmt-----------");
        Observable<Character> values = Observable.using(
                () -> {
                    String resource = "MyResource";
                    LOGGER.info("RxJava Resource mgmt Leased: " + resource);
                    return resource;
                },
                r -> Observable.create(o -> {
                    for (Character c : r.toCharArray()) {
                        o.onNext(c);
                    }
                    o.onComplete();
                }),
                r -> LOGGER.info("RxJava Resource mgmt Disposed: {}", r)
        );

        values.subscribe(
                s->LOGGER.info("RxJava Resource mgmt onNext: {}",s),
                s->LOGGER.info("RxJava Resource mgmt onError: {}",s)
        );

        LOGGER.info("------Reactor Resource mgmt-----------");
        Flux<Character> valuesReactor = Flux.using(
                () -> {
                    String resource = "MyResource";
                    LOGGER.info("Reactor Resource mgmt Leased: " + resource);
                    return resource;
                },
                r -> Flux.create(o -> {
                    for (Character c : r.toCharArray()) {
                        o.next(c);
                    }
                    o.complete();
                }),
                r -> LOGGER.info("Reactor Resource mgmt Disposed: {}", r)
        );

        valuesReactor.subscribe(
                s->LOGGER.info("Reactor Resource mgmt onNext: {}",s),
                s->LOGGER.info("Reactor Resource mgmt onError: {}",s)
        );
    }
}
