package guyuan002.bjsubway;

/*
 * Class Responsibilities:
 * This class is responsible for encapsulating a edge with capacity
 * (time the subway commutes for one station, usually 3, but airport
 * express line is special) and the connecting station name.
 * */
public class Edge {
    public double capacity;
    public int line;
    public String out;

    public Edge() {
        line = 0;
        capacity = 2.5;
        out = null;
    }
}
