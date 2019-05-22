package com.dotgoing.utils.j.option;

import org.junit.Assert;
import org.junit.Test;

public class CatTest {

    @Test
    public void map_test() {

        Cat<String> cat = Cat.of("str");
        Cat<Integer> catLen = cat.someMap((s) -> new Some(s.length()));
        int len = catLen.getOrElse(5);

        Assert.assertEquals("should equal", 3, len);

        cat = Cat.empty();
        catLen = cat.someMap((s) -> new Some(s.length()));
        len = catLen.getOrElse(5);

        Assert.assertEquals("should equal", 5, len);

        String str = "hello, you are so good";
        int mapLen = Cat.of(str)
                .someMap((s) -> new Some<>(s.length()))
                .getOrElse(5);

        Assert.assertEquals("should equal", str.length(), mapLen);
    }
}
