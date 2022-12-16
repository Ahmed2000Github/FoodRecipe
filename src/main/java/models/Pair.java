package models;

public class Pair<FIRST, SECOND> {

    public FIRST first;
    public SECOND second;

    public Pair(FIRST first, SECOND second) {
        this.first = first;
        this.second = second;
    }
}