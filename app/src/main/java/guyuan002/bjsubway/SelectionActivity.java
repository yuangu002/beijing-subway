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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class SelectionActivity extends AppCompatActivity {
    private static final String TAG = "SelectionActivity";
    private TextView msg;
    private String s1;
    private String s2;
    private String startStation = new String();
    private String terminalStation = new String();
    private Spinner fromLine, toLine, source, destination;
    private String selectedLine;
    private Button okButton;
    private TextView fromlinetxt;
    private TextView tolinetxt;
    private TextView smsg;
    private TextView tmsg;
    private RadioGroup priorityGroup;
    private RadioButton radioButton;
    private String priority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        Bundle info = getIntent().getExtras();
        s1 = info.getString("from");
        s2 = info.getString("to");
        if (s1 == null || s2 == null) Log.e("Error", "there is an error");
        msg = findViewById(R.id.msg);
        fromLine = findViewById(R.id.fromLine);
        toLine = findViewById(R.id.toLine);
        source = findViewById(R.id.source);
        destination = findViewById(R.id.destination);
        fromlinetxt = findViewById(R.id.fromLinetxt);
        tolinetxt = findViewById(R.id.toLinetxt);
        smsg = findViewById(R.id.fromTXT);
        tmsg = findViewById(R.id.toTXT);
        priorityGroup = findViewById(R.id.choosePriority);
        msg.setText("From " + s1 + " to " + s2 + ": ");
        okButton = findViewById(R.id.ok);
        if (s1.equals("Station")){
            if (s2.equals("Station")){
                Log.v(TAG, "from station to station");
                fromLine.setVisibility(View.VISIBLE);
                toLine.setVisibility(View.VISIBLE);
                source.setVisibility(View.VISIBLE);
                destination.setVisibility(View.VISIBLE);
                fromlinetxt.setText("Choose Line");
                tolinetxt.setText("Choose Line");
                smsg.setText("Choose Station");
                tmsg.setText("Choose Station");
                setLineSpinner(fromLine);
                setLineSpinner(toLine);
            }else{
                Log.v(TAG, "from station to site");
                fromLine.setVisibility(View.VISIBLE);
                source.setVisibility(View.VISIBLE);
                destination.setVisibility(View.VISIBLE);
                fromlinetxt.setText("Choose Line");
                smsg.setText("Choose Station");
                tmsg.setText("Choose Site");
                setLineSpinner(fromLine);
                setSiteSpinner(destination);
            }
        }else{
            if (s2.equals("Station")){
                Log.v(TAG, "from site to station");
                toLine.setVisibility(View.VISIBLE);
                source.setVisibility(View.VISIBLE);
                destination.setVisibility(View.VISIBLE);
                tolinetxt.setText("Choose Line");
                smsg.setText("Choose Site");
                tmsg.setText("Choose Station");
                setSiteSpinner(source);
                setLineSpinner(toLine);
            }else{
                Log.v(TAG, "from site to site");
                source.setVisibility(View.VISIBLE);
                destination.setVisibility(View.VISIBLE);
                smsg.setText("Choose Site");
                tmsg.setText("Choose Site");
                setSiteSpinner(source);
                setSiteSpinner(destination);
            }
        }
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (startStation.isEmpty() || terminalStation.isEmpty()) Toast.makeText(getApplicationContext(), "Source and terminal not set up: Please select!", Toast.LENGTH_LONG).show();
                else{
                    if (startStation.equals(terminalStation)) Toast.makeText(getApplicationContext(), "Source and destination are the same!", Toast.LENGTH_LONG).show();
                    else if (priority == null) Toast.makeText(getApplicationContext(), "You must select your priority!", Toast.LENGTH_LONG).show();
                    else movetoResultActivity();
                }
            }
        });
    }

    public void onPriorityClicked(View view){
        int buttonID = priorityGroup.getCheckedRadioButtonId();
        radioButton = findViewById(buttonID);
        priority = radioButton.getText().toString();
    }

    private void movetoResultActivity(){
        Log.v(TAG, "Moving to result activity");
        Log.v(TAG, "from: "+startStation+" to: "+terminalStation);
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("source", startStation);
        intent.putExtra("destination", terminalStation);
        intent.putExtra("priority", priority);
        startActivity(intent);
    }

    private void setStationSpinner(Spinner spinner){
        ArrayAdapter<CharSequence> adapter;
        Log.v(TAG, "selected line is: " + selectedLine);
        switch (selectedLine){
            case "Airport Express":
                adapter = ArrayAdapter.createFromResource(this, R.array.line0, android.R.layout.simple_spinner_item);
                break;
            case "1":
                adapter = ArrayAdapter.createFromResource(this, R.array.line1, android.R.layout.simple_spinner_item);
                break;
            case "2":
                adapter = ArrayAdapter.createFromResource(this, R.array.line2, android.R.layout.simple_spinner_item);
                break;
            case "4":
                adapter = ArrayAdapter.createFromResource(this, R.array.line4, android.R.layout.simple_spinner_item);
                break;
            case "5":
                adapter = ArrayAdapter.createFromResource(this, R.array.line5, android.R.layout.simple_spinner_item);
                break;
            case "6":
                adapter = ArrayAdapter.createFromResource(this, R.array.line6, android.R.layout.simple_spinner_item);
                break;
            case "7":
                adapter = ArrayAdapter.createFromResource(this, R.array.line7, android.R.layout.simple_spinner_item);
                break;
            case "8":
                adapter = ArrayAdapter.createFromResource(this, R.array.line8, android.R.layout.simple_spinner_item);
                break;
            case "9":
                adapter = ArrayAdapter.createFromResource(this, R.array.line9, android.R.layout.simple_spinner_item);
                break;
            case "10":
                adapter = ArrayAdapter.createFromResource(this, R.array.line10, android.R.layout.simple_spinner_item);
                break;
            case "13":
                adapter = ArrayAdapter.createFromResource(this, R.array.line13, android.R.layout.simple_spinner_item);
                break;
            case "14":
                adapter = ArrayAdapter.createFromResource(this, R.array.line14, android.R.layout.simple_spinner_item);
                break;
            case "15":
                adapter = ArrayAdapter.createFromResource(this, R.array.line15, android.R.layout.simple_spinner_item);
                break;
            case "16":
                adapter = ArrayAdapter.createFromResource(this, R.array.line16, android.R.layout.simple_spinner_item);
                break;
            default:
                adapter = null;
                break;
        }
        if (adapter == null) Log.e("Attention: ", "adapter not initialized");
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                switch (parent.getId()){
                    case R.id.source:
                        startStation = selected;
                        Log.v(TAG, "source: " + startStation);
                        break;
                    case R.id.destination:
                        terminalStation = selected;
                        Log.v(TAG, "destination: "+terminalStation);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setSiteSpinner(Spinner spinner){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.site, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String siteName = parent.getItemAtPosition(position).toString();
                if (siteName!=null) {
                    switch (parent.getId()){
                        case R.id.source:
                            startStation = getStationNamefromSite(siteName);
                            break;
                        case R.id.destination:
                            terminalStation = getStationNamefromSite(siteName);
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private String getStationNamefromSite(String siteName){
        String id = new String();
        Collection c = MainActivity.graph.values();
        Iterator iterator = c.iterator();
        while (iterator.hasNext()){
            Station station = (Station)iterator.next();
            if (station.isSite){
                // multiple sites in a single station
                if (station.siteName.contains(",")){
                    String[] single = station.siteName.split(", ");
                    for (int i = 0; i < single.length; i++){
                        if (siteName.equals(single[i])) id = station.name;
                    }
                }else{
                    if (siteName.equals(station.siteName)) id = station.name;
                }
            }
        }
        if (id.isEmpty()) Log.e("Error: ", "site name not found!");
        return id;
    }

    private void setLineSpinner(Spinner spinner){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.line, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLine = parent.getItemAtPosition(position).toString();
                switch (parent.getId()){
                    case R.id.fromLine:
                        Log.v(TAG, selectedLine + " selected for from line");
                        setStationSpinner(source);
                        break;
                    case R.id.toLine:
                        Log.v(TAG, selectedLine + " selected for to line");
                        setStationSpinner(destination);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
