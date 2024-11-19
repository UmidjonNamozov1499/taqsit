package com.example.taqsit.service;

import com.example.taqsit.entity.FileCatalog;
import com.example.taqsit.entity.FileStore;
import com.example.taqsit.entity.User;
import com.example.taqsit.payload.AllApiResponse;
import com.example.taqsit.repository.FileCatalogRepository;
import com.example.taqsit.repository.FileStoreRepository;
import com.example.taqsit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileStoreRepository fileStoreRepository;

    private final UserRepository userRepository;

    private final FileCatalogRepository fileCatalogRepository;

    @Value("${file.upload.url}")
    private String storeUrl;

    public HttpEntity<?> saveAvatar(MultipartFile multipartFile, User user) {
        try {
            if (user == null) return AllApiResponse.response(403, 0, "Auth error!");
            FileStore fileStore = new FileStore();
            fileStore.setFileSize(multipartFile.getSize());
            String originalFilename = multipartFile.getOriginalFilename();
            fileStore.setFileName(originalFilename);
            String dir = String.format("%s/%s", storeUrl, "avatar");
            File file = new File(dir);
            if (!file.exists()) {
                file.mkdirs();
            }
            if (originalFilename == null) return AllApiResponse.response(422, 0, "File type is wrong");
            String[] split = originalFilename.split("\\.");
            String fileName = String.format("%s/%s", dir, "item-" + new Date().getTime() + "." + split[split.length - 1]);
            file = new File(fileName);
            if (file.exists()) {
                return AllApiResponse.response(422, 0, "Error create file!");
            }
            file.createNewFile();
            multipartFile.transferTo(file);
            fileStore.setContentType(multipartFile.getContentType());
            fileStore.setFileUniqueName(fileName);
            FileStore save = fileStoreRepository.save(fileStore);
            user.setPhoto(save.getId());
            userRepository.save(user);
            return AllApiResponse.response(1, "Success", Map.of("fileId", save.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            return AllApiResponse.response(5050, 0, "Error", e.getMessage());
        }
    }

    public FileStore saveFile(MultipartFile multipartFile) {
        try {
            FileStore fileStore = new FileStore();
            fileStore.setFileSize(multipartFile.getSize());
            String originalFilename = multipartFile.getOriginalFilename();
            fileStore.setFileName(originalFilename);
            String dir = String.format("%s/%s", storeUrl, "files");
            File file = new File(dir);
            if (!file.exists()) {
                file.mkdirs();
            }
            if (originalFilename == null) throw new IllegalArgumentException("File type is wrong");
            String[] split = originalFilename.split("\\.");
            String fileName = String.format("%s/%s", dir, "item-" + new Date().getTime() + "." + split[split.length - 1]);
            file = new File(fileName);
            if (file.exists()) {
                throw new IllegalArgumentException("Error create file!");
            }
            file.createNewFile();
            multipartFile.transferTo(file);
            fileStore.setContentType(multipartFile.getContentType());
            fileStore.setFileUniqueName(fileName);
            FileStore save = fileStoreRepository.save(fileStore);
            return save;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Error create file");
        }
    }

    public List<FileStore> saveFileList(List<MultipartFile> multipartFile) {
        try {
            return multipartFile.stream().map(this::saveFile).toList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Error create files!");
        }
    }

    public FileCatalog getFileCatalog(MultipartFile file, List<MultipartFile> files, FileCatalog catalog, List<Integer> oldFiles) {
        try {
            if (catalog == null) {
                catalog = new FileCatalog();
            }
            if (catalog.getId() != null) {
                if (oldFiles != null && oldFiles.size() > 0) {
                    List<Integer> fileList = catalog.getFileList();
                    catalog.setFileList(fileList.stream().filter(oldFiles::contains).toList());
                }

            }
            if (file != null) {
                FileStore fileStore = saveFile(file);
                catalog.setGeneralFile(fileStore.getId());
            }
            if (files != null && files.size() > 0) {
                List<FileStore> stores = saveFileList(files);
                List<Integer> list = catalog.getFileList() != null ? new ArrayList<>(catalog.getFileList()) : new ArrayList<>();
                list.addAll(stores.stream().map(FileStore::getId).toList());
                catalog.setFileList(list);
            }
            return fileCatalogRepository.save(catalog);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Error save to file catalog!");
        }
    }

    public HttpEntity<?> getFile(Integer id) {
        try {
            Optional<FileStore> byName = fileStoreRepository.findById(id);
            if (byName.isPresent()) {
                FileStore fileStore = byName.get();
                Path pathFile = Paths.get(fileStore.getFileUniqueName());
                Resource resource = new UrlResource(pathFile.toUri());
                return ResponseEntity.ok().contentType(MediaType.parseMediaType(fileStore.getContentType()))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename-\"" + fileStore.getFileUniqueName() + "\"")
                        .body(resource);
            } else return AllApiResponse.response(404, 0, "File not found!");
        } catch (Exception e) {
            e.printStackTrace();
            return AllApiResponse.response(500, 0, "Error get file!");
        }
    }
}
