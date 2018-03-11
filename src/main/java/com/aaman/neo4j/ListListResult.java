package com.aaman.neo4j;


import java.util.List;

public class ListListResult {
    public final List<List<Object>> value;

    public ListListResult(List<List<Object>> value) {
        this.value = value;
    }
}