package com.aliernfrog.pftool.di

import org.koin.mp.KoinPlatformTools

inline fun <reified T : Any> get(): T = KoinPlatformTools.defaultContext().get().get<T>()