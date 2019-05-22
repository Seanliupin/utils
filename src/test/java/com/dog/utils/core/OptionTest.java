package com.dog.utils.core;

import com.dog.utils.option.Option;
import org.junit.Assert;
import org.junit.Test;

public class OptionTest {

    @Test
    public void map_test() {
        String str = "hallo";
        Option<String> some = Option.of(str);
        Option<Integer> afterMap = some.map(String::length);

        Assert.assertEquals("", (int) afterMap.value(), str.length());

        Option<String> none = Option.empty();
        afterMap = none.map(String::length);

        Assert.assertFalse(afterMap.hasValue());
    }


    @Test
    public void flat_map_test() {
        final String str = "hallo";
        final Option<String> some = Option.of(str);
        Option<Integer> afterMap = some.flatMap((r) -> Option.of(r.length()));
        Assert.assertEquals("", (int) afterMap.value(), str.length());

        Option<Integer> noMap = some.flatMap((r) -> Option.empty());
        Assert.assertFalse(noMap.hasValue());

        final Option<String> none = Option.empty();
        afterMap = none.flatMap((r) -> Option.of(r.length()));
        Assert.assertFalse(afterMap.hasValue());
    }
}

