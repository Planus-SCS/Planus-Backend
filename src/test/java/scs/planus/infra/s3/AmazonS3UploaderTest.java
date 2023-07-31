package scs.planus.infra.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import scs.planus.support.ServiceTest;

import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class AmazonS3UploaderTest extends ServiceTest {

    public static final String BASE_URL = "https://test-planus-bucket.s3.amazonaws.com/";
    private static final String TEST_BUCKET = "test-planus-bucket";
    private static final String TEST_DIRECTORY = "test-directory";

    @MockBean
    private AmazonS3Client amazonS3Client;
    private AmazonS3Uploader amazonS3Uploader;

    @BeforeEach
    void init() {
        amazonS3Uploader = new AmazonS3Uploader(amazonS3Client, TEST_BUCKET);
    }

    @DisplayName("S3에 이미지가 업로드되어야 한다.")
    @Test
    void upload() throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile("test-image", "test.png", "image/png", "test".getBytes());
        String urlPath = BASE_URL + TEST_DIRECTORY + "/" + file.getOriginalFilename();

        given(amazonS3Client.putObject(any(PutObjectRequest.class)))
                .willReturn(new PutObjectResult());
        given(amazonS3Client.getUrl(anyString(), anyString()))
                .willReturn(new URL(urlPath));

        //when
        String upload = amazonS3Uploader.upload(file, TEST_DIRECTORY);

        //then
        assertThat(upload).isEqualTo(urlPath);
    }

    @DisplayName("이미지 변경 시, 기존의 이미지가 제거된 후, 새로운 이미지가 S3에 업로드되어야 한다.")
    @Test
    void updateImage() throws Exception {
        //given
        MockMultipartFile oldFile = new MockMultipartFile("oldImage", "old-image.png", "image/png", "test".getBytes());
        String oldUrlPath = BASE_URL + TEST_DIRECTORY + "/" + oldFile.getOriginalFilename();

        MockMultipartFile updatedFile = new MockMultipartFile("updatedImage", "new-image.png", "image/png", "test".getBytes());
        String updatedUrlPath = BASE_URL + TEST_DIRECTORY + "/" + updatedFile.getOriginalFilename();

        given(amazonS3Client.putObject(any(PutObjectRequest.class)))
                .willReturn(new PutObjectResult());
        given(amazonS3Client.getUrl(anyString(), anyString()))
                .willReturn(new URL(oldUrlPath))
                .willReturn(new URL(updatedUrlPath));

        amazonS3Uploader.upload(oldFile, TEST_DIRECTORY);

        //when
        String updatedImage = amazonS3Uploader.updateImage(oldFile, oldUrlPath, TEST_DIRECTORY);

        //then
        verify(amazonS3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
        assertThat(updatedImage).isEqualTo(updatedUrlPath);
    }

    @DisplayName("이미지가 변경되지 않았더라면 이전 이미지 URL을 반환한다.")
    @Test
    void updateImage_If_Not_Update_Image_Then_Return_Old_Image_Url() throws Exception {

        //given
        MockMultipartFile oldFile = new MockMultipartFile("oldImage", "old-image.png", "image/png", "test".getBytes());
        String oldUrlPath = BASE_URL + TEST_DIRECTORY + "/" + oldFile.getOriginalFilename();

        given(amazonS3Client.putObject(any(PutObjectRequest.class)))
                .willReturn(new PutObjectResult());
        given(amazonS3Client.getUrl(anyString(), anyString()))
                .willReturn(new URL(oldUrlPath));

        amazonS3Uploader.upload(oldFile, TEST_DIRECTORY);

        //when
        String updatedImage = amazonS3Uploader.updateImage(null, oldUrlPath, TEST_DIRECTORY);

        //then
        assertThat(updatedImage).isEqualTo(oldUrlPath);
    }
}