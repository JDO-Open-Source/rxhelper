/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jidouauto.lib.rxhelper.transformer;

import com.jidouauto.lib.rxhelper.LifecycleSource;

import org.reactivestreams.Publisher;

import java.util.concurrent.CancellationException;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.CompletableTransformer;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.MaybeTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * @author eddie
 * <p>
 * 提供将业务流程和lifecycle绑定的实现
 * @see {@link LifecycleSource}
 */
public final class LifecycleTransformer<T> implements ObservableTransformer<T, T>,
        FlowableTransformer<T, T>,
        SingleTransformer<T, T>,
        MaybeTransformer<T, T>,
        CompletableTransformer {
    final Observable<?> observable;


    public static <T, R> LifecycleTransformer<T> bindUntilEvent(final Observable<R> lifecycle,
                                                             final R... events) {
        checkNotNull(lifecycle, "lifecycle == null");
        checkNotNull(events, "event == null");
        return bind(takeUntilEvent(lifecycle, events));
    }

    private static <T> T checkNotNull(T reference, Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        } else {
            return reference;
        }
    }

    public static <T, R> LifecycleTransformer<T> bind(final Observable<R> lifecycle) {
        return new LifecycleTransformer<>(lifecycle);
    }

    private static <R> Observable<R> takeUntilEvent(final Observable<R> lifecycle, final R... events) {
        return lifecycle.filter(new Predicate<R>() {
            @Override
            public boolean test(R lifecycleEvent) throws Exception {
                for (R event : events) {
                    if (lifecycleEvent.equals(event)) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public LifecycleTransformer(Observable<?> observable) {
        if (null == observable) {
            throw new NullPointerException("observable is null");
        }
        this.observable = observable;
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream.takeUntil(observable);
    }

    @Override
    public Publisher<T> apply(Flowable<T> upstream) {
        return upstream.takeUntil(observable.toFlowable(BackpressureStrategy.LATEST));
    }

    @Override
    public SingleSource<T> apply(Single<T> upstream) {
        return upstream.takeUntil(observable.firstOrError());
    }

    @Override
    public MaybeSource<T> apply(Maybe<T> upstream) {
        return upstream.takeUntil(observable.firstElement());
    }

    @Override
    public CompletableSource apply(Completable upstream) {
        return Completable.ambArray(upstream, observable.flatMapCompletable(CANCEL_COMPLETABLE));
    }

    static final Function<Object, Completable> CANCEL_COMPLETABLE = new Function<Object, Completable>() {
        @Override
        public Completable apply(Object ignore) throws Exception {
            return Completable.error(new CancellationException());
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LifecycleTransformer<?> that = (LifecycleTransformer<?>) o;

        return observable.equals(that.observable);
    }

    @Override
    public int hashCode() {
        return observable.hashCode();
    }

    @Override
    public String toString() {
        return "LifecycleTransformer{" +
                "observable=" + observable +
                '}';
    }
}
