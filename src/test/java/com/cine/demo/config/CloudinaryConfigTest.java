package com.cine.demo.config;

import com.cloudinary.Cloudinary;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class CloudinaryConfigTest {

    @Test
    void cloudinary_beanIsNotNull() {
        CloudinaryConfig config = new CloudinaryConfig();
        ReflectionTestUtils.setField(config, "cloudName", "test-cloud");
        ReflectionTestUtils.setField(config, "apiKey", "test-key");
        ReflectionTestUtils.setField(config, "apiSecret", "test-secret");

        Cloudinary cloudinary = config.cloudinary();

        assertThat(cloudinary).isNotNull();
    }

    @Test
    void cloudinary_hasCorrectCloudName() {
        CloudinaryConfig config = new CloudinaryConfig();
        ReflectionTestUtils.setField(config, "cloudName", "my-cloud");
        ReflectionTestUtils.setField(config, "apiKey", "key");
        ReflectionTestUtils.setField(config, "apiSecret", "secret");

        Cloudinary cloudinary = config.cloudinary();

        assertThat(cloudinary.config.cloudName).isEqualTo("my-cloud");
    }

    @Test
    void cloudinary_hasCorrectApiKey() {
        CloudinaryConfig config = new CloudinaryConfig();
        ReflectionTestUtils.setField(config, "cloudName", "cloud");
        ReflectionTestUtils.setField(config, "apiKey", "my-api-key");
        ReflectionTestUtils.setField(config, "apiSecret", "secret");

        Cloudinary cloudinary = config.cloudinary();

        assertThat(cloudinary.config.apiKey).isEqualTo("my-api-key");
    }
}
