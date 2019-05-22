package com.dotgoing.utils.core;

import com.dotgoing.utils.cat.Cat;
import com.dotgoing.utils.option.None;
import com.dotgoing.utils.option.Option;
import org.junit.Assert;
import org.junit.Test;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;

public class CatTest {

    @Test
    public void flat_map_test() {
        Cat<String> cat = Cat.of("str");
        Cat<Integer> catLen = cat.someFlatMap((s) -> Mono.just(Option.of(s.length())));
        int len = catLen.getOrElse(5);
        Assert.assertEquals("should equal", 3, len);

        cat = Cat.empty();
        catLen = cat.someFlatMap((s) -> Mono.just(Option.of(s.length())));
        len = catLen.getOrElse(5);
        Assert.assertEquals("should equal", 5, len);

        String str = "hello, you are so good";
        int mapLen = Cat.of(str)
                .someFlatMap((s) -> Mono.just(Option.of(s.length())))
                .getOrElse(5);
        Assert.assertEquals("should equal", str.length(), mapLen);

        cat = Cat.of("str");
        catLen = cat.someFlatMap((s) -> {
            fakeError();
            return Mono.just(Option.of(s.length()));
        });
        len = catLen.getOrElse(5);
        Assert.assertEquals("should equal", 5, len);
        None<Integer> errorNone = (None<Integer>) catLen.value();
        Assert.assertEquals("should equal", "fake error", errorNone.error().getMessage());
    }

    @Test
    public void map_test() {
        String testStr = "str";
        String defaultStr = "default-str";
        String mapStr = "map-str";
        int defaultInt = 20;
        int mapInt = 10;
        String strValue;
        Cat<String> someStr = Cat.of(testStr);
        Cat<String> noStr = Cat.empty();

        strValue = someStr.getOrElse(defaultStr);
        Assert.assertEquals(testStr, strValue);
        strValue = noStr.getOrElse(defaultStr);
        Assert.assertEquals(defaultStr, strValue);

        strValue = someStr.actSome((t) -> fakeError()).getOrElse(defaultStr);
        Assert.assertEquals(testStr, strValue);
        strValue = noStr.actSome((t) -> fakeError()).getOrElse(defaultStr);
        Assert.assertEquals(defaultStr, strValue);

        int intValue;
        intValue = someStr.someMap((t) -> Option.of(mapInt)).getOrElse(defaultInt);
        Assert.assertEquals(mapInt, intValue);
        intValue = noStr.someMap((t) -> Option.of(mapInt)).getOrElse(defaultInt);
        Assert.assertEquals(defaultInt, intValue);

        strValue = someStr.noneMap((t) -> Option.of(mapStr)).getOrElse(defaultStr);
        Assert.assertEquals(testStr, strValue);
        strValue = noStr.noneMap((t) -> Option.of(mapStr)).getOrElse(defaultStr);
        Assert.assertEquals(mapStr, strValue);
    }

    @Test
    public void flatt_map_test() {
        String testStr = "test-str";
        String defaultStr = "default-str";
        String mapStr = "map-str";
        int defaultInt = 20;
        int mapInt = 10;
        String strValue;
        Cat<String> someStr = Cat.of(testStr);
        Cat<String> noStr = Cat.empty();

        int intValue;

        intValue = someStr.someFlatMap((t) -> Mono.just(Option.of(mapInt))).getOrElse(defaultInt);
        Assert.assertEquals(mapInt, intValue);
        intValue = noStr.someFlatMap((t) -> Mono.just(Option.of(mapInt))).getOrElse(defaultInt);
        Assert.assertEquals(defaultInt, intValue);

        strValue = someStr.noneFlatMap((t) -> Mono.just(Option.of(mapStr))).getOrElse(defaultStr);
        Assert.assertEquals(testStr, strValue);
        strValue = noStr.noneFlatMap((t) -> Mono.just(Option.of(mapStr))).getOrElse(defaultStr);
        Assert.assertEquals(mapStr, strValue);
    }

    @Test
    public void exception() {
        String testStr = "str";
        AtomicInteger atomicInteger = new AtomicInteger(0);
        Cat<String> someValue = Cat.of(testStr);
        Cat<String> noValue = Cat.empty();

        Option<String> re = someValue.actSome((t) -> {
            atomicInteger.set(t.length());
            fakeError();
        }).someMap((l) -> {
            Assert.assertEquals(l, testStr);
            return Option.of(l);
        }).noneMap((e) -> {
            fakeError();
            return Option.of("");
        }).block();


        Assert.assertEquals(atomicInteger.get(), testStr.length());
        Assert.assertEquals(re.value(), testStr);

    }

    private void fakeError() {
        throw new RuntimeException("fake error");
    }

}
