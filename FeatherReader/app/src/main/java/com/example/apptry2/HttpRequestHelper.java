package com.example.apptry2;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.util.Log;

public class HttpRequestHelper {

    private HttpURLConnection httpConn;
    private DataOutputStream request;
    private final String boundary =  "*****";

    public HttpRequestHelper(String requestURL)
    {
        try {
            URL url = new URL(requestURL);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setUseCaches(false);
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setConnectTimeout(15000); // timeout in miliseconds?

            httpConn.setRequestMethod("POST");
            httpConn.setRequestProperty("Connection", "Keep-Alive");
            httpConn.setRequestProperty("Cache-Control", "no-cache");
            httpConn.setRequestProperty(
                    "Content-Type", "multipart/form-data;boundary=" + this.boundary);

            request = new DataOutputStream(httpConn.getOutputStream());
        }catch (Exception e){
            Log.e("Exception",e.toString());
        }
    }

    public void addFilePart(Bitmap inImage)
    {
        try {
            ByteArrayOutputStream bytes1 = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes1);
            byte[] bytes = bytes1.toByteArray();
            request.write(bytes);
        }
        catch (Exception e){
            Log.e(getClass()+"Exception",e.toString());
        }
    }

    public String finish() {
        String response = "error";
        try {
            request.flush();
            request.close();


            int status = httpConn.getResponseCode();

            if (status == HttpURLConnection.HTTP_OK) {
                InputStream responseStream = new
                        BufferedInputStream(httpConn.getInputStream());
                BufferedReader responseStreamReader =
                        new BufferedReader(new InputStreamReader(responseStream));

                String line = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = responseStreamReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                responseStreamReader.close();
                response = stringBuilder.toString().trim();
                httpConn.disconnect();
            } else {
                throw new IOException("Server returned non-OK status: " + status);
            }
        }
        catch(Exception e){
            Log.e(getClass()+"Exception",e.toString());
        }
        return response;
    }


}
