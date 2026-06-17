package com.gmail.ia.reader.domain.enums;

public enum DriveFolderEnum {
    TEMPORAL("temporal"),
    APROBADOS("aprobados"),
    MICROCURRICULUMS("micro-curriculums");

    private final String folderName;

    DriveFolderEnum(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderName() {
        return folderName;
    }
}
