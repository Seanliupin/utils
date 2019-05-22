package com.dotgoing.utils.core;

import com.dotgoing.utils.option.None;
import com.dotgoing.utils.option.Option;
import com.dotgoing.utils.option.Some;
import org.junit.Assert;
import org.junit.Test;

public class OptionTest {

    @Test
    public void map_test() {
        String str = "hallo";
        Option<String> some = new Some<>(str);
        Option<Integer> afterMap = some.map(String::length);

        Assert.assertEquals("", (int) afterMap.value(), str.length());

        Option<String> none = new None<>();
        afterMap = none.map(String::length);

        Assert.assertFalse(afterMap.hasValue());
    }


    @Test
    public void flat_map_test() {
        final String str = "hallo";
        final Option<String> some = new Some<>(str);
        Option<Integer> afterMap = some.flatMap((r) -> new Some<>(r.length()));
        Assert.assertEquals("", (int) afterMap.value(), str.length());

        Option<Integer> noMap = some.flatMap((r) -> new None<>());
        Assert.assertFalse(noMap.hasValue());

        final Option<String> none = new None<>();
        afterMap = none.flatMap((r) -> new Some<>(r.length()));
        Assert.assertFalse(afterMap.hasValue());
    }
}

