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
    public void map_test() {
        Cat<String> cat = Cat.of("str");

        Cat<Integer> catLen = cat.someMap((s) -> Option.of(s.length()));
        int len = catLen.getOrElse(5);
        Assert.assertEquals("should equal", 3, len);

        cat = Cat.empty();
        catLen = cat.someMap((s) -> Option.of(s.length()));
        len = catLen.getOrElse(5);
        Assert.assertEquals("should equal", 5, len);

        String str = "hello, you are so good";
        int mapLen = Cat.of(str)
                .someMap((s) -> Option.of(s.length()))
                .getOrElse(5);
        Assert.assertEquals("should equal", str.length(), mapLen);

        cat = Cat.of("str");
        catLen = cat.someMap((s) -> {
            fakeError();
            return Option.of(s.length());
        });
//        因为someMap的时候出错了，因此只能取默认值
        len = catLen.getOrElse(5);
        Assert.assertEquals("should equal", 5, len);
        None<Integer> errorNone = (None<Integer>) catLen.value();
        Assert.assertEquals("should equal", "fake error", errorNone.error().getMessage());
    }

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

    /**
     * 要确保act中的异常不影响外面的map流
     */
    @Test
    public void exception_test() {
        String testStr = "str";
        AtomicInteger atomicInteger = new AtomicInteger(0);
        Cat<String> cat = Cat.of(testStr);

        Option<String> re = cat.actSome((t) -> {
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
//        Assert.assertEquals(re.value(), testStr);
        System.out.println("re " + re);
    }

    private void fakeError() {
        throw new RuntimeException("fake error");
    }

}
