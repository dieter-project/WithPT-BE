package com.sideproject.withpt.common.utils;

import com.sideproject.withpt.config.AwsS3MockConfig;
import io.findify.s3mock.S3Mock;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@Import(AwsS3MockConfig.class)
@SpringBootTest(properties = "spring.config.location="
    + "classpath:/application.yml,"
    + "classpath:/application-oauth.yml,"
    + "classpath:/application-db.yml")
class S3ComponentTest {

    @Autowired
    private S3Mock s3Mock;

    @Autowired
    private AwsS3Uploader awsS3Uploader;

    private String urlPath;
    String directory = "test";
    String subDirectory = "user1";

    @AfterEach
    public void tearDown() {
        awsS3Uploader.delete(directory + "/" + subDirectory, urlPath);
        s3Mock.stop();
    }

    @Test
    void upload() {
        String path  = "test.png";
        String contentType = "image/png";
        MockMultipartFile file = new MockMultipartFile("test", path, contentType, "test".getBytes());

        urlPath = awsS3Uploader.upload(directory, subDirectory, file);

        Assertions.assertThat(urlPath).isNotNull();
    }
}