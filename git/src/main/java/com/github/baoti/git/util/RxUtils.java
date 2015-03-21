package com.github.baoti.git.util;

import rx.Observable;
import rx.functions.Func1;
import rx.internal.util.UtilityFunctions;

public class RxUtils {

    public static <A, B> Observable<B> after(Observable<A> pre) {
        return pre.ignoreElements().map(RxUtils.<A, B>toNull());
    }

    public static <A, B> Observable<B> afterDo(Observable<A> pre, Observable<B> work) {
        return RxUtils.<A, B>after(pre).concatWith(work);
    }

    private static <A, B> Func1<A, B> toNull() {
        return UtilityFunctions.returnNull();
    }
}
