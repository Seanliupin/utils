package com.dotgoing.utils.core

import org.junit.Test
import reactor.core.publisher.Mono

class MonoOpTest : BastTest() {

    @Test
    fun noneValueOperatorTest() {
        val none: Mono<Option<String>> = Mono.just(None())
        val mappedStr = "mapped str"

        none.someMap {
            Some(mappedStr)
        }.block()!!.shouldEqual(None())

        none.someMap {
            throw Exception("bug")
            Some(mappedStr)
        }.block()!!.shouldEqual(None())

        none.someMap {
            Some(mappedStr)
        }.block()!!.shouldNotEqual(None(Exception("something wrong")))

        none.orValue {
            "real-value"
        }.block()!!.shouldEqual(Some("real-value"))

        none.orValue {
            throw Exception("bug")
            "real-value"
        }.block()!!.shouldEqual(None(Exception("bug")))

        none.orMono {
            MonoObj.monoOp("real-value")
        }.block()!!.shouldEqual(Some("real-value"))

        none.orMono {
            throw Exception("bug")
        }.block()!!.shouldEqual(None(Exception("bug")))

        none.orOption {
            Some("sdf")
        }.block()!!.shouldEqual(Some("sdf"))

        none.orOption {
            throw Exception("bug")
        }.block()!!.shouldEqual(None(Exception("bug")))

        none.orOption {
            None(Exception("other bug"))
        }.block()!!.shouldEqual(None(Exception("other bug")))
    }
}