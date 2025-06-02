package com.ticket.box.UnitTest.Service;

import com.ticket.box.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {
  @Mock
  private MultipartFile file ;

  @InjectMocks
  private FileService fileService;

  @TempDir
  private Path tempFolder;

  private String baseTestPath;

  @BeforeEach
  public void setUp(){
    baseTestPath = tempFolder.toUri().toString();
    ReflectionTestUtils.setField(fileService, "baseURI", baseTestPath);
  }

  @Test
  public void shouldCreateFolderWhenNotExist() throws URISyntaxException {
    String folder = baseTestPath + "test";

    this.fileService.createDirectory(folder);

    URI uri = new URI(folder);
    Path path = Paths.get(uri);
    File tmpDir = new File(path.toString());
    assertTrue(tmpDir.exists());
    assertTrue(tmpDir.isDirectory());
  }

  @Test
  public void shouldNotCreateFolderWhenExist() throws URISyntaxException, IOException {
    String folder = baseTestPath + "testExist";
    URI uri = new URI(folder);
    Path path = Paths.get(uri);
    Files.createDirectory(path);

    this.fileService.createDirectory(folder);

    File tmpDir = new File(path.toString());
    assertTrue(tmpDir.exists());
    assertTrue(tmpDir.isDirectory());
  }

  @Test
  public void shouldStoreNewFile() throws IOException, URISyntaxException {
    String folder = "uploads";
    String originalFilename = "test file.jpg";
    byte[] fileContent = "test content".getBytes();

    when(file.getOriginalFilename()).thenReturn(originalFilename);
    when(file.getInputStream()).thenReturn(new ByteArrayInputStream(fileContent));

    String directoryPath = baseTestPath + folder;
    fileService.createDirectory(directoryPath);

    String storedFilename = fileService.store(file, folder);

    assertNotNull(storedFilename);
    assertTrue(storedFilename.endsWith("test_file.jpg"));
  }
}