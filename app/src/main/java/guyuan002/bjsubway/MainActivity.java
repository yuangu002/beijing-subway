package guyuan002.bjsubway;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static HashMap<String, Station> graph = new HashMap<String, Station>();
    private Spinner fromSpinner;
    private Spinner toSpinner;
    private String s1 = "Station";
    private String s2 = "Station";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            DataInputStream dis1 = new DataInputStream(getAssets().open(String.format("stationinfo.txt")));
            DataInputStream dis2 = new DataInputStream(getAssets().open(String.format("lineinfo.txt")));
            Core.buildVertex(graph, dis1);
            Core.buildEdge(graph, dis2);
            Log.v(TAG, "building graph...");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!graph.isEmpty()) Log.v(TAG, "graph is successfully built");
        setSpinner();
        if (s1==null || s2 == null) Log.e(TAG, "There is an error");
    }

    private void setSpinner(){
        Log.v("set spinner", "all set");
        fromSpinner = findViewById(R.id.from);
        toSpinner = findViewById(R.id.to);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.fromTo, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(adapter);
        toSpinner.setAdapter(adapter);
        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                s1 = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                s2 = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public void MovetoSelection(View view){
        Log.v(TAG, "moving to selection activity");
        Log.v(TAG, "from: "+s1+" to: "+s2);

        Intent intent = new Intent(this, SelectionActivity.class);
        intent.putExtra("from", s1);
        intent.putExtra("to", s2);
        startActivity(intent);
    }
}
