package com.gmail.ia.reader.application.app.drive;

import com.gmail.ia.reader.domain.dtos.gmail.pdf.PdfDocument;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class DriveStorageService {


    private static final String ROOT_FOLDER = "root";
    private static final String FOLDER_MIME_TYPE =
            "application/vnd.google-apps.folder";

    private final Drive driveService;


    public String uploadPdf(
            String fullPath,
            PdfDocument pdfDocument) throws Exception {

        String folderId = resolveOrCreatePath(fullPath);

        String existingFileId =
                findFileId(
                        pdfDocument.fileName(),
                        folderId
                );

        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(
                        pdfDocument.content()
                );

        InputStreamContent mediaContent =
                new InputStreamContent(
                        "application/pdf",
                        inputStream
                );

        if (existingFileId != null) {

            driveService.files()
                    .update(
                            existingFileId,
                            null,
                            mediaContent
                    )
                    .setFields("id")
                    .execute();

            return existingFileId;
        }

        File metadata = new File();
        metadata.setName(pdfDocument.fileName());
        metadata.setParents(
                Collections.singletonList(folderId)
        );

        File uploadedFile =
                driveService.files()
                        .create(metadata, mediaContent)
                        .setFields("id")
                        .execute();

        return uploadedFile.getId();
    }

    // Busca un PDF por ruta.
    public Optional<String> findPdfByPath(
            String fullPath,
            String fileName) throws Exception {

        String folderId = resolvePath(fullPath);

        if (folderId == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(
                findFileId(fileName, folderId)
        );
    }

    // Descarga un archivo usando su ruta.
    public Optional<InputStream> downloadPdf(
            String fullPath,
            String fileName) throws Exception {

        Optional<String> fileId =
                findPdfByPath(fullPath, fileName);

        if (fileId.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(
                driveService.files()
                        .get(fileId.get())
                        .executeMediaAsInputStream()
        );
    }

   //  Resuelve una ruta.
   //      No crea carpetas.
    private String resolvePath(
            String fullPath) throws Exception {

        String normalizedPath =
                normalizePath(fullPath);

        String currentParent = ROOT_FOLDER;

        for (String folder : normalizedPath.split("/")) {

            if (folder.isBlank()) {
                continue;
            }

            currentParent =
                    findFolderId(
                            folder,
                            currentParent
                    );

            if (currentParent == null) {
                return null;
            }
        }

        return currentParent;
    }

    // Resuelve una ruta creando carpetas faltantes.
    private String resolveOrCreatePath(
            String fullPath) throws Exception {

        String normalizedPath =
                normalizePath(fullPath);

        String currentParent = ROOT_FOLDER;

        for (String folder : normalizedPath.split("/")) {

            if (folder.isBlank()) {
                continue;
            }

            currentParent =
                    getOrCreateFolderId(
                            folder,
                            currentParent
                    );
        }

        return currentParent;
    }

    // Busca una carpeta dentro de otra.
    private String findFolderId(
            String folderName,
            String parentId) throws Exception {

        String query = String.format(
                "name='%s' and mimeType='%s' and '%s' in parents and trashed=false",
                escapeQueryValue(folderName),
                FOLDER_MIME_TYPE,
                parentId
        );

        FileList result =
                driveService.files()
                        .list()
                        .setQ(query)
                        .setFields("files(id)")
                        .execute();

        if (result.getFiles().isEmpty()) {
            return null;
        }

        return result.getFiles()
                .get(0)
                .getId();
    }

 // Busca o crea una carpeta.
    private String getOrCreateFolderId(
            String folderName,
            String parentId) throws Exception {

        String folderId =
                findFolderId(folderName, parentId);

        if (folderId != null) {
            return folderId;
        }

        File metadata = new File();
        metadata.setName(folderName);
        metadata.setMimeType(FOLDER_MIME_TYPE);
        metadata.setParents(
                Collections.singletonList(parentId)
        );

        File createdFolder =
                driveService.files()
                        .create(metadata)
                        .setFields("id")
                        .execute();

        return createdFolder.getId();
    }

   // Busca un archivo dentro de una carpeta.
    private String findFileId(
            String fileName,
            String parentId) throws Exception {

        String query = String.format(
                "name='%s' and '%s' in parents and trashed=false",
                escapeQueryValue(fileName),
                parentId
        );

        FileList result =
                driveService.files()
                        .list()
                        .setQ(query)
                        .setFields("files(id,name)")
                        .execute();

        if (result.getFiles().isEmpty()) {
            return null;
        }

        return result.getFiles()
                .get(0)
                .getId();
    }

    private String normalizePath(
            String path) {

        return path
                .replace("\\", "/")
                .trim();
    }

    private String escapeQueryValue(
            String value) {

        return value.replace("'", "\\'");
    }

    public void deletePdf(
            String fullPath,
            String fileName) throws Exception {

        Optional<String> fileId =
                findPdfByPath(
                        fullPath,
                        fileName
                );

        if (fileId.isEmpty()) {
            return;
        }

        driveService.files()
                .delete(fileId.get())
                .execute();
    }

    public void deleteById(
            String fileId) throws Exception {

        driveService.files()
                .delete(fileId)
                .execute();
    }


}
