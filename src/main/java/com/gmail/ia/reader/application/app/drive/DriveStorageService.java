package com.gmail.ia.reader.application.app.drive;

import com.gmail.ia.reader.domain.enums.DriveFolderEnum;
import com.gmail.ia.reader.domain.dtos.gmail.pdf.PdfDocument;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@Component
public class DriveStorageService {

/*
    private static final String ROOT_FOLDER = "root";
    private static final String FOLDER_MIME_TYPE =
            "application/vnd.google-apps.folder";

    private final Drive driveService;


    public String uploadPdf(
            DriveFolderEnum driveFolderEnum,
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





 */

    @Value("${drive.root-folder-id}")
    private String rootFolderId;

    private final Drive drive;

    public String uploadPdf(
            DriveFolderEnum area,
            String relativePath,
            PdfDocument pdfDocument){

        try {

            String folderId =
                    resolveFolderHierarchy(
                            rootFolderId,
                            area,
                            relativePath
                    );

            File metadata = new File();

            String finalName =
                    LocalDateTime.now()
                            .format(
                                    DateTimeFormatter.ofPattern(
                                            "yyyyMMdd_HHmmss"
                                    )
                            )
                            + "_"
                            + pdfDocument.fileName();

            metadata.setName(
                    finalName
            );

            metadata.setParents(
                    List.of(folderId)
            );

            InputStreamContent content =
                    new InputStreamContent(
                            "application/pdf",
                            Files.newInputStream(
                                   pdfDocument.tempFile()
                            )
                    );

            File uploaded =
                    drive.files()
                            .create(metadata, content)
                            .setFields("id")
                            .execute();

            return uploaded.getId();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error subiendo PDF a Drive",
                    e
            );
        }
    }

    private String resolveFolderHierarchy(
            String rootFolderId,
            DriveFolderEnum area,
            String path) throws IOException {

        String currentParentId = rootFolderId;

        currentParentId =
                findOrCreateFolder(
                        area.getFolderName(),
                        currentParentId
                );

        if (path == null || path.isBlank()) {
            return currentParentId;
        }

        String[] folders =
                path.split("/");

        for (String folder : folders) {

            currentParentId =
                    findOrCreateFolder(
                            folder,
                            currentParentId
                    );
        }

        return currentParentId;
    }

    private String findOrCreateFolder(
            String folderName,
            String parentId) throws IOException {

        String query =
                String.format(
                        "mimeType='application/vnd.google-apps.folder' " +
                                "and name='%s' " +
                                "and '%s' in parents " +
                                "and trashed=false",
                        escapeDriveQuery(folderName),
                        parentId
                );

        FileList result =
                drive.files()
                        .list()
                        .setQ(query)
                        .setFields("files(id,name)")
                        .execute();

        if (!result.getFiles().isEmpty()) {
            return result
                    .getFiles()
                    .get(0)
                    .getId();
        }

        File folder = new File();

        folder.setName(folderName);

        folder.setMimeType(
                "application/vnd.google-apps.folder"
        );

        folder.setParents(
                List.of(parentId)
        );

        File created =
                drive.files()
                        .create(folder)
                        .setFields("id")
                        .execute();

        return created.getId();
    }

    public void moveToApproved(
            String fileId,
            String path) {

        try {

            String approvedFolderId =
                    resolveFolderHierarchy(
                            rootFolderId,
                            DriveFolderEnum.APROBADOS,
                            path
                    );

            File file =
                    drive.files()
                            .get(fileId)
                            .setFields("parents")
                            .execute();

            String previousParents =
                    String.join(
                            ",",
                            file.getParents()
                    );

            drive.files()
                    .update(fileId, null)
                    .setAddParents(
                            approvedFolderId
                    )
                    .setRemoveParents(
                            previousParents
                    )
                    .execute();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error moviendo archivo a aprobados",
                    e
            );
        }
    }

    private String escapeDriveQuery(
            String value) {

        return value
                .replace("\\", "\\\\")
                .replace("'", "\\'");
    }

}
