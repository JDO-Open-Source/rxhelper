/**
 * Copyright (c) 2016-present, RxJava Contributors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

package com.jidouauto.lib.rxhelper.ext;

import java.util.NoSuchElementException;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.plugins.RxJavaPlugins;

public final class ObservableFirstSingle<T> extends Single<T> {

    final ObservableSource<? extends T> source;

    public ObservableFirstSingle(ObservableSource<? extends T> source) {
        this.source = source;
    }

    @Override
    public void subscribeActual(SingleObserver<? super T> t) {
        source.subscribe(new SingleElementObserver<T>(t));
    }

    static final class SingleElementObserver<T> implements Observer<T>, Disposable {
        final SingleObserver<? super T> downstream;

        Disposable upstream;

        T value;

        boolean done;

        SingleElementObserver(SingleObserver<? super T> actual) {
            this.downstream = actual;
        }

        @Override
        public void onSubscribe(Disposable d) {
            if (DisposableHelper.validate(this.upstream, d)) {
                this.upstream = d;
                downstream.onSubscribe(this);
            }
        }

        @Override
        public void dispose() {
            upstream.dispose();
        }

        @Override
        public boolean isDisposed() {
            return upstream.isDisposed();
        }

        @Override
        public void onNext(T t) {
            if (done) {
                return;
            }
            value = t;
            done = true;
            upstream.dispose();
            downstream.onSuccess(value);
        }

        @Override
        public void onError(Throwable t) {
            if (done) {
                return;
            }
            done = true;
            downstream.onError(t);
        }

        @Override
        public void onComplete() {
            if (done) {
                return;
            }
            done = true;
            downstream.onError(new NoSuchElementException());
        }
    }
}
