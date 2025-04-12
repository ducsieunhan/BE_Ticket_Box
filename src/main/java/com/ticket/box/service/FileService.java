package com.ticket.box.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {
    @Value("${ticketbox.upload-file.base-uri}")
    private String baseURI;

    public void createDirectory(String folder) throws URISyntaxException {
        // uri is url for access file
        // path is file in device
        // uri for using Paths
        URI uri = new URI(folder);
        // convert uri into path
        Path path = Paths.get(uri);
        File tmpDir = new File(path.toString());
        // Path path = Paths.get(folder);
        // File tmpDir = new File(path.toString());
        if (!tmpDir.isDirectory()) {
            try {
                Files.createDirectory(tmpDir.toPath());
                System.out.println("CREATE NEW DIRECTORY SUCCESSFUL, PATH =" + tmpDir.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(">>>DIRECTORY ALREADY EXISTS");
        }
    }

    public String store(MultipartFile file, String folder) throws URISyntaxException, IOException {
        // get and validate file name
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) {
            throw new IOException("Invalid file name");
        }
        // config file name tranh dau cach va mot so ky tu dac biet
        String sanitizedFileName = originalFileName.replaceAll(" ", "_");
        sanitizedFileName = sanitizedFileName.replaceAll("[^a-zA-Z0-9._-]", "");

        // tao unique file name tranh trung ten bi replace
        String finalName = System.currentTimeMillis() + "-" + sanitizedFileName;

        // tao file path
        URI uri = new URI(baseURI + folder + "/" + finalName);
        Path path = Paths.get(uri);

        // copy uploaded file den thu muc da tao
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        }

        return finalName;
    }
}
