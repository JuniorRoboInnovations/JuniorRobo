package com.jrrobo.juniorroboapp.utility

import kotlinx.coroutines.CoroutineDispatcher

// Dispatcher provider interface for testing the coroutines
interface DispatcherProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}