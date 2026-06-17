package com.gmail.ia.reader.application.app.drive;

import com.gmail.ia.reader.domain.dtos.drive.UploadDriveResponse;
import com.gmail.ia.reader.domain.enums.DriveFolderEnum;
import com.gmail.ia.reader.domain.dtos.gmail.pdf.PdfDocument;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@Component
public class DriveStorageService {
    @Value("${drive.root-folder-id}")
    private String rootFolderId;

    @Value("${drive.mc-root-folder-id}")
    private String mcRootFolderId;

    private final String PREFIXMC = "MC";

    private final Drive drive;

    public UploadDriveResponse uploadPdf(
            DriveFolderEnum area,
            String relativePath,
            PdfDocument pdfDocument) {

        try {
            String folderId = resolveFolderHierarchy(rootFolderId, area, relativePath);

            File metadata = new File();
            String finalName = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                    + "_"
                    + pdfDocument.fileName();

            metadata.setName(finalName);
            metadata.setParents(List.of(folderId));

            InputStreamContent content = new InputStreamContent(
                    "application/pdf",
                    Files.newInputStream(pdfDocument.tempFile())
            );

            File uploaded = drive.files()
                    .create(metadata, content)
                    .setFields("id, webViewLink")
                    .execute();

            Permission readerPermission = new Permission()
                    .setType("anyone")
                    .setRole("reader");

            drive.permissions()
                    .create(uploaded.getId(), readerPermission)
                    .execute();


            return new UploadDriveResponse(uploaded.getId(), uploaded.getWebViewLink());

        } catch (Exception e) {
            throw new RuntimeException("Error subiendo PDF a Drive y configurando permisos", e);
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

        try {

            // NUEVO:
            // Primero intenta encontrar la carpeta.
            return findFolder(
                    folderName,
                    parentId
            );

        } catch (IllegalArgumentException e) {

            // Si no existe, la crea.
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
    }

    private String findFolder(
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

        if (result.getFiles().isEmpty()) {
            throw new IllegalArgumentException(
                    "No existe la carpeta: " + folderName
            );
        }

        return result.getFiles()
                .get(0)
                .getId();
    }

    public String moveToApproved(String fileId, String path) {
        try {
            String approvedFolderId = resolveFolderHierarchy(rootFolderId, DriveFolderEnum.APROBADOS, path);


            File file = drive.files()
                    .get(fileId)
                    .setFields("parents")
                    .execute();
            String previousParents = String.join(",", file.getParents());

            Permission readerPermission = new Permission()
                    .setType("anyone")
                    .setRole("reader");

            drive.permissions()
                    .create(fileId, readerPermission)
                    .execute();

            File updatedFile = drive.files()
                    .update(fileId, null)
                    .setAddParents(approvedFolderId)
                    .setRemoveParents(previousParents)
                    .setFields("webViewLink")
                    .execute();

            return updatedFile.getWebViewLink();

        } catch (Exception e) {
            throw new RuntimeException("Error moviendo archivo a aprobados y configurando permisos", e);
        }
    }



    private String findFileId(
            String fileName,
            String parentId) throws IOException {

        String query =
                String.format(
                        "name='%s' " +
                                "and '%s' in parents " +
                                "and trashed=false",
                        escapeDriveQuery(fileName),
                        parentId
                );

        FileList result =
                drive.files()
                        .list()
                        .setQ(query)
                        .setFields("files(id,name)")
                        .execute();

        if (result.getFiles().isEmpty()) {
            throw new IllegalArgumentException(
                    "No existe el archivo: " + fileName
            );
        }

        return result.getFiles()
                .get(0)
                .getId();
    }

    public PdfDocument getMcPdf(
            String area,
            String fileName) {

        try {
            // Busca INDU o SOFT dentro de la raíz MC.
            String areaFolderId =
                    findFolder(
                            area,
                            mcRootFolderId
                    );

            // Busca el PDF dentro de la carpeta.
            String fileId =
                    findFileId(
                           PREFIXMC.concat("_").concat(fileName),
                            areaFolderId
                    );

            Path temp =
                    Files.createTempFile(
                            "mc-pdf-",
                            ".pdf"
                    );

            try (OutputStream out =
                         Files.newOutputStream(temp)) {

                drive.files()
                        .get(fileId)
                        .executeMediaAndDownloadTo(out);
            }

            return new PdfDocument(
                    fileName,
                    temp
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error obteniendo PDF desde caperta MC de drive",
                    e
            );
        }
    }

    private String stripAccents(String value) {
        return java.text.Normalizer.normalize(value, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    private String escapeDriveQuery(
            String value) {

        return value
                .replace("\\", "\\\\")
                .replace("'", "\\'");
    }

}
