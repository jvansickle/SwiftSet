package com.michaeloles.swiftset;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class ExerciseSelector extends AppCompatActivity {

    SearchView searchView;
    ArrayAdapter adapter;
    ExerciseDb remaining;
    int lastRandom = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_selector);
        setTitle("Choose An Exercise");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        remaining = MainActivity.getRemainingDb();
        if(remaining==null) {
            startActivity(new Intent(this, MainActivity.class));
        }else {
            ArrayList<String> colList = remaining.getColumnsList();
            Collections.sort(colList);
            String[] searchResults = colList.toArray(new String[colList.size()]);
            initList(searchResults);
            searchView = (SearchView) findViewById(R.id.exSearch);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    adapter.getFilter().filter(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }
    }

    private void initList(final String[] searchResults) {
        //Creates a list with each exercise and stores the exercise name and url in the intent
        String[] exerciseNames = new String[searchResults.length];//Convert searchResults which contains IDs to names
        HashMap<String,String> namesMap = ExerciseDb.getNames();
        final HashMap<String,String> idsMap = ExerciseDb.getIds();
        for(int i=0; i<searchResults.length; i++) {
            exerciseNames[i] = namesMap.get(searchResults[i]);
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, exerciseNames);
        final ListView exListView = (ListView) findViewById(R.id.exerciseList);
        exListView.setAdapter(adapter);
        exListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String selectedFromList = (String) (exListView.getItemAtPosition(position));
                Intent intent = new Intent(view.getContext(), ExerciseViewer.class);
                intent.putExtra("selected_exercise", idsMap.get(selectedFromList));
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
    }

    public void chooseRandom(View view){
        ExerciseDb remaining = MainActivity.getRemainingDb();
        ArrayList<String> colList = remaining.getColumnsList();
        final HashMap<String,String> urls = remaining.getUrls();

        Random r = new Random();
        int rand;
        //Avoids choosing the same exercise twice in a row
        while(true) {
            rand = r.nextInt(colList.size());
            if(rand!=lastRandom || colList.size()<2) {
                break;
            }
        }

        String selectedFromList = colList.get(rand);
        String selectedUrl = urls.get(selectedFromList);
        Intent intent = new Intent(view.getContext(), ExerciseViewer.class);
        intent.putExtra("selected_exercise", selectedFromList);
        intent.putExtra("selected_url", selectedUrl);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }
}