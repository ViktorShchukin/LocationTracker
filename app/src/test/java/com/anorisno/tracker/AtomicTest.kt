package com.anorisno.tracker

import org.junit.Test
import java.util.concurrent.atomic.AtomicLong

class AtomicTest {

    @Test
    fun atomicLongOverflow() {
        val atomic: AtomicLong = AtomicLong(Long.MAX_VALUE)
        val toLong: Long
        toLong = atomic.addAndGet(1)
        println(toLong)
        println(Long.MIN_VALUE)
    }
}