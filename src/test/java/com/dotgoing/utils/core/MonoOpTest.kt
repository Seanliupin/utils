package com.dotgoing.utils.core

import com.dotgoing.utils.core.extention.mono.*
import com.dotgoing.utils.core.extention.monoOp
import com.dotgoing.utils.core.extention.op
import com.dotgoing.utils.core.option.None
import com.dotgoing.utils.core.option.Option
import com.dotgoing.utils.core.option.Some
import org.junit.Test
import reactor.core.publisher.Mono
import kotlin.test.assertEquals

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

    @Test
    @Throws(Exception::class)
    fun needOperatorTest() {
        val none: Mono<Option<String>> = Mono.just(None())
        val noneInt: Mono<Option<Int>> = Mono.just(None())
        val some: Mono<Option<String>> = Mono.just(Some("hello"))
        val someInt: Mono<Option<Int>> = Mono.just(Some(9))


        someInt.valueFilter("should bigger than 3") {
            it > 3
        }.block()!!.shouldEqual(Some(9))

        someInt.valueFilterNot("should bigger than 3") {
            it > 3
        }.block()!!.shouldEqual(None(Exception("should bigger than 3")))

        someInt.valueFilterNot("should bigger than 3") {
            it > 13
        }.block()!!.shouldEqual(Some(9))



        someInt.valueFilter("") {
            it > 3
        }.someMap {
            Some(it.toString())
        }.noneMap {
            None(Exception("nothing"))
        }.block()!!.shouldEqual(Some("9"))

        someInt.valueFilter("") {
            it > 10
        }.someMap {
            Some(it.toString())
        }.noneMap {
            None(Exception("nothing"))
        }.block()!!.shouldEqual(None(Exception("nothing")))

        someInt.valueFilter("") {
            it > 10
        }.noneMap {
            None(Exception("redirect:login"))
        }.someMap {
            Some(it.toString())
        }.block()!!.shouldEqual(None(Exception("redirect:login")))

        someInt.valueFilter("") {
            it > 3
        }.noneMap {
            None(Exception("redirect:login"))
        }.someMap {
            Some(it.toString())
        }.block()!!.shouldEqual(Some("9"))
    }

    @Test
    @Throws(Exception::class)
    fun someValueOperatorTest() {
        val some: Mono<Option<String>> = Mono.just(Some("hello"))

        some.someMap { it ->
            op("$it-world", "")
        }.block()!!.shouldEqual(Some("hello-world"))

        some.someFlatMap { it ->
            monoOp("$it-world")
        }.block()!!.shouldEqual(Some("hello-world"))

        some.orValue {
            "its me"
        }.block()!!.shouldEqual(Some("hello"))

        //这里测试的是延迟计算
        some.orValue {
            bug("this should never be happened !!!")
            "it is dangerous"
        }.block()!!.shouldEqual(Some("hello"))

        some.mapValue {
            "$it world"
        }.block()!!.shouldEqual(Some("hello world"))

        some.valueFilter("leaved msg") {
            it.contains("you")
        }.block()!!.shouldEqual(None(Exception("leaved msg")))

        some.valueFilter("leaved msg") {
            it.contains("ll")
        }.block()!!.shouldEqual(Some("hello"))
    }

    @Test
    fun extract_some_test() {
        val some: Mono<Option<String>> = Mono.just(Some("hello"))
        val str = some.extract().block()
        assertEquals("hello", str, "should extract obj")
    }

    @Test(expected = TestException::class)
    fun extract_none_test() {
        val some: Mono<Option<String>> = Mono.just(Some("hello"))
        val none = some.someMap {
            None<String>(TestException())
        }

        none.extract().block()
    }

    @Test(expected = TestException::class)
    fun extract_none_exception_test() {
        val none: Mono<Option<String>> = Mono.just(None())
        none.extract(TestException()).block()
    }
}