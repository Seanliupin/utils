package com.dotgoing.utils.core

import com.dotgoing.utils.core.option.*
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class OptionTest {

    @Test(expected = OptionStateException::class)
    fun some_should_have_no_error() {
        val op: Option<String> = Some("hello")
        op.error()
    }

    @Test(expected = OptionStateException::class)
    fun none_should_have_no_value() {
        val op: Option<String> = None()
        op.value()
    }

    @Test
    fun option_map_test() {
        val str = "hello"
        val someValue: Option<String> = Some(str)
        val mappedSome = someValue.map {
            "pre-$it"
        }
        assertEquals(mappedSome.value(), "pre-$str", "some map should success")

        val mappedSomeLen = someValue.map {
            it.length
        }
        assertEquals(mappedSomeLen.value(), str.length, "some map should success")

        val noneValue: Option<String> = None()

        val mappedNone = noneValue.map {
            "pre-$it"
        }
        assertTrue(mappedNone.hasNoValue(), "none has no value")

        val mappedNoneLen = noneValue.map {
            it.length
        }
        assertTrue(mappedNoneLen.hasNoValue(), "none has no value")
    }

    @Test
    fun option_map_error_test() {
        val str = "hello"
        val someValue: Option<String> = Some(str)
        val errorOption = someValue.map {
            throw Exception("oh no")
        }
        assertTrue(errorOption.hasNoValue(), "error in map function should result in None")
        assertEquals(errorOption.error().message, "oh no", "when error occurs, map should catch it")

        val errorAgain = errorOption.map {
            throw Exception("oh no again")
        }
        assertTrue(errorAgain.hasNoValue(), "error in map function should result in None")
        assertEquals(errorAgain.error().message, "oh no", "None should never map, thus it will never capture the map error")
    }

    @Test
    fun option_flatMap_test() {
        val str = "hello"
        val someValue: Option<String> = Some(str)

        val flatMappedSome = someValue.flatMap {
            Some("pre-$it")
        }
        assertEquals(flatMappedSome.value(), "pre-$str", "some map should success")

        val flatMappedSomeLen = someValue.flatMap {
            Some(it.length)
        }
        assertEquals(flatMappedSomeLen.value(), str.length, "some map should success")

        val flatMappedSomeToNone = someValue.flatMap {
            None<String>()
        }

        assertTrue(flatMappedSomeToNone.hasNoValue(), "some should flat map to none")

        val noneValue: Option<String> = None()

        val flatMappedNone = noneValue.flatMap {
            Some("pre-$it")
        }
        assertTrue(flatMappedNone.hasNoValue(), "None will never flatMap to Some")

        val flatMappedNoneLen = noneValue.map {
            it.length
        }
        assertTrue(flatMappedNoneLen.hasNoValue(), "None will never flatMap to Some")

        val flatMappedNoneToNone = noneValue.map {
            None<String>()
        }
        assertTrue(flatMappedNoneToNone.hasNoValue(), "None will never flatMap to Some")
    }


    @Test
    fun option_flatMap_error_test() {
        val str = "hello"
        val someValue: Option<String> = Some(str)

        val errorOption = someValue.flatMap {
            throw Exception("oh no")
            Some("pre-$it")
        }
        assertTrue(errorOption.hasNoValue(), "error in flatMap function should result in None")
        assertEquals(errorOption.error().message, "oh no", "when error occurs, flatMap should catch it")

        val errorAgain = errorOption.flatMap {
            throw Exception("oh no again")
            Some("again-$it")
        }
        assertTrue(errorAgain.hasNoValue(), "error in flatMap function should result in None")
        assertEquals(errorAgain.error().message, "oh no", "None should never flatMap, thus it will never capture the flatMap error")
    }

    @Test
    fun none_str_constructor() {
        val a = None<String>("hello")
        val b = None<String>(OptionException("hello"))

        assertEquals(b, a, "both should be equal")

        val c = None<String>("hello")
        val d = None<String>(Exception("hello"))

        assertNotEquals(c, d, "both should be equal")
    }
}