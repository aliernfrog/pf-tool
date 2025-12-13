package io.github.aliernfrog.pftool_shared.di

import org.koin.mp.KoinPlatformTools

inline fun <reified T : Any> getKoinInstance(): T = KoinPlatformTools.defaultContext().get().get<T>()