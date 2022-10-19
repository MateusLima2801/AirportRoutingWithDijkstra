package com.example;

import java.util.*;
import java.lang.Thread;
import javafx.util.*;

public class Program
{
    public static void main(String[] args)
    {
        ConnectorDB connectorDB = new ConnectorDB();
        //connectorDB.clearItineraries();
        ArrayList<Airport> collection = connectorDB.createAirportList();
        App app(collection);
        ArrayList<Pair<int>> routes =new ArrayList<Pair<int>>();
        for(int i = 0; i<collection.size(); i++)
        {
            for(int j = i+1; j<collection.size(); j++)
            {
                routes.add(new Pair<int>(i,j));
            }
        }
        
    }
}