package com.dog.utils.core;

import com.dog.utils.option.Cat;
import com.dog.utils.option.Option;
import org.junit.Assert;
import org.junit.Test;

public class CatTest {

    @Test
    public void map_test() {
        String originStr = "origin";
        String defaultStr = "default-str";
        String mapStr = "map-str";
        int defaultInt = 20;
        int mapInt = 10;
        String strValue;
        Cat<String> someStr = Cat.of(originStr);
        Cat<String> noStr = Cat.empty();

        strValue = someStr.getOrElse(defaultStr);
        Assert.assertEquals(originStr, strValue);
        strValue = noStr.getOrElse(defaultStr);
        Assert.assertEquals(defaultStr, strValue);

        strValue = someStr.actOnSome((t) -> fakeError()).getOrElse(defaultStr);
        Assert.assertEquals(originStr, strValue);
        strValue = noStr.actOnSome((t) -> fakeError()).getOrElse(defaultStr);
        Assert.assertEquals(defaultStr, strValue);

        int intValue;
        intValue = someStr.someMap((t) -> Option.of(mapInt)).getOrElse(defaultInt);
        Assert.assertEquals(mapInt, intValue);
        intValue = noStr.someMap((t) -> Option.of(mapInt)).getOrElse(defaultInt);
        Assert.assertEquals(defaultInt, intValue);

        strValue = someStr.noneMap((t) -> Option.of(mapStr)).getOrElse(defaultStr);
        Assert.assertEquals(originStr, strValue);
        strValue = noStr.noneMap((t) -> Option.of(mapStr)).getOrElse(defaultStr);
        Assert.assertEquals(mapStr, strValue);
    }

    @Test
    public void flat_map_test() {
        String originStr = "origin-str";
        String defaultStr = "default-str";
        String mapStr = "map-str";
        int defaultInt = 20;
        int mapInt = 10;
        String strValue;
        Cat<String> someStr = Cat.of(originStr);
        Cat<String> noStr = Cat.empty();
        int intValue;

        intValue = someStr.someFlatMap((t) -> Cat.of(Option.of(mapInt))).getOrElse(defaultInt);
        Assert.assertEquals(mapInt, intValue);
        intValue = noStr.someFlatMap((t) -> Cat.of(Option.of(mapInt))).getOrElse(defaultInt);
        Assert.assertEquals(defaultInt, intValue);

        strValue = someStr.noneFlatMap((t) -> Cat.of(Option.of(mapStr))).getOrElse(defaultStr);
        Assert.assertEquals(originStr, strValue);
        strValue = noStr.noneFlatMap((t) -> Cat.of(Option.of(mapStr))).getOrElse(defaultStr);
        Assert.assertEquals(mapStr, strValue);
    }

    @Test
    public void exception() {
        String originStr = "origin-str";
        String defaultStr = "default-str";
        String mapStr = "map-str";
        int defaultInt = 20;
        int mapInt = 10;
        String strValue;
        Cat<String> someStr = Cat.of(originStr);
        Cat<String> noStr = Cat.empty();
        int intValue;

        strValue = someStr.someMap((t) -> {
            fakeError();
            return Option.of(mapStr);
        }).getOrElse(defaultStr);
        Assert.assertEquals(defaultStr, strValue);

        strValue = someStr.someFlatMap((t) -> {
            fakeError();
            return Cat.of(mapStr);
        }).getOrElse(defaultStr);
        Assert.assertEquals(defaultStr, strValue);

        intValue = someStr.someMap((t) -> {
            fakeError();
            return Option.of(mapInt);
        }).getOrElse(defaultInt);
        Assert.assertEquals(defaultInt, intValue);

        intValue = someStr.someFlatMap((t) -> {
            fakeError();
            return Cat.of(mapInt);
        }).getOrElse(defaultInt);
        Assert.assertEquals(defaultInt, intValue);


    }

    private void fakeError() {
        throw new RuntimeException("fake error");
    }

}
