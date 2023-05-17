package scs.planus.infra.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import scs.planus.global.exception.PlanusException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static scs.planus.global.exception.CustomExceptionStatus.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class AmazonS3Uploader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    public String upload(MultipartFile multipartFile, String dirName) {
        try {
            File uploadFile = convert(multipartFile).orElseThrow(() -> new PlanusException(INVALID_FILE));
            return upload(uploadFile, dirName);
        } catch (IOException e) {
            throw new PlanusException(INTERNAL_SERVER_ERROR);
        }
    }

    // 사진 업데이트시, 기존 파일 삭제
    public void deleteImage(String fileName) {
        if (fileName != null) {
            AmazonS3URI amazonS3URI = new AmazonS3URI(fileName);
            String s3URIKey = amazonS3URI.getKey();
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, s3URIKey));
        }
    }

    public String updateImage(MultipartFile multipartFile, String oldImageProfileUrl, String dirName) {
        if (dirName.equals("members")) {
            return updateMemberImage(multipartFile, oldImageProfileUrl, dirName);
        }

        return updateGroupImage(multipartFile, oldImageProfileUrl, dirName);

    }

    // S3로 파일 업로드하기
    private String upload(File uploadFile, String dirName) {
        String fileName = dirName + "/" + UUID.randomUUID() + uploadFile.getName().replaceAll(" ", "");
        String uploadImageUrl = putS3(uploadFile, fileName);
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    // S3로 업로드
    private String putS3(File uploadFile, String fileName) {
        try {
            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
            return amazonS3Client.getUrl(bucket, fileName).toString();
        } catch (AmazonS3Exception e) {
            removeNewFile(uploadFile);
            throw new PlanusException(INVALID_FILE_EXTENSION);
        }
    }

    // 로컬에 저장된 이미지 지우기
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("File delete success");
            return;
        }
        log.info("File delete fail");
    }

    // 로컬에 파일 업로드 하기
    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(System.getProperty("user.dir") + "/" + file.getOriginalFilename());
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }

        return Optional.empty();
    }

    // 프로필 이미지 제거 가능, 기본 프로필로의 변경
    private String updateMemberImage(MultipartFile multipartFile, String oldImageProfileUrl, String dirName) {
        if (multipartFile != null) {
            deleteImage(oldImageProfileUrl);
            return upload(multipartFile, dirName);
        }
        deleteImage(oldImageProfileUrl);
        return null;
    }

    // 그룹 이미지 제거 불가능
    private String updateGroupImage(MultipartFile multipartFile, String oldImageProfileUrl, String dirName) {
        if (multipartFile != null) {
            deleteImage(oldImageProfileUrl);
            return upload(multipartFile, dirName);
        }
        return oldImageProfileUrl;
    }

}

