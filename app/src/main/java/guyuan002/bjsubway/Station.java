package guyuan002.bjsubway;

import java.util.ArrayList;

/*
 * Class Responsibilities:
 * This class is responsible for encapsulating all information about
 * a subway station
 * */

public class Station {
    // list of edges incident to the station
    public ArrayList<Edge> adj;
    // station's ID: "English (Chinese)"
    public String name;
    // whether it is a transfer station
    public boolean isTransfer;
    // if the shortest path to this station is known
    public boolean known;
    // if the station is close to a place of interest
    public boolean isSite;
    // the station's line with its previous station along the shortest path
    public int from;
    // the station's line with its next station along the shortest path
    public int to;
    // name of the place of interest if there's one
    public String siteName;
    // list of lines the station belongs to
    public int[] lines;
    // time to travel from the source
    public double time;
    // number of transfer stations along the way
    public int num;
    // number of sites visited along the way
    public int siteNumber;
    // the previous station along the shortest path
    public String path;

    public Station() {
        adj = new ArrayList<Edge>();
        lines = new int[4];
        isTransfer = false;
        known = false;
        isSite = false;
        from = -1;
        to = -1;
        siteName = null;
        time = Integer.MAX_VALUE;
        num = Integer.MAX_VALUE;
        siteNumber = Integer.MIN_VALUE;
        path = null;
    }
}
