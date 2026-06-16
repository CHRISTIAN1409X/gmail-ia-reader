package com.gmail.ia.reader.domain.logic;

import com.gmail.ia.reader.domain.dtos.cloude.PathPart;

import java.util.*;
import java.util.stream.Collectors;

public class EmailUtils {

    public static String recreatePath(List<PathPart> pathPartList){
        List<PathPart> copyPathPartList = new ArrayList<>(pathPartList);
        return concatenePath(copyPathPartList);
    }

    private static String concatenePath(List<PathPart> copyList){
        return copyList
                .stream()
                .map(PathPart::partPath)
                .collect(Collectors.joining("/"));
    };

    public static Optional<String> extractItemSubject(String subject) {

        Set<String> validItems = Set.of("INDU", "SOFT");

        return Arrays.stream(
                        subject.toUpperCase()
                                .replace('-', ' ')
                                .replace('_', ' ')
                                .trim()
                                .split("\\s+")
                )
                .filter(validItems::contains)
                .findFirst();
    }
}
