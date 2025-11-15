package com.smarroquin.catalogomultimedia.services;


import com.azure.core.http.rest.PagedIterable;
import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.*;
import com.azure.storage.blob.options.BlockBlobSimpleUploadOptions;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.azure.storage.blob.specialized.BlockBlobClient;
import com.azure.storage.common.sas.*;
import com.smarroquin.catalogomultimedia.dtos.MediaFileDTO;
import com.smarroquin.catalogomultimedia.enums.file_type;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.InputStream;
import java.time.*;
import java.util.*;
import java.util.function.Supplier;

@ApplicationScoped
public class AzureBlobStorageService {
    private BlobServiceClient blobServiceClient;
    private BlobContainerClient container;
    private static final String CONTAINER_NAME = "catalogos";

    public AzureBlobStorageService() {

    }

    @PostConstruct
    void init() {
        String conn = System.getProperty("AZURE_STORAGE_CONNECTION_STRING");
        if (conn == null || conn.isBlank()) {
            throw new IllegalStateException("AZURE_STORAGE_CONNECTION_STRING no está definida.");
        }

        blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(conn).buildClient();

        container = blobServiceClient.getBlobContainerClient(CONTAINER_NAME);
        if (!container.exists()) container.create();
    }

    /**
     * Crea el contenedor si no existe
     */
    private BlobContainerClient ensureContainer(String name) {
        BlobContainerClient client = blobServiceClient.getBlobContainerClient(name);
        if (!client.exists()) client.create();
        return client;
    }

    /**
     * Sube un archivo al contenedor según la estructura:
     * posters/{title_name}/{timestamp}.jpg o fichas/{title_name}/{timestamp}.pdf
     */
    public MediaFileDTO uploadCatalogFile(file_type file_type,
                                          String titleName,
                                          String originalFileName,
                                          String contentType,
                                          InputStream data,
                                          long sizeBytes,
                                          String uploadedBy,
                                          Duration sasTtl,
                                          boolean openInline) {


        String safeTitle = slug(titleName);
        String ext = guessExtension(originalFileName, contentType, file_type);

        // Fallback de content-type si viene vacío
        if (contentType == null || contentType.isBlank() || "application/octet-stream".equalsIgnoreCase(contentType)) {
            contentType = switch (ext.toLowerCase(Locale.ROOT)) {
                case "png" -> "image/png";
                case "jpg", "jpeg" -> "image/jpeg";
                case "pdf" -> "application/pdf";
                default -> "application/octet-stream";
            };
        }

        String blobPath = buildPath(file_type, safeTitle, ext);
        BlockBlobClient blob = container.getBlobClient(blobPath).getBlockBlobClient();

        BlobHttpHeaders headers = new BlobHttpHeaders().setContentType(contentType);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("uploadedBy", uploadedBy != null ? uploadedBy : "unknown");
        metadata.put("fileType", file_type.name());
        metadata.put("titleName", safeTitle);


        blob.uploadWithResponse(
                new BlockBlobSimpleUploadOptions(BinaryData.fromStream(data, sizeBytes))
                        .setHeaders(headers)
                        .setMetadata(metadata),
                null, null
        );

        BlobProperties props = blob.getProperties();


        OffsetDateTime expiryTime = OffsetDateTime.now().plusDays(365);

        BlobSasPermission sasPermission = new BlobSasPermission()
                .setReadPermission(true);

        BlobServiceSasSignatureValues sasSignatureValues = new BlobServiceSasSignatureValues(expiryTime, sasPermission)
                .setStartTime(OffsetDateTime.now().minusMinutes(5));

        String BlobSasUrl = this.buildBlobSasUrl(blob, Duration.ofDays(365), true, blob.getBlobName(), contentType);

        // DTO
        // DTO
        MediaFileDTO dto = new MediaFileDTO();
        dto.setBlobName(blobPath);
        dto.setBlob_url(blob.getBlobUrl());
        dto.setPublicUrl(blob.getBlobUrl());
        dto.setSignedUrl(BlobSasUrl);
        dto.setEtag(props.getETag());
        dto.setContent_type(props.getContentType());
        dto.setSize_bytes(props.getBlobSize());
        dto.setFile_type(file_type);
        dto.setUploaded_at(OffsetDateTime.now(ZoneOffset.UTC));
        dto.setUploaded_by(uploadedBy);

        return dto;

    }

    /**
     * Obtiene un blob por su nombre
     */
    public Optional<MediaFileDTO> getBlob(String blobName) {
        BlockBlobClient blob = container.getBlobClient(blobName).getBlockBlobClient();
        if (!blob.exists()) return Optional.empty();

        BlobProperties props = blob.getProperties();
        MediaFileDTO dto = new MediaFileDTO();
        dto.setBlob_url(blob.getBlobUrl());
        dto.setBlobName(blobName);
        dto.setEtag(props.getETag());
        dto.setContent_type(props.getContentType());
        dto.setSize_bytes(props.getBlobSize());
        dto.setUploaded_at(props.getLastModified());
        dto.setUploaded_by(getMeta(props.getMetadata(), "uploadedBy"));
        dto.setFile_type(fileTypeFromMetaOr(inferTypeByPath(blobName), props.getMetadata()));

        return Optional.of(dto);
    }

    /**
     * Lista todos los blobs o por prefijo
     */
    public List<MediaFileDTO> listBlobs(String prefix) {
        List<MediaFileDTO> result = new ArrayList<>();
        PagedIterable<BlobItem> items = (prefix == null || prefix.isEmpty())
                ? container.listBlobs()
                : container.listBlobsByHierarchy("/");

        for (BlobItem item : items) {
            String name = item.getName();
            BlockBlobClient blob = container.getBlobClient(name).getBlockBlobClient();
            BlobProperties props = safe(() -> blob.getProperties(), null);
            if (props == null) continue;

            MediaFileDTO dto = new MediaFileDTO();
            dto.setBlob_url(blob.getBlobUrl());
            dto.setBlobName(name);
            dto.setEtag(props.getETag());
            dto.setContent_type(props.getContentType());
            dto.setSize_bytes(props.getBlobSize());
            dto.setUploaded_at(props.getLastModified());
            dto.setUploaded_by(getMeta(props.getMetadata(), "uploadedBy"));
            dto.setFile_type(fileTypeFromMetaOr(inferTypeByPath(name), props.getMetadata()));
            result.add(dto);
        }
        return result;
    }

    public List<MediaFileDTO> listAllBlobs() {
        return listBlobs(null);
    }

    /**
     * Elimina físicamente un blob
     */
    public boolean deleteBlob(String blobName) {
        BlockBlobClient blob = container.getBlobClient(blobName).getBlockBlobClient();
        if (!blob.exists()) return false;
        blob.delete();
        return true;
    }

    /**
     * Genera una URL SAS de lectura temporal
     */
    public String generateBlobReadSasUrl(String blobName, Duration ttl) {
        BlobClient blobClient = container.getBlobClient(blobName);
        return this.buildBlobSasUrl(blobClient.getBlockBlobClient(), ttl, true, blobClient.getBlobName(), null);
    }

    /* -------------------- Helpers -------------------- */

    private static <T> T safe(Supplier<T> sup, T def) {
        try {
            return sup.get();
        } catch (Exception e) {
            return def;
        }
    }

    private static String slug(String text) {
        if (text == null) return "untitled";
        return text.trim().toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_+|_+$", "");
    }

    private static String guessExtension(String fileName, String contentType, file_type type) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        }
        if ("application/pdf".equalsIgnoreCase(contentType)) return "pdf";
        if ("image/png".equalsIgnoreCase(contentType)) return "png";
        if ("image/jpeg".equalsIgnoreCase(contentType)) return "jpg";
        return (type == file_type.TECHNICAL_SHEET) ? "pdf" : "jpg";
    }

    private static String buildPath(file_type type, String safeTitle, String ext) {
        String folder = (type == file_type.POSTER) ? "posters" : "fichas";
        long ts = System.currentTimeMillis();
        return String.format("%s/%s/%d.%s", folder, safeTitle, ts, ext);
    }

    private static String getMeta(Map<String, String> meta, String key) {
        return (meta != null && meta.containsKey(key)) ? meta.get(key) : null;
    }

    private static file_type fileTypeFromMetaOr(file_type fallback, Map<String, String> meta) {
        if (meta != null && meta.containsKey("fileType")) {
            try {
                return file_type.valueOf(meta.get("fileType"));
            } catch (Exception ignored) {
            }
        }
        return fallback;
    }

    private static file_type inferTypeByPath(String blobName) {
        if (blobName.startsWith("posters/")) return file_type.POSTER;
        if (blobName.startsWith("fichas/")) return file_type.TECHNICAL_SHEET;
        return null;
    }

    private String buildBlobSasUrl(BlockBlobClient blob,
                                   Duration ttl,
                                   boolean openInline,
                                   String downloadFileName,
                                   String contentType) {

        OffsetDateTime starts = OffsetDateTime.now().minusMinutes(5);
        OffsetDateTime expires = OffsetDateTime.now().plus(ttl);

        BlobSasPermission perm = new BlobSasPermission().setReadPermission(true);

        BlobServiceSasSignatureValues sv = new BlobServiceSasSignatureValues(expires, perm)
                .setStartTime(starts)
                .setProtocol(SasProtocol.HTTPS_ONLY);

        if (contentType != null && !contentType.isBlank()) {
            sv.setContentType(contentType);
        }

        if (downloadFileName != null && !downloadFileName.isBlank()) {
            String disp = openInline
                    ? "inline; filename=\"" + downloadFileName + "\""
                    : "attachment; filename=\"" + downloadFileName + "\"";
            sv.setContentDisposition(disp);
        }

        String sasToken = blob.generateSas(sv);
        return blob.getBlobUrl() + "?" + sasToken;
    }
}