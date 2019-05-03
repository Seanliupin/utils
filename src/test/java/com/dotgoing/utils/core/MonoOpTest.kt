package com.dotgoing.utils.core

import com.dotgoing.utils.core.`fun`.*
import com.dotgoing.utils.core.option.None
import com.dotgoing.utils.core.option.Option
import com.dotgoing.utils.core.option.Some
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
            monoOp("real-value")
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

    @Test
    @Throws(Exception::class)
    fun mapOperatorTest() {
        val none: Mono<Option<String>> = Mono.just(None())
        val noneInt: Mono<Option<Int>> = Mono.just(None())
        val some: Mono<Option<String>> = Mono.just(Some("hello"))
        val someInt: Mono<Option<Int>> = Mono.just(Some(9))

        none.noneMap {
            None(java.lang.Exception("first-none"))
        }.block()!!.shouldEqual(None(Exception("first-none")))

        none.noneMap {
            throw Exception("bug")
        }.block()!!.shouldEqual(None(Exception("bug")))


        none.noneMap {
            None(Exception("first-none"))
        }.noneMap {
            None(Exception("second-none"))
        }.someMap {
            Some(5)
        }.block()!!.shouldEqual(None(Exception("second-none")))

        some.noneMap {
            None(Exception("first-none"))
        }.someMap {
            Some(8)
        }.block()!!.shouldEqual(Some(8))

        //这里的限制是，none只能noneMap出同态的Some，而不能是其他类型的Some
        none.noneMap {
            Some("first-none")
        }.someMap {
            Some("$it->$it")
        }.block()!!.shouldEqual(Some("first-none->first-none"))

        none.noneMap {
            Some("first-none")
        }.someMap {
            throw Exception("bug")
            Some("$it->$it")
        }.block()!!.shouldEqual(None(Exception("bug")))

        none.noneMap {
            Some("first-none")
        }.someFlatMap {
            someInt
        }.block()!!.shouldEqual(Some(9))

        none.noneMap {
            Some("first-none")
        }.someFlatMap {
            throw Exception("bug")
            someInt
        }.block()!!.shouldEqual(None(Exception("bug")))


    }
}