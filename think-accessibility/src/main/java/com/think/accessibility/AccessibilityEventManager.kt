package com.think.accessibility

import android.view.accessibility.AccessibilityEvent
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer


object AccessibilityEventManager: ObservableOnSubscribe<AccessibilityEvent> {


    private var mObservable: Observable<AccessibilityEvent> = Observable.create(this)

    private lateinit var mEmitter: ObservableEmitter<AccessibilityEvent>

    fun listen(observer: Observer<AccessibilityEvent>) {
        mObservable.subscribe(observer)
    }

    fun dispatcher(event: AccessibilityEvent) {
        if (!mEmitter.isDisposed){
            mEmitter.onNext(event)
        }
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

    override fun subscribe(emitter: ObservableEmitter<AccessibilityEvent>) {
        mEmitter = emitter;
    }
}
