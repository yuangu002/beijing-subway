package guyuan002.bjsubway;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/*
 * Class Responsibilities:
 * This class will provide all core functions, including graph-building and dijkstra algorithm
 * RULES of graph-building:
 * 1. The input data only contains subway lines in Beijing downtown and airport express. Thus lines
 * in Beijing rural/suburban areas are not included, like Changping, Yizhuang and Daxing lines.
 * 2. I didn't consider the time for waiting the next subway coming. It depends on when you arrive the station
 * 3. Usually it takes 3 minutes to commute from one station to another. But a few exceptions: edges
 * incident to a terminal station will have 4-minute capacity and a transfer station with 3.5 minutes
 * due to high volume of passengers. TODO: add accurate distance between each two stations.
 * 4. If the shortest path switches the line, there will be 2.5 more minutes for additional walk inside the
 * transfer station. In Beijing, some transfer stations (usually transportation hub) require huge amount of
 * additional time ranging from 5 to 10 minutes, while others may only take less than 2 minutes. So I estimate
 * that the average may be 2.5 minutes around since I didn't have access to accurate data.
 * TODO: collect accurate data for transfer lines' additional time
 * 5. Some stations, whose construction is complete, are temporarily closed (subway passes them without
 * stopping). I still include these stations in the graph due to convenience of calculating time,
 * but in the GUI, they are not the option for source and destination.
 * */
public class Core {

    public static boolean debug = false;

    public static void buildVertex(HashMap<String, Station> graph, DataInputStream dis) throws FileNotFoundException {
        Scanner reader = new Scanner(dis);
        while (reader.hasNext()) {
            Station station = new Station();
            String str = reader.next()	 + " (" + reader.next() + ")";
            station.name = str.replaceAll("_", " ");
            int numTrans = reader.nextInt();
            if (numTrans > 0) station.isTransfer = true;
            for (int i = 0; i < numTrans+1; i++) {
                station.lines[i] = reader.nextInt();
            }
            if (reader.nextInt() == 1) {
                station.isSite = true;
                station.siteName = reader.next().replaceAll("_", " ");
            }
            graph.put(station.name, station);
        }
        reader.close();
    }

    public static void buildEdge(HashMap<String, Station> graph, DataInputStream dis) throws FileNotFoundException{
        Scanner reader = new Scanner(dis);
        ArrayList<String> line = new ArrayList<String>();
        while (reader.hasNextLine()) {
            int lineNum = Integer.parseInt(reader.nextLine());
            line.clear();
            while (true) {
                String name = reader.nextLine();
                if (name.equals("end")) break;
                line.add(name);
            }
            // airport express is somewhat special
            if (lineNum == 0) {
                setAirportExpress(line, graph);
            }
            else {
                for (int i = 0; i < line.size(); i++) {
                    String id = line.get(i);
                    // terminal station
                    if ((i == 0) || (i == line.size()-1)) {
                        Station s = graph.get(id);
                        Edge e = new Edge();
                        e.out = (i == 0) ? line.get(i+1) : line.get(i-1);
                        e.capacity = 3.5;
                        e.line = lineNum;
                        s.adj.add(e);
                        graph.put(id, s);
                    }else {
                        Station s = graph.get(id);
                        Edge e1 = new Edge();
                        Edge e2 = new Edge();
                        e1.out = line.get(i+1);
                        e2.out = line.get(i-1);
                        if (s.isTransfer) {
                            e1.capacity = 3;
                            e2.capacity = 3;
                        }
                        e1.line = e2.line = lineNum;
                        s.adj.add(e1);
                        s.adj.add(e2);
                        graph.put(id, s);
                    }
                }
                // loop line: 2 and 10; connecting terminals
                if ((lineNum == 2) || (lineNum == 10)) {
                    String start = line.get(0);
                    String terminal = line.get(line.size()-1);
                    Station startStation = graph.get(start);
                    Station terminalStation = graph.get(terminal);
                    Edge e3 = new Edge();
                    Edge e4 = new Edge();
                    e3.out = terminal;
                    e3.capacity = 3.5;
                    e3.line = lineNum;
                    startStation.adj.add(e3);
                    e4.out = start;
                    e4.capacity = 3.5;
                    e4.line = lineNum;
                    terminalStation.adj.add(e4);
                    graph.put(start, startStation);
                    graph.put(terminal, terminalStation);
                }
            }
        }
        reader.close();
    }

    public static double dijkstra(HashMap<String, Station> graph, String s, String t, double transferWait) {
        Station source = graph.get(s);
        Station terminal = graph.get(t);
        source.time = 0;
        HeapforTime bag = new HeapforTime();
        bag.push_back(source);

        while (!bag.isEmpty()) {
            String current = bag.pop();
            Station currentStation = graph.get(current);
            currentStation.known = true;

            // if destination's shortest path has been known, break the loop
            if (t.equals(current)) break;

            int prevLine = -1;
            if (!current.equals(s)) prevLine = match(graph, current, currentStation.path);

            for (int i = 0; i < currentStation.adj.size(); i++) {
                String next = currentStation.adj.get(i).out;
                int nextLine = currentStation.adj.get(i).line;
                Station nextStation = graph.get(next);

                // If the next station's shortest path is not known, it needs to be updated.
                if (!nextStation.known){
                    // If there is a switch of lines, add the additional wait time to capacity
                    if (currentStation.isTransfer && prevLine != nextLine) {
                        if (currentStation.time + currentStation.adj.get(i).capacity + transferWait < nextStation.time) {
                            nextStation.time = currentStation.time + currentStation.adj.get(i).capacity + transferWait;
                            nextStation.path = current;
                            bag.push_back(nextStation);
                        }
                    }else {
                        if (currentStation.time + currentStation.adj.get(i).capacity < nextStation.time) {
                            nextStation.time = currentStation.time + currentStation.adj.get(i).capacity;
                            nextStation.path = current;
                            bag.push_back(nextStation);
                        }
                    }
                }
            }
        }
        return terminal.time;
    }

    public static int dijkstraforLeastTransfers(HashMap<String, Station> graph, String s, String t) {
        Station source = graph.get(s);
        Station terminal = graph.get(t);
        source.num = 0;
        source.time = 0;
        HeapforTransfer bag = new HeapforTransfer();
        bag.push_back(source);

        while (!bag.isEmpty()) {
            String current = bag.pop();
            Station currentStation = graph.get(current);
            currentStation.known = true;

            // if destination's shortest path has been known, break the loop
            if (t.equals(current)) break;

            int prevLine = -1;
            if (!current.equals(s)) prevLine = match(graph, current, currentStation.path);

            for (int i = 0; i < currentStation.adj.size(); i++) {
                String next = currentStation.adj.get(i).out;
                int nextLine = currentStation.adj.get(i).line;
                Station nextStation = graph.get(next);

                // If the next station's shortest path is not known, it needs to be updated.
                if (!nextStation.known){
                    // If there is a switch of lines, add the additional wait time to capacity; +1 to the transfer stations
                    if (currentStation.isTransfer && prevLine != nextLine) {
                        if (currentStation.num + 1 < nextStation.num){
                            nextStation.num = currentStation.num + 1;
                            nextStation.path = current;
                            bag.push_back(nextStation);
                        }
                    }else {
                        if (currentStation.num < nextStation.num){
                            nextStation.num = currentStation.num;
                            nextStation.path = current;
                            bag.push_back(nextStation);
                        }
                    }
                }
            }
        }
        return terminal.num;
    }

    public static int dijkstraforMostSites(HashMap<String, Station> graph, String s, String t) {
        Station source = graph.get(s);
        Station terminal = graph.get(t);
        if (source.isSite) source.siteNumber = 1;
        else source.siteNumber = 0;
        HeapforSite bag = new HeapforSite();
        bag.push_back(source);

        while (!bag.isEmpty()) {
            String current = bag.pop();
            Station currentStation = graph.get(current);
            currentStation.known = true;

            // if destination's shortest path has been known, break the loop
            if (t.equals(current)) break;

            for (int i = 0; i < currentStation.adj.size(); i++) {
                String next = currentStation.adj.get(i).out;
                Station nextStation = graph.get(next);

                // If the next station's shortest path is not known, it needs to be updated.
                if (!nextStation.known){
                    // If next station is a site
                    if (nextStation.isSite && !nextStation.siteName.equals("Beijing Capital International Airport")) {
                        // if the station is a site get its site number.
                        int number = 0;
                        if (nextStation.siteName.contains(",")){
                            String[] single = nextStation.siteName.split(", ");
                            number = single.length;
                        }else number = 1;

                        if (currentStation.siteNumber + number > nextStation.siteNumber){
                            nextStation.siteNumber = currentStation.siteNumber + number;
                            nextStation.path = current;
                            bag.push_back(nextStation);
                        }
                    }else {
                        if (currentStation.siteNumber > nextStation.siteNumber){
                            nextStation.siteNumber = currentStation.siteNumber;
                            nextStation.path = current;
                            bag.push_back(nextStation);
                        }
                    }
                }
            }
        }
        return terminal.siteNumber;
    }

    public static ArrayList<Station> printPath(HashMap<String, Station> g, String t) {
        ArrayList<Station> switchInfo = new ArrayList<Station>();
        Station cur = g.get(t);
        while (cur.path != null) {
            if (g.get(cur.path).path != null) {
                int line1 = match(g, cur.name, cur.path);
                int line2 = match(g, cur.path, g.get(cur.path).path);
                if (line1 != line2) {
                    Station switchStation = g.get(cur.path);
                    switchStation.from = line2;
                    switchStation.to = line1;
                    switchInfo.add(switchStation);
                }
            }
            cur = g.get(cur.path);
        }
        return switchInfo;
    }

    private static void setAirportExpress(ArrayList<String> line, HashMap<String, Station> graph) {
        String dongzhi = line.get(0);
        String sanyuan = line.get(1);
        String t3 = line.get(2);
        String t2 = line.get(3);
        Edge e1 = new Edge();
        Edge e2 = new Edge();
        e1.out = sanyuan;
        e1.capacity = 6;
        graph.get(dongzhi).adj.add(e1);
        e2.out = dongzhi;
        e2.capacity = 6;
        graph.get(sanyuan).adj.add(e2);
        Edge e3 = new Edge();
        e3.out = t3;
        e3.capacity = 10;
        graph.get(sanyuan).adj.add(e3);
        Edge e4 = new Edge();
        e4.out = t2;
        e4.capacity = 9;
        graph.get(t3).adj.add(e4);
        Edge e5 = new Edge();
        e5.out = line.get(1);
        e5.capacity = 15;
        graph.get(t2).adj.add(e5);
    }

    public static int match(HashMap<String, Station> graph, String s1, String s2) {
        Station a = graph.get(s1);
        Station b = graph.get(s2);
        for (int i = 0; i < a.lines.length; i++) {
            for (int j = 0; j < b.lines.length; j++) if (a.lines[i] == b.lines[j]) return a.lines[i];
        }
        // This line of code should never be reached. I set 3 because Beijing subway doesn't have line 3
        return 3;
    }
}
