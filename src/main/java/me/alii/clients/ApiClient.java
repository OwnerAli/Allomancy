package me.alii.clients;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ApiClient {
    public enum MapFile {
        DATA,
        META
    }

    private static final String BASE_URL = "http://localhost:8080";
    private static final String API_KEY = "THISSTRINGisNOTsuspiciousATALLwhatSOEVERdontevenlookatitforMorethanTwoSECONDSThiSisLAZYEasyApproach";
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public CompletableFuture<Void> uploadMap(String mapId, Path dataFile, Path metaFile) {
        String boundary = "----MapServiceBoundary" + System.currentTimeMillis();

        byte[] body = buildMultipartBody(boundary, mapId, dataFile, metaFile);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/maps"))
                .header("X-Api-Key", API_KEY)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        // notify player: success
                    } else if (response.statusCode() == 401) {
                        // notify player: unauthorized
                    } else if (response.statusCode() == 429) {
                        // notify player: rate limited
                    } else {
                        // notify player: something went wrong
                    }
                });
    }

    public CompletableFuture<Map<MapFile, byte[]>> downloadMap(String mapId) {
        CompletableFuture<byte[]> dataFuture = downloadMapData(mapId);
        CompletableFuture<byte[]> metaFuture = downloadMapMeta(mapId);

        return CompletableFuture.allOf(dataFuture, metaFuture)
                .thenApply(_ -> Map.of(
                        MapFile.DATA, dataFuture.join(),
                        MapFile.META, metaFuture.join()
                ));
    }

    public CompletableFuture<byte[]> downloadMapData(String mapId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/maps/" + mapId + "/data"))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray())
                .thenApply(response -> {
                    if (response.statusCode() == 200) return response.body();
                    if (response.statusCode() == 404) throw new RuntimeException("Map not found: " + mapId);
                    if (response.statusCode() == 429) throw new RuntimeException("Rate limited");
                    throw new RuntimeException("Download failed: " + response.statusCode());
                });
    }

    public CompletableFuture<byte[]> downloadMapMeta(String mapId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/maps/" + mapId + "/metadata"))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray())
                .thenApply(response -> {
                    if (response.statusCode() == 200) return response.body();
                    if (response.statusCode() == 404) throw new RuntimeException("Map not found: " + mapId);
                    if (response.statusCode() == 429) throw new RuntimeException("Rate limited");
                    throw new RuntimeException("Metadata download failed: " + response.statusCode());
                });
    }

    private byte[] buildMultipartBody(String boundary, String mapId, Path dataFile, Path metadataFile) {
        try {
            var output = new java.io.ByteArrayOutputStream();
            String nl = "\r\n";
            String dashes = "--" + boundary;

            // mapId field
            output.write((dashes + nl).getBytes());
            output.write(("Content-Disposition: form-data; name=\"mapId\"" + nl + nl).getBytes());
            output.write((mapId + nl).getBytes());

            // mapData file
            output.write((dashes + nl).getBytes());
            output.write(("Content-Disposition: form-data; name=\"mapData\"; filename=\"data.json\"" + nl).getBytes());
            output.write(("Content-Type: application/json" + nl + nl).getBytes());
            output.write(Files.readAllBytes(dataFile));
            output.write(nl.getBytes());

            // metadata file
            output.write((dashes + nl).getBytes());
            output.write(("Content-Disposition: form-data; name=\"metadata\"; filename=\"metadata.json\"" + nl).getBytes());
            output.write(("Content-Type: application/json" + nl + nl).getBytes());
            output.write(Files.readAllBytes(metadataFile));
            output.write(nl.getBytes());

            // closing boundary
            output.write((dashes + "--" + nl).getBytes());

            return output.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to build multipart body", e);
        }
    }
}