package com.example;

import java.lang.Math;

public class Local
{
    double Lati;
    double Longi;

    public Local(double lati, double longi)
    {
        Lati = lati;
        Longi = longi;
    }     

    //Haversine formula
    //return: distance in km
    public double calculateDistance(Local another)
    {
        double distance = 0;
        try
        {
            double radius = 6378.1; //in km
            double squared = Math.pow(Math.sin( (another.Lati - Lati)/ 2),2.);
            squared += Math.cos(Lati)*Math.cos(another.Lati)* Math.pow(Math.sin((another.Longi - Longi) / 2), 2.);
            distance = 2*radius*Math.asin(Math.sqrt(squared));
        }
        catch( Exception e)
        {
            System.out.println(e.getMessage());
        }
        

        return distance; 
    }
}