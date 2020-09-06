package com.dog.utils.core;

import com.dog.utils.option.Cat;
import com.dog.utils.option.Option;
import com.dog.utils.option.Some;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

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
    public void act_test() {
        Cat<String> someStr = Cat.of("origin");
        Cat<String> noStr = Cat.empty();

        AtomicInteger intData = new AtomicInteger(0);

        intData.set(0);
        noStr.actOnSome((d) -> intData.set(10)).getData().block();
        Assert.assertEquals(0, intData.get());

        intData.set(0);
        someStr.actOnSome((d) -> intData.set(20)).getData().block();
        Assert.assertEquals(20, intData.get());

        intData.set(0);
        noStr.actOnNone((d) -> intData.set(30)).getData().block();
        Assert.assertEquals(30, intData.get());

        intData.set(0);
        someStr.actOnNone((d) -> intData.set(40)).getData().block();
        Assert.assertEquals(0, intData.get());

        intData.set(0);
        noStr.act((d) -> intData.set(30)).getData().block();
        Assert.assertEquals(30, intData.get());

        intData.set(0);
        someStr.act((d) -> intData.set(40)).getData().block();
        Assert.assertEquals(40, intData.get());

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
    public void flat_either_map_test() {
        String originStr = "origin-str";
        int back = -1;
        int mapInt = 10;
        Cat<String> someStr = Cat.of(originStr);
        Cat<String> noStr = Cat.empty();
        int intValue;

        intValue = someStr.eitherFlatMap((t) -> Cat.of(Option.of(mapInt)), (e) -> Cat.of(back)).get();
        Assert.assertEquals(mapInt, intValue);

        intValue = noStr.eitherFlatMap((t) -> Cat.of(Option.of(mapInt)), (e) -> Cat.of(back)).get();
        Assert.assertEquals(back, intValue);
    }

    @Test
    public void either_map_test() {
        String originStr = "origin-str";
        int back = -1;
        int mapInt = 10;
        Cat<String> someStr = Cat.of(originStr);
        Cat<String> noStr = Cat.empty();
        int intValue;

        intValue = someStr.eitherMap((t) -> Option.of(mapInt), (e) -> Some.of(back)).get();
        Assert.assertEquals(mapInt, intValue);

        intValue = noStr.eitherMap((t) -> Option.of(mapInt), (e) -> Some.of(back)).get();
        Assert.assertEquals(back, intValue);
    }

    @Test
    public void flow_if_test() {
        final String originStr = "origin-str";
        final String goodStr = "good";
        Cat<String> someStr = Cat.of(originStr);
        Cat<String> noStr = Cat.empty();

        Option<String> opValue;
        String flowedValue;
        String notFlowedValue;

        flowedValue = someStr.someFlatFlowIf(true, s -> Cat.of(goodStr)).get();
        Assert.assertEquals(goodStr, flowedValue);

        opValue = someStr.someFlatFlowIf(true, s -> Cat.empty("no")).getOption();
        Assert.assertTrue(opValue.hasNoValue());

        opValue = noStr.someFlatFlowIf(true, s -> Cat.of(goodStr)).getOption();
        Assert.assertTrue(opValue.hasNoValue());

        notFlowedValue = someStr.someFlatFlowIf(false, s -> Cat.of(goodStr)).get();
        Assert.assertEquals(originStr, notFlowedValue);

        opValue = noStr.someFlatFlowIf(false, s -> Cat.of(goodStr)).getOption();
        Assert.assertTrue(opValue.hasNoValue());

        flowedValue = someStr.someFlowIf(true, s -> Option.of(goodStr)).get();
        Assert.assertEquals(goodStr, flowedValue);

        opValue = noStr.someFlowIf(true, s -> Option.of(goodStr)).getOption();
        Assert.assertTrue(opValue.hasNoValue());

        notFlowedValue = someStr.someFlowIf(false, s -> Option.of(goodStr)).get();
        Assert.assertEquals(originStr, notFlowedValue);

        opValue = noStr.someFlowIf(false, s -> Option.of(goodStr)).getOption();
        Assert.assertTrue(opValue.hasNoValue());
    }

    @Test
    public void filter_test() {
        String originStr = "origin-str";
        String backStr = "back";
        Option<String> someStr = Option.of(originStr);
        Option<String> noStr = Option.empty();

        String result = someStr.filter((v) -> v.length() > originStr.length()).getOrElse(backStr);
        Assert.assertEquals(backStr, result);

        result = someStr.filter((v) -> v.length() == originStr.length()).getOrElse(backStr);
        Assert.assertEquals(originStr, result);

        result = noStr.filter((v) -> v.length() > 0).getOrElse(backStr);
        Assert.assertEquals(backStr, result);

        result = someStr.filter((v) -> v.length() > originStr.length()).getOrElseLazy(() -> backStr);
        Assert.assertEquals(backStr, result);

        result = someStr.filter((v) -> v.length() == originStr.length()).getOrElseLazy(() -> backStr);
        Assert.assertEquals(originStr, result);

        result = noStr.filter((v) -> v.length() > 0).getOrElseLazy(() -> backStr);
        Assert.assertEquals(backStr, result);

        // 验证lazy模块没有副作用
        AtomicInteger hasEffect = new AtomicInteger(0);
        result = noStr.filter((v) -> v.length() > 0).getOrElseLazy(() -> {
            hasEffect.getAndIncrement();
            return backStr;
        });
        Assert.assertEquals(1, hasEffect.get());

        AtomicInteger hasNoEffect = new AtomicInteger(0);
        result = someStr.filter((v) -> v.length() == originStr.length()).getOrElseLazy(() -> {
            hasNoEffect.getAndIncrement();
            return backStr;
        });
        Assert.assertEquals(0, hasNoEffect.get());
    }

    @Test
    public void cat_filter_test() {
        String originStr = "origin-str";
        String backStr = "back";
        Cat<String> someStr = Cat.of(originStr);
        Cat<String> noStr = Cat.empty();

        String result = someStr.filter((v) -> v.length() > originStr.length()).getOrElse(backStr);
        Assert.assertEquals(backStr, result);

        result = someStr.filter((v) -> v.length() == originStr.length()).getOrElse(backStr);
        Assert.assertEquals(originStr, result);

        result = noStr.filter((v) -> v.length() > 0).getOrElse(backStr);
        Assert.assertEquals(backStr, result);
    }

    @Test
    public void cat_check_test() {
        String originStr = "origin-str";
        String backStr = "back";
        Cat<String> someStr = Cat.of(originStr);
        Cat<String> noStr = Cat.empty();

        String result;
        result = someStr.someCheck(v -> {
            if (v.length() > 2) {
                throw new RuntimeException("string len is longer than 2");
            }
        }).getOrElse(backStr);
        Assert.assertEquals(backStr, result);

        result = someStr.someCheck(v -> {
            if (v.length() > 20) {
                throw new RuntimeException("string len is longer than 20");
            }
        }).getOrElse(backStr);
        Assert.assertEquals(originStr, result);

        result = noStr.someCheck(v -> {
            if (v.length() > 2) {
                throw new RuntimeException("string len is longer than 2");
            }
        }).getOrElse(backStr);
        Assert.assertEquals(backStr, result);

        result = noStr.someCheck(v -> {
            if (v.length() > 20) {
                throw new RuntimeException("string len is longer than 20");
            }
        }).getOrElse(backStr);
        Assert.assertEquals(backStr, result);
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
