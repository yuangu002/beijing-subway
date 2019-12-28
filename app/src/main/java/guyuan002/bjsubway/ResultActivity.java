package guyuan002.bjsubway;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ResultActivity extends AppCompatActivity {

    private static final String TAG = "ResultActivity";
    private String source;
    private String destination;
    private TextView print;
    private TextView fromWhere;
    private TextView toWhere;
    private double time;
    private int site;
    private String priority;
    private StringBuilder pathtxt = new StringBuilder();
    private StringBuilder sitetxt = new StringBuilder();
    private RadioGroup mode;
    private RadioButton radioButton;
    private int n;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Bundle info = getIntent().getExtras();
        source = info.getString("source");
        destination = info.getString("destination");
        priority = info.getString("priority");
        switch (priority){
            case "Minimal time":
                time = Core.dijkstra(MainActivity.graph, source, destination, 2.5);
                break;
            case "Least transfer stations":
                Core.dijkstraforLeastTransfers(MainActivity.graph, source, destination);
                break;
            case "Most sites":
                site = Core.dijkstraforMostSites(MainActivity.graph, source, destination);
                break;
        }
        setPathtxt(pathtxt);
        sitetxt.append("Along the path, you can also visit: \n");
        setSitetxt(sitetxt, MainActivity.graph, destination);
        if (n==0) sitetxt.append("No interesting site along the path, but I hope you have a wonderful day in Beijing!");
        mode = findViewById(R.id.pathorsite);
        print = findViewById(R.id.info);
        print.setMovementMethod(new ScrollingMovementMethod());
        fromWhere = findViewById(R.id.frommsg);
        toWhere = findViewById(R.id.tomsg);
        fromWhere.setText("From: " + source);
        toWhere.setText("To: " + destination);
        print.setText(pathtxt);
    }

    public void onModeClicked(View view){
        int buttonID = mode.getCheckedRadioButtonId();
        radioButton = findViewById(buttonID);
        switch (radioButton.getText().toString()){
            case "Recommended Path":
                print.setText(pathtxt);
                break;
            case "Must See along the way":
                print.setText(sitetxt);
                break;
        }
    }

    private void setPathtxt(StringBuilder sb){
        ArrayList<Station> transferinfo = Core.printPath(MainActivity.graph, destination);
        if (priority.equals("Minimal time")) sb.append("Minimal time:  "+time + " minutes expected.\n");
        if (priority.equals("Least transfer stations")) sb.append("Least number of transfer stations along the way: " + transferinfo.size() + "\n");
        if (priority.equals("Most sites")) sb.append("Most number of sites along the way: "+ site +"\n");
        // there are switches of lines
        if (!transferinfo.isEmpty()) {
            for (int i = transferinfo.size()-1; i >= 0 ; i--) {
                Station transfer = transferinfo.get(i);
                // we want airport express printed in the screen
                if (transfer.from==0 || transfer.to==0){
                    if (transfer.from == 0) sb.append("Take Airport Express to " + transfer.name + ", transfer to LINE " + transfer.to+"\n");
                    else if (transfer.to == 0) sb.append("Take LINE " + transfer.from + " to " + transfer.name + ", transfer to Airport Express\n");
                }
                else if (transfer.from != 0 && transfer.to != 0) sb.append("Take LINE " + transfer.from + " to " + transfer.name + ", transfer to LINE " + transfer.to+"\n");
            }
        }else {
            int theLine = Core.match(MainActivity.graph, destination, MainActivity.graph.get(destination).path);
            if (theLine == 0) sb.append("No transfer: Take Airport Express" +  "\n");
            else sb.append("No transfer: Take LINE " + theLine +  "\n");
        }
        sb.append("and all the way down to...");
    }

    private void setSitetxt(StringBuilder sb, HashMap<String, Station> g, String t){
        if (g.get(t).path != null) setSitetxt(sb, g, g.get(t).path);
        if (g.get(t).isSite && !g.get(t).siteName.equals("Beijing Capital International Airport")) {
            sb.append(g.get(t).siteName + " at " + g.get(t).name + " station\n");
            n++;
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        resetGraph();
    }

    public void backtoMain(View view){
        Log.v(TAG, "back to main");
        resetGraph();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void resetGraph(){
        MainActivity.graph.clear();
        try {
            DataInputStream dis1 = new DataInputStream(getAssets().open(String.format("stationinfo.txt")));
            DataInputStream dis2 = new DataInputStream(getAssets().open(String.format("lineinfo.txt")));
            Core.buildVertex(MainActivity.graph, dis1);
            Core.buildEdge(MainActivity.graph, dis2);
            Log.v(TAG, "rebuilding graph...");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
