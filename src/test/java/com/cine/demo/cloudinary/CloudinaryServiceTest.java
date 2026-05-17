package com.cine.demo.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cine.demo.service.impl.CloudinaryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloudinaryServiceTest {

    @Mock private Cloudinary cloudinary;
    @Mock private Uploader uploader;
    @Mock private MultipartFile file;

    @InjectMocks
    private CloudinaryServiceImpl cloudinaryService;

    @BeforeEach
    void setUp() {
        when(cloudinary.uploader()).thenReturn(uploader);
    }

    // ── uploadImage ───────────────────────────────────────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void uploadImage_returnsSecureUrl() throws IOException {
        when(file.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(uploader.upload(any(byte[].class), any(Map.class)))
                .thenReturn(Map.of("secure_url", "https://res.cloudinary.com/demo/image/upload/v1/folder/img.jpg"));

        String url = cloudinaryService.uploadImage(file, "movies");

        assertThat(url).isEqualTo("https://res.cloudinary.com/demo/image/upload/v1/folder/img.jpg");
    }

    @Test
    @SuppressWarnings("unchecked")
    void uploadImage_wrapsIOExceptionInRuntimeException() throws IOException {
        when(file.getBytes()).thenThrow(new IOException("disk error"));

        assertThatThrownBy(() -> cloudinaryService.uploadImage(file, "movies"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error uploading image to Cloudinary")
                .hasCauseInstanceOf(IOException.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    void uploadImage_passesFolderToUploader() throws IOException {
        when(file.getBytes()).thenReturn(new byte[0]);
        when(uploader.upload(any(byte[].class), any(Map.class)))
                .thenReturn(Map.of("secure_url", "https://example.com/img.jpg"));

        cloudinaryService.uploadImage(file, "posters");

        verify(uploader).upload(any(byte[].class), argThat(opts ->
                "posters".equals(((Map<?, ?>) opts).get("folder"))));
    }

    // ── deleteImage ───────────────────────────────────────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void deleteImage_callsDestroyWithExtractedPublicId() throws IOException {
        when(uploader.destroy(any(), any())).thenReturn(Map.of());
        String url = "https://res.cloudinary.com/demo/image/upload/v123/movies/poster.jpg";

        cloudinaryService.deleteImage(url);

        verify(uploader).destroy(eq("movies/poster"), any(Map.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void deleteImage_handlesUrlWithoutVersion() throws IOException {
        when(uploader.destroy(any(), any())).thenReturn(Map.of());
        String url = "https://res.cloudinary.com/demo/image/upload/movies/poster.png";

        cloudinaryService.deleteImage(url);

        verify(uploader).destroy(eq("movies/poster"), any(Map.class));
    }

    @Test
    void deleteImage_wrapsIOExceptionInRuntimeException() throws IOException {
        when(uploader.destroy(any(), any())).thenThrow(new IOException("network error"));
        String url = "https://res.cloudinary.com/demo/image/upload/v1/folder/img.jpg";

        assertThatThrownBy(() -> cloudinaryService.deleteImage(url))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error deleting image from Cloudinary")
                .hasCauseInstanceOf(IOException.class);
    }
}
