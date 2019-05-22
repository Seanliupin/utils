package com.dotgoing.utils.core;

import com.dotgoing.utils.cat.Cat;
import com.dotgoing.utils.option.Some;
import org.junit.Assert;
import org.junit.Test;
import reactor.core.publisher.Mono;

public class CatTest {

    @Test
    public void map_test() {

        Cat<String> cat = Cat.of("str");
        Cat<Integer> catLen = cat.someMap((s) -> new Some<>(s.length()));
        int len = catLen.getOrElse(5);
        Assert.assertEquals("should equal", 3, len);

        cat = Cat.empty();
        catLen = cat.someMap((s) -> new Some<>(s.length()));
        len = catLen.getOrElse(5);
        Assert.assertEquals("should equal", 5, len);

        String str = "hello, you are so good";
        int mapLen = Cat.of(str)
                .someMap((s) -> new Some<>(s.length()))
                .getOrElse(5);
        Assert.assertEquals("should equal", str.length(), mapLen);

        cat = Cat.of("str");
        catLen = cat.someMap((s) -> {
            fakeError();
            return new Some<>(s.length());
        });

        Assert.assertEquals("should equal", 3, len);
    }

    private void fakeError() {
        throw new RuntimeException("");
    }

    @Test
    public void flat_map_test() {
        Cat<String> cat = Cat.of("str");
        Cat<Integer> catLen = cat.someFlatMap((s) -> Mono.just(new Some<>(s.length())));
        int len = catLen.getOrElse(5);
        Assert.assertEquals("should equal", 3, len);

        cat = Cat.empty();
        catLen = cat.someFlatMap((s) -> Mono.just(new Some<>(s.length())));
        len = catLen.getOrElse(5);
        Assert.assertEquals("should equal", 5, len);

        String str = "hello, you are so good";
        int mapLen = Cat.of(str)
                .someFlatMap((s) -> Mono.just(new Some<>(s.length())))
                .getOrElse(5);
        Assert.assertEquals("should equal", str.length(), mapLen);
    }
}
