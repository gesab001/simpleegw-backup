package com.giovannisaberon.simpleegw;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class ReadingActivity extends AppCompatActivity implements ReadingAdapter.VerseAdapterListener {

    private RecyclerView recyclerView;
    private ReadingAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<String> selectedbooks = new ArrayList<String>(){};
    ArrayList<String> reorderedList = new ArrayList<String>(){};
    JSONObject jsonWritings = new JSONObject();
    HashMap<String, EGWData> selectedparagraphs = new HashMap<String, EGWData>();
    HashMap<String, ArrayList<LinkedTreeMap<Object,Object>>> egwmap = new HashMap<String, ArrayList<LinkedTreeMap<Object,Object>>>();
    ItemTouchHelper touchHelper;
    JSONObject jsontotalparagraphs = new JSONObject();
    JSONObject jsonstartingdate = new JSONObject();
    String json_data = null;
    EGWJson egwJson;
    EGWData egwData;
    JSONObject jsonObject = new JSONObject();
    String[] dataset;
    SharedPreferences pref;
    private SharedPreferences.Editor editor;


    @Override
    public void onVerseSelected(ArrayList<String> selectedbooks, HashMap<String, EGWData> selectedparagraphs, EGWData selecteditem) {
    }

    public void fullScreen(ArrayList<String> selectedbooks, HashMap<String, EGWData> selectedparagraphs, EGWData selecteditem){
        pref = this.getApplicationContext().getSharedPreferences("FullScreenPrefs", 0);
        editor = pref.edit();

        String bookcode = selecteditem.getBookcode();
        String paragraph = selecteditem.toString();
        editor.putString("currentitem", bookcode);
        editor.putString(bookcode, paragraph);
        editor.commit();

        for (EGWData egwData : selectedparagraphs.values()){
            bookcode = egwData.getBookcode();
            paragraph = egwData.toString();
            editor.putString(bookcode, paragraph);
            editor.commit();
        }
        Intent intent = new Intent(this, FullscreenActivity.class);
        intent.putExtra("selectedbooks", selectedbooks);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);
        final Intent readingIntent = new Intent(this, SettingsActivity.class);
        Button settings_button = (Button) findViewById(R.id.settings_button);
        settings_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                startActivity(readingIntent);
            }
        });

        Button refresh_button = (Button) findViewById(R.id.refresh_button);
        refresh_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                loadRecyclerView();
            }
        });

        Resources res = getResources();
        dataset = res.getStringArray(R.array.book_arrays);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);

        if(pref.contains("reorderedList")){
            String list = pref.getString("reorderedList", null);
            String[] array = list.split("@");
            for (int i=0; i<array.length; i++){
                reorderedList.add(array[i]);// 0 - for private mode
            }
            Log.i("reordered list", reorderedList.toString());

        }

        egwJson = new EGWJson(this);
        try {
            json_data = egwJson.loadJSONFromAsset("bookreferences.json");
            jsonObject = egwJson.getJsonObject(json_data);
            json_data = egwJson.loadJSONFromAsset("totalparagraphs.json");
            jsontotalparagraphs = egwJson.getJsonObject(json_data);
            json_data = egwJson.loadJSONFromAsset("startingdate.json");
            jsonstartingdate = egwJson.getJsonObject(json_data);
//            json_data = egwJson.loadJSONFromAsset("egw.json");
            egwmap = egwJson.convertToHashmap();

//            jsonWritings = egwJson.getJsonObject(json_data);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        }catch (JSONException e) {
//            e.printStackTrace();
//        }
        for(int i=0; i<dataset.length; i++){
            String book = dataset[i];
            Boolean choice = pref.getBoolean(book, false);
//
            if(choice){
                try {
//
                    Log.i("adding", book);
                    selectedbooks.add(book);

                    ArrayList<LinkedTreeMap<Object,Object>> getbook = egwmap.get(book);
                    int totalparagraphs = jsontotalparagraphs.getInt(book);
                    String startingdate = jsonstartingdate.getString(book);
                    int id = setCurrentID(startingdate, totalparagraphs);
                    Log.i("current id", Integer.toString(id));
                    LinkedTreeMap<Object,Object> getparagraph = (LinkedTreeMap) getbook.get(id);

                    String bookcode = book.toString();


                    String title = jsonObject.getString(book);
                    double page = (Double) getparagraph.get("page");
                    double paragraph = (Double) getparagraph.get("paragraph");
                    int pageint = (int) page;
                    int paragraphint = (int) paragraph;
                    String word = getparagraph.get("word").toString();
                    egwData = new EGWData(id, bookcode, title, pageint, paragraphint, word);
                    Log.i("egwData", egwData.toString());
                    selectedparagraphs.put(bookcode, egwData);

                    Log.i("word", word);
                    Log.i("page", getparagraph.get("page").toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }
        loadRecyclerView();


    }


    private int setCurrentID(String datestring, int totalParagraphs){
        Long currentID;
        Date date = new Date();
        long time = date.getTime();
        Log.i("datestring", datestring);
        java.sql.Timestamp startDate = java.sql.Timestamp.valueOf(datestring + " 00:00:00");
        java.sql.Timestamp current = new java.sql.Timestamp(date.getTime());
        long then = startDate.getTime();
        long now = current.getTime();
        long days = TimeUnit.MILLISECONDS.toDays(now - then);
        currentID = days;
        while (currentID > totalParagraphs){
            currentID = currentID - totalParagraphs;
        }
        return Integer.parseInt(Long.toString(currentID))-1;
    }


    private void loadRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
//
//        // use this setting to improve performance if you know that changes
//        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(false);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ReadingAdapter.VerseAdapterListener listener = new ReadingAdapter.VerseAdapterListener() {
            @Override
            public void onVerseSelected(ArrayList<String> selectedbooks, HashMap<String, EGWData> selectedparagraphs, EGWData selecteditem) {
                fullScreen(selectedbooks, selectedparagraphs, selecteditem);
            }
        };

        if (reorderedList.isEmpty()){
            Log.i("selectedparagraphs", Integer.toString(selectedparagraphs.size()));
            mAdapter = new ReadingAdapter(selectedbooks, listener, selectedparagraphs, this);

        }
        else{
            ArrayList<String> removeitems = new ArrayList<String>(){};
            for (String book : reorderedList) {
                if (selectedbooks.contains(book)) {
                    Log.i("already in the list", book);

                } else {
                    removeitems.add(book);
                    Log.i("remove book", book);

                }
            }

            reorderedList.removeAll(removeitems);
            ArrayList<String> additems = new ArrayList<String>(){};

            for (String book: selectedbooks){
                if (reorderedList.contains(book)){

                }else{
                    additems.add(book);
                }
            }
            reorderedList.addAll(additems);

            Log.i("selectedparagraphs", Integer.toString(selectedparagraphs.size()));
            Log.i("reorderedlist final", Integer.toString(reorderedList.size()));
            mAdapter = new ReadingAdapter(reorderedList, listener, selectedparagraphs, this);

        }
        ItemTouchHelper.Callback callback =
                new ItemMoveCallback(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(mAdapter);
    }
}
