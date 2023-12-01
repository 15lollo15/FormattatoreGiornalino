package org.example;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DocToPdfConverter {
    // The authentication key (API Key).
    // Get your own by registering at https://app.pdf.co
    //final static String API_KEY = "raccalorenzo11@gmail.com_71f9de3f01b8415d0b41b776ca6729b32311cfafa80bcc6f39594fe55fe5b0ee9e8d98dd";

    // Source DOC or DOCX file
//    final static Path SourceFile = Paths.get(".\\sample.doc");
//    // Destination PDF file name
//    final static Path DestinationFile = Paths.get(".\\result.pdf");

    public static final String UPLOAD_API_URL = "https://api.pdf.co/v1/file/upload/get-presigned-url?contenttype=application/octet-stream&name=%s";
    public static final String CONVERT_API_URL = "https://api.pdf.co/v1/pdf/convert/from/doc";

    private String apiKey;

    public DocToPdfConverter(String apiKey) {
        this.apiKey = apiKey;
    }

    public void convert(Path sourceFile, Path destinationFile) throws IOException {
        OkHttpClient webClient = new OkHttpClient();

        String query = String.format(
                UPLOAD_API_URL,
                sourceFile.getFileName());

        URL url = null;
        try
        {
            url = new URI(null, query, null).toURL();
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }

        // Prepare request
        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-api-key", apiKey) // (!) Set API Key
                .build();

        // Execute request
        Response response = webClient.newCall(request).execute();

        if (response.code() == 200)
        {
            // Parse JSON response
            JsonObject json = new JsonParser().parse(response.body().string()).getAsJsonObject();

            boolean error = json.get("error").getAsBoolean();
            if (!error)
            {
                // Get URL to use for the file upload
                String uploadUrl = json.get("presignedUrl").getAsString();
                // Get URL of uploaded file to use with later API calls
                String uploadedFileUrl = json.get("url").getAsString();

                // 2. UPLOAD THE FILE TO CLOUD.

                if (uploadFile(webClient, uploadUrl, sourceFile))
                {
                    // 3. CONVERT UPLOADED DOC (DOCX) FILE TO PDF

                    ConvertDocToPdf(webClient, destinationFile, uploadedFileUrl);
                }
            }
            else
            {
                // Display service reported error
                System.out.println(json.get("message").getAsString());
            }
        }
        else
        {
            // Display request error
            System.out.println(response.code() + " " + response.message());
        }
    }

    private void ConvertDocToPdf(OkHttpClient webClient, Path destinationFile, String uploadedFileUrl) throws IOException {
        // Make correctly escaped (encoded) URL
        URL url = null;
        try
        {
            url = new URI(null, CONVERT_API_URL, null).toURL();
        }
        catch (URISyntaxException | MalformedURLException e)
        {
            e.printStackTrace();
        }

        // Create JSON payload
        String jsonPayload = String.format("{\"name\": \"%s\", \"url\": \"%s\"}",
                destinationFile.getFileName(),
                uploadedFileUrl);

        // Prepare request body
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonPayload);

        // Prepare request
        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-api-key", apiKey) // (!) Set API Key
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        // Execute request
        Response response = webClient.newCall(request).execute();

        if (response.code() == 200)
        {
            // Parse JSON response
            JsonObject json = new JsonParser().parse(response.body().string()).getAsJsonObject();

            boolean error = json.get("error").getAsBoolean();
            if (!error)
            {
                // Get URL of generated PDF file
                String resultFileUrl = json.get("url").getAsString();

                // Download PDF file
                downloadFile(webClient, resultFileUrl, destinationFile.toFile());

                System.out.printf("Generated PDF file saved as \"%s\" file.", destinationFile.toString());
            }
            else
            {
                // Display service reported error
                System.out.println(json.get("message").getAsString());
            }
        }
        else
        {
            // Display request error
            System.out.println(response.code() + " " + response.message());
        }
    }

    private boolean uploadFile(OkHttpClient webClient, String url, Path sourceFile) throws IOException
    {
        // Prepare request body
        RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"), sourceFile.toFile());

        // Prepare request
        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-api-key", apiKey) // (!) Set API Key
                .addHeader("content-type", "application/octet-stream")
                .put(body)
                .build();

        // Execute request
        Response response = webClient.newCall(request).execute();

        return (response.code() == 200);
    }

    private void downloadFile(OkHttpClient webClient, String url, File destinationFile) throws IOException
    {
        // Prepare request
        Request request = new Request.Builder()
                .url(url)
                .build();
        // Execute request
        Response response = webClient.newCall(request).execute();

        byte[] fileBytes = response.body().bytes();

        // Save downloaded bytes to file
        OutputStream output = new FileOutputStream(destinationFile);
        output.write(fileBytes);
        output.flush();
        output.close();

        response.close();
    }
}
