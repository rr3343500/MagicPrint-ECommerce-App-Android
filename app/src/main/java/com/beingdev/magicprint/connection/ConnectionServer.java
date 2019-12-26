package com.beingdev.magicprint.connection;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by iwish on 11/30/2016.
 */
public class ConnectionServer extends AsyncTask<String, String, String> {

    ProgressDialog pdLoading = null;
    HttpURLConnection conn;
    String urlString = null;
    int READ_TIMEOUT = 15000;
    int CONNECTION_TIMEOUT = 10000;
    String REQUESTED_METHOD = "POST";
    Uri.Builder builder = null;
    String rtn = "FALSE";


    public AsyncResponse delegate = null;




    String crlf = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    private String selectedFilePath = null;
    private DataOutputStream request;

    private  boolean isFileUpload = true;

    private HashMap<String, String> fileBuildparameter = new HashMap<>();
    private HashMap<String, String> stringBuildparameter = new HashMap<>();


    public ConnectionServer(){


    }

    public void execute(AsyncResponse delegate)
    {
        this.execute("");

        this.delegate = delegate;
    }


    @Override
    public String doInBackground(String... string) {
        try{
            URL url = new URL(urlString);
            this.conn = (HttpURLConnection) url.openConnection();
            this.conn.setReadTimeout(this.READ_TIMEOUT);
            this.conn.setConnectTimeout(this.CONNECTION_TIMEOUT);
            this.conn.setRequestMethod(this.REQUESTED_METHOD);




            this.conn.setUseCaches(false);
            this.conn.setDoOutput(true);

            this.conn.setRequestProperty("Connection", "Keep-Alive");
            this.conn.setRequestProperty("Cache-Control", "no-cache");
            this.conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            this.conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);


            request = new DataOutputStream(this.conn.getOutputStream());
            Log.e("request",this.conn.getOutputStream().toString());






           if(fileBuildparameter.size()>0) {
               for(Map.Entry<String, String> entry :fileBuildparameter.entrySet())
               {
                   FileInputStream fileInputStream = new FileInputStream(entry.getValue());

                   int bytesRead, bytesAvailable, bufferSize;
                   byte[] buffer;
                   int maxBufferSize = 1 * 1024 * 1024;
                   File selectedFile = new File(entry.getValue());


                   String[] parts = selectedFilePath.split("/");
                   final String fileName = parts[parts.length - 1];


                   request.writeBytes(twoHyphens + boundary + crlf);
                   request.writeBytes("Content-Disposition: form-data; name=\"" + entry.getKey() + "\";filename=\""
                           + entry.getValue() + "\"" + crlf);
                   request.writeBytes("Content-Type: " + MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(entry.getValue())) + crlf);
                   request.writeBytes(crlf);
                   //returns no. of bytes present in fileInputStream
                   bytesAvailable = fileInputStream.available();
                   //selecting the buffer size as minimum of available bytes or 1 MB
                   bufferSize = Math.min(bytesAvailable,maxBufferSize);
                   //setting the buffer as byte array of size of bufferSize
                   buffer = new byte[bufferSize];

                   //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                   bytesRead = fileInputStream.read(buffer,0,bufferSize);

                   //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                   while (bytesRead > 0){
                       //write the bytes read from inputstream
                       request.write(buffer,0,bufferSize);
                       bytesAvailable = fileInputStream.available();
                       bufferSize = Math.min(bytesAvailable,maxBufferSize);
                       bytesRead = fileInputStream.read(buffer,0,bufferSize);
                   }

                   request.writeBytes(crlf);
               }
           }

            if(stringBuildparameter.size()>0) {
                for(Map.Entry<String, String> entity :stringBuildparameter.entrySet()) {
                    request.writeBytes(twoHyphens + boundary + crlf);
                    request.writeBytes("Content-Disposition: form-data; name=\"" + entity.getKey() + "\"" + crlf);
                    request.writeBytes(crlf);
                    request.writeBytes(entity.getValue());
                    request.writeBytes(crlf);
                }
            }








        request.writeBytes(twoHyphens + boundary + twoHyphens);
            request.flush();
            request.close();

            InputStream responseStream;
            Log.e("Response Code", String.valueOf(this.conn.getResponseCode()));

            if(this.conn.getResponseCode()== HttpURLConnection.HTTP_OK)
            responseStream = new BufferedInputStream(this.conn.getInputStream());
            else
            responseStream = new BufferedInputStream(this.conn.getErrorStream());


            BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));

            Log.e("Response Code", String.valueOf(this.conn.getResponseCode()) );
            String line = "";
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            responseStreamReader.close();
            String response = stringBuilder.toString();
            return response;





           } catch (IOException e)
        {
            e.printStackTrace();

        }finally {
            this.conn.disconnect();
        }
        return rtn;
    }
    @Override
    public void onPreExecute() {
        super.onPreExecute();
        if(this.pdLoading!=null) {
            //this method will be running on UI thread
            this.pdLoading.setMessage("\tLoading...");
            this.pdLoading.setCancelable(false);
            this.pdLoading.show();
        }

    }





    @Override
    public void onPostExecute(String result){
        super.onPostExecute(result);
        if(this.pdLoading!=null) {
            this.pdLoading.dismiss();
        }
        delegate.processFinish(result);
      //  Log.e("data",result);

    }
    public void setFilepath(String key, String filePath)
    {

          this.selectedFilePath = filePath;

        fileBuildparameter.put(key,filePath);



    }

    public void set_current_activity(Context context)
    {

        this.pdLoading = new ProgressDialog(context);
    }

    public void set_url(String url)
    {

                 this.urlString = url;




    }
    public void readTimeout(int timeout){

        this.READ_TIMEOUT = timeout;
    }

    public void connectionTimeout(int timeout){

        this.CONNECTION_TIMEOUT = timeout;
    }

    public void requestedMethod(String method)
    {
        this.REQUESTED_METHOD = method;
    }

    public void buildParameter(String key, String parameter)
    {

        stringBuildparameter.put(key,parameter);

    }



    public interface AsyncResponse {
        void processFinish(String output);
    }

    public void setRequestProperty(String Key, String Value)
    {
        this.conn.setRequestProperty(Key,Value);
    }








}
