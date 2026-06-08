package com.gmail.ia.reader.utils;

import java.util.function.Supplier;

public class ThrowableActions {
    public static <T extends RuntimeException> void launchRuntimeExeption(Supplier<T> exeption){
        throw exeption.get();
    }
}
