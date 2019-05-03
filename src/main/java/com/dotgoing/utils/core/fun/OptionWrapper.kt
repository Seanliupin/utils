package com.dotgoing.utils.core.`fun`

import com.dotgoing.utils.core.option.Option
import java.util.function.Function

class OptionWrapper<T>(val option: Option<T>) {

    fun noneMap(action: Function<String, T>): OptionWrapper<T> {
        return OptionWrapper(option.noneMap {
            action.apply(it)
        })
    }

    fun noneMapOne(transform: (String) -> T): OptionWrapper<T> {
        return OptionWrapper(option.noneMap(transform))
    }


}