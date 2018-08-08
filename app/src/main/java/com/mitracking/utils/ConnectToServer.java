package com.mitracking.utils;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.mitracking.MainActivity;
import com.mitracking.Singleton;
import com.mitracking.dialogs.UpdateDialog;
import com.mitracking.fragments.LoginFragment;
import com.mitracking.fragments.MainFragment;
import com.mitracking.service.AlarmReceiver;
import com.mitracking.service.SendService;

import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.KeyStore;

public class ConnectToServer {

    private static int statusCode;
    public static UpdateDialog updateDialog;
    /**
     * @param args
     * 0) url
     * 1) int identify provider class
     * 2) provider class
     * 3) JSONObject
     */

    public ConnectToServer(Object[] args){
        new ConnectAsync().executeOnExecutor(Singleton.getsExecutor(), args);
    }

    private class ConnectAsync extends AsyncTask<Object[], String, String> {

        private Object[] aux;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.gc();
        }

        @SuppressLint("LongLogTag")
        @Override
        protected String doInBackground(Object[]... params) {
            aux = params[0];
            String sUrl = (String)aux[0], result = "";

            sUrl = Singleton.getBaseUrl()+sUrl;

            JSONObject json = (JSONObject)aux[3];
            Log.d("CTS_URL --->", sUrl);

            HttpClient httpclient = getNewHttpClient();

            HttpPost httppost = new HttpPost(sUrl);

                try {
                    Log.d("json send", json.toString());
                    StringEntity se = new StringEntity(json.toString(), HTTP.UTF_8);

                    httppost.setEntity(se);

                    httppost.setHeader("Accept", "application/json");
                    httppost.setHeader("Content-type", "application/json");

                    // Execute HTTP Post Request
                    org.apache.http.HttpResponse response = httpclient.execute(httppost);
                    result = getResponse(response);

                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                    if(updateDialog != null)
                        updateDialog.dismiss();
                    Singleton.dissmissLoad();
                    Singleton.showCustomDialog(Singleton.getFragmentManager(),
                            "Atención", "Hubo un problema con tu conexión, intentalo de nuevo más tarde", "Continuar", 0);
                } catch (IOException e) {
                    e.printStackTrace();
                    if(updateDialog != null)
                        updateDialog.dismiss();
                    Singleton.dissmissLoad();
                    Singleton.showCustomDialog(Singleton.getFragmentManager(),
                            "Atención", "Hubo un problema con tu conexión, intentalo de nuevo más tarde", "Continuar", 0);
                }

            return result;
        }

        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC", progress[0]);
        }

        @SuppressLint("LongLogTag")
        @Override
        protected void onPostExecute(String result) {
            Log.d("CTS_onPostExecute "+aux[0], " "+result);

            decideMethod((int)aux[1], aux[2], result);

            System.gc();
        }
    }

    private String getResponse(org.apache.http.HttpResponse response){
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
            String line = null;

            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                if(updateDialog != null)
                    updateDialog.dismiss();
                Singleton.dissmissLoad();
                Singleton.showCustomDialog(Singleton.getFragmentManager(),
                        "Atención", "Hubo un problema con tu conexión, intentalo de nuevo más tarde", "Continuar", 0);
                return "Error";
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            if(updateDialog != null)
                updateDialog.dismiss();
            Singleton.dissmissLoad();
            Singleton.showCustomDialog(Singleton.getFragmentManager(),
                    "Atención", "Hubo un problema con tu conexión, intentalo de nuevo más tarde", "Continuar", 0);
        }
        catch (Exception e) {
            e.printStackTrace();
            if(updateDialog != null)
                updateDialog.dismiss();
            Singleton.dissmissLoad();
            Singleton.showCustomDialog(Singleton.getFragmentManager(),
                    "Atención", "Hubo un problema con tu conexión, intentalo de nuevo más tarde", "Continuar", 0);
        }

        return sb.toString();
    }

    private HttpParams setTimeOut(){
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 60000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 60000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        return httpParameters;
    }

    public HttpClient getNewHttpClient(){
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
            int timeoutConnection = 60000;
            HttpConnectionParams.setConnectionTimeout(params, timeoutConnection);
            int timeoutSocket = 60000;
            HttpConnectionParams.setSoTimeout(params, timeoutSocket);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception", e.getMessage());
            if(updateDialog != null)
                updateDialog.dismiss();
            Singleton.dissmissLoad();
            Singleton.showCustomDialog(Singleton.getFragmentManager(),
                    "Atención", "Hubo un problema con tu conexión, intentalo de nuevo más tarde", "Continuar", 0);
            return new DefaultHttpClient(setTimeOut());
        }
    }

    private void decideMethod(int i, Object o, String result) {
        switch(i){
            case 0:

                break;
            case 1:
                ((LoginFragment) o).getResponse(result);
                break;
            case 2:
                ((MainFragment) o).getResponse(result);
                break;
            case 3:
                ((SendService) o).getResponse(result);
                break;
            case 4:
                ((AlarmReceiver) o).getResponse(result);
                break;
            case 5:
                if(o.getClass() == SendService.class)
                    ((SendService) o).getErrorResponse(result);
                else if(o.getClass() == MainActivity.class)
                    ((MainActivity) o).getTrackResponse(result);
                break;
        }
    }

}