package com.cours_m2.gastroguidemichelin.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class StorageDTO {
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class UploadRequest {
        String resourceType;
        String contentType;
        String filename;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class UploadResponse {
        String uploadUrl;
        String imageKey;
        String method;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class DownloadResponse {
        String url;
    }
}
