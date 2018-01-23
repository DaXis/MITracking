package com.mitracking.utils;

import android.content.Context;

import com.mitracking.Singleton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class TextFileMannager {
	
	private static String data="", p = "";
	
	public TextFileMannager(){
		
	}

    public static String loadFromResource(Context ctx,  int resource){
	     final InputStream rr = ctx.getResources().openRawResource(resource);
	     try {
			final BufferedReader e0 = new BufferedReader(new InputStreamReader(rr, "UTF-8"));
			try {
				while ((p = e0.readLine()) != null){ 
					data = data + p; 
                }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) { 
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     return data;
    }
    
    public String getData(){
    	return data.replace("null", "");
    }
    
    public static String loadFromFile(File file){
    	//StringBuilder text = new StringBuilder();
	    	try {
	    	    @SuppressWarnings("resource")
				BufferedReader br = new BufferedReader(new FileReader(file));
	    	    String line;
	    	    while ((line = br.readLine()) != null) {
	    	        /*text.append(line);
	    	        text.append('\n');*/
	    	    	data = data+line;
	    	    	//Log.v("MENU CACHE JSON ---->", data);
	    	    }
	    	}
	    	catch (IOException e) {
	    	    //You'll need to add proper error handling here
	    	}
	    	return data;
    }
	
    public static String get(InputStream is){
    	BufferedReader reader = new BufferedReader(new InputStreamReader(is));  
        StringBuilder sb = new StringBuilder();  

        String line = null;  
        try {  
            while ((line = reader.readLine()) != null) {  
                sb.append(line + "\n");  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                is.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }
        return  "{\"Results\":" + sb.substring(11, sb.length()-2) + "}\n" ;
    }

    public static void generateNoteOnSD(String sFileName, String sBody){
        try{
            File root = Singleton.getCacheCarpet();
            if (!root.exists()) {
                root.mkdirs();
            }
            File file = new File(root, sFileName);
            FileWriter writer = new FileWriter(file);
            writer.append(sBody);
            writer.flush();
            writer.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

}
