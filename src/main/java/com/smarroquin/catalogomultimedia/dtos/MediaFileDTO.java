package com.smarroquin.catalogomultimedia.dtos;

import com.smarroquin.catalogomultimedia.enums.*;

import java.time.OffsetDateTime;

public class MediaFileDTO {
    private String blob_url;
    private String blobName;
    private String etag;
    private String content_type;
    private Long size_bytes;
    private file_type file_type;
    private OffsetDateTime uploaded_at;
    private String uploaded_by;

    private String signedUrl;
    private String publicUrl;

    // Getters/Setters
    public String getBlob_url() {
        return blob_url;
    }

    public void setBlob_url(String blob_url) {
        this.blob_url = blob_url;
    }

    public String getBlobName() {
        return blobName;
    }

    public void setBlobName(String blobName) {
        this.blobName = blobName;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public Long getSize_bytes() {
        return size_bytes;
    }

    public void setSize_bytes(Long size_bytes) {
        this.size_bytes = size_bytes;
    }

    public file_type getFile_type() {
        return file_type;
    }

    public void setFile_type(file_type file_type) {
        this.file_type = file_type;
    }

    public OffsetDateTime getUploaded_at() {
        return uploaded_at;
    }

    public void setUploaded_at(OffsetDateTime uploaded_at) {
        this.uploaded_at = uploaded_at;
    }

    public String getUploaded_by() {
        return uploaded_by;
    }

    public void setUploaded_by(String uploaded_by) {
        this.uploaded_by = uploaded_by;
    }

    public String getSignedUrl() {
        return signedUrl;
    }

    public void setSignedUrl(String signedUrl) {
        this.signedUrl = signedUrl;
    }

    public String getPublicUrl() {
        return publicUrl;
    }

    public void setPublicUrl(String publicUrl) {
        this.publicUrl = publicUrl;
    }
}
