package com.gmail.ia.reader.domain.logic;

import com.gmail.ia.reader.domain.dtos.cloude.PathPart;

import java.util.ArrayList;
import java.util.List;
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
}
