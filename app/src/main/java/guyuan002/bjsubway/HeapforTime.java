package guyuan002.bjsubway;

import java.util.ArrayList;

/*
 * Class Responsibilities:
 * This class is responsible for encapsulating a priority queue(heap) that is used to update and
 * maintain the least time so far during the relaxation process.
 * Every time an item is appended or popped from the heap, the heap property will be maintained.
 * */
public class HeapforTime {

    public ArrayList<Station> bag;


    public HeapforTime() {
        bag = new ArrayList<Station>();
    }

    public boolean isEmpty(){
        return bag.size()==0;
    }

    public void push_back(Station s) {
        bag.add(s);
        upHeap(bag.size()-1);
    }

    public String pop() {
        String next = bag.get(0).name;
        bag.set(0, bag.get(bag.size()-1));
        bag.remove(bag.size()-1);
        downHeap(0);
        return next;
    }

    private void upHeap(int i) {
        int current = i;
        int parent = (i-1)/2;
        if ((i>0) && (bag.get(current).time < bag.get(parent).time)) {
            Station temp = bag.get(current);
            bag.set(current, bag.get(parent));
            bag.set(parent, temp);
            upHeap(parent);
        }
    }

    private void downHeap(int i) {
        int smaller = i;
        int leftChild = 2*i+1;
        int rightChild = 2*i+2;
        if ((leftChild < bag.size()) && (bag.get(leftChild).time < bag.get(smaller).time)) smaller = leftChild;
        if ((rightChild < bag.size()) && (bag.get(rightChild).time < bag.get(smaller).time)) smaller = rightChild;
        if (smaller != i) {
            Station temp = bag.get(i);
            bag.set(i, bag.get(smaller));
            bag.set(smaller, temp);
            downHeap(smaller);
        }
    }
}
