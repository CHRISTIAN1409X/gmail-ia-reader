package com.gmail.ia.reader.infraestructure.config.google;



import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
//import com.google.auth.http.HttpCredentialsAdapter;
//import com.google.auth.oauth2.UserCredentials;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.UserCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Arrays;
import java.util.List;



@Configuration
public class GmailConfig {

    @Value("${gmail.client-id}")
    private String clientId;

    @Value("${gmail.client-secret}")
    private String clientSecret;

    @Value("${gmail.refresh-token}")
    private String refreshToken;

    @Value("${gmail.application-name}")
    private String applicationName;

    private UserCredentials getCredentials() {
        return UserCredentials.newBuilder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRefreshToken(refreshToken)
                .build();
    }

    @Bean
    public Gmail gmailService() throws Exception {
        NetHttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        return new Gmail.Builder(
                transport,
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(getCredentials()))
                .setApplicationName(applicationName)
                .build();
    }

    @Bean
    public Drive driveService() throws Exception {
        NetHttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        return new Drive.Builder(
                transport,
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(getCredentials()))
                .setApplicationName(applicationName)
                .build();
    }
}
















/*

@Component
public class GmailConfig {
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Arrays.asList(
            GmailScopes.GMAIL_READONLY,
            GmailScopes.GMAIL_SEND,
            DriveScopes.DRIVE
    );
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Bean
    public Gmail getGmailService() throws Exception {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        File credentialsFile = new File("credentials.json");

        if (!credentialsFile.exists()) {
            throw new FileNotFoundException("No se encontró credentials.json en la raíz del proyecto.");
        }

        InputStream in = new FileInputStream(credentialsFile);

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();


        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        System.out.println("ACCESS TOKEN: " + credential.getAccessToken());
        System.out.println("REFRESH TOKEN: " + credential.getRefreshToken());

        return new Gmail.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName("Spring Boot Gmail Extractor")
                .build();
    }


}

 */





