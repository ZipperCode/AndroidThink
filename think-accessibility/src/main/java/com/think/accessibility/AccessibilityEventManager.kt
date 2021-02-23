package com.think.accessibility

import android.view.accessibility.AccessibilityEvent
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Observer


object AccessibilityEventManager {

    private var mObservable: Observable<AccessibilityEvent> = Observable.create { mEmitter = it }

    private lateinit var mEmitter: ObservableEmitter<AccessibilityEvent>

    fun listen(observer: Observer<AccessibilityEvent>) {
        mObservable.subscribe(observer)
    }

    fun onNext(event: AccessibilityEvent) {
        if (!mEmitter.isDisposed)
            mEmitter.onNext(event)
    }

    fun onError(error: Throwable) {
        if (!mEmitter.isDisposed)
            mEmitter.onError(error)
    }

    fun cancel() {
        if (!mEmitter.isDisposed) {
            mEmitter.onComplete()
        }
    }
}
