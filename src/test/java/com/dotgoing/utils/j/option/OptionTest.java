package com.dotgoing.utils.j.option;

import org.junit.Assert;
import org.junit.Test;

public class OptionTest {

    @Test
    public void map_test() {
        String str = "hallo";
        Option<String> some = new Some(str);
        Option<Integer> afterMap = some.map((r) -> r.length());

        Assert.assertTrue("", afterMap.value() == str.length());

        Option<String> none = new None<>();
        afterMap = none.map((r) -> r.length());

        Assert.assertTrue("none map nothing", !afterMap.hasValue());
    }


    @Test
    public void flat_map_test() {
        final String str = "hallo";
        final Option<String> some = new Some(str);
        Option<Integer> afterMap = some.flatMap((r) -> new Some(r.length()));
        Assert.assertTrue("", afterMap.value() == str.length());

        Option<Integer> noMap = some.flatMap((r) -> new None());
        Assert.assertTrue("", !noMap.hasValue());

        final Option<String> none = new None<>();
        afterMap = none.flatMap((r) -> new Some(r.length()));
        Assert.assertTrue("none map nothing", !afterMap.hasValue());
    }
}

