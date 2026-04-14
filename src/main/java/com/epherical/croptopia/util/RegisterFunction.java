package com.epherical.croptopia.util;


import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

import java.util.function.Function;

public interface RegisterFunction<T> {
    T register(Identifier id, Function<Identifier, T> object);
}
