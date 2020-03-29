package com.giovannisaberon.simpleegw;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class EGWJson {

    private Context context;

    public EGWJson(Context context){
        this.context = context;
    }

    public String loadJSONFromAsset(String filename) throws IOException {
        String json = "there is nothing";
        AssetManager am = context.getAssets();

        try {
            InputStream is = am.open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return "error";
        }
        Log.i("json bible: ", json);
        return json;
    }

    public HashMap<String, ArrayList<LinkedTreeMap<Object,Object>>> convertToHashmap() throws IOException {
        InputStream is = context.getAssets().open("egw.json");
        JsonReader reader = new JsonReader(new InputStreamReader(is));
        final Gson gson = new Gson();
        HashMap<String, ArrayList<LinkedTreeMap<Object,Object>>> map = gson.fromJson(reader, HashMap.class);
        return map;
    }
    public String JsonData(){


        String json = "{'Genesis': {'1': [{'1': 'In the beginning God'}, {'2': 'And there was light'}]}," +
                "       'Exodus': {'1': [{'1': 'Moses is born'}, {'2': 'Seven plagues'}]}" +
                "}";

        return json;
    }

    public JSONObject getJsonObject(String file) throws JSONException {
        JSONObject obj = new JSONObject(file);
        return obj;
    }

    public JSONObject getBook(JSONObject jsonBible, String book) throws JSONException {
        JSONObject jsonbook = jsonBible.getJSONObject(book);
        return jsonbook;
    }

    public JSONArray getChapter(JSONObject bible, String book, String chapter) throws JSONException {
        JSONObject jsonbook = this.getBook(bible, book);
        JSONArray jsonchapter = jsonbook.getJSONArray(chapter);
        return jsonchapter;
    }
}
