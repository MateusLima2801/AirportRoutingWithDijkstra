package com.example;

import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectorDB {
    final String user = "root";
    final String password = "Dona2801#";
    final String url = "jdbc:mysql://127.0.0.1:3306/airport_db";
    Connection conn = null;

    public ConnectorDB()
    {
       connect2DB();
    }

    //conecta com o banco de dados
    public void connect2DB()
    {
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    //le dados de aeroportos do banco para uma lista de 'Airport'
    public ArrayList<Airport> createAirportList() {
        ArrayList<Airport> list = new ArrayList<Airport>();
        try {

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM airports");
            while (rs.next()) {
                String code = rs.getString("code");
                double latitude = rs.getDouble("latitude");
                double longitude = rs.getDouble("longitude");
                String city = new String(rs.getBytes("city"));
                String state = new String(rs.getBytes("state"));
                list.add(new Airport(code, new Local(latitude, longitude), city, state));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        return list;
    }

    //determina o proximo indice na tabela de itinerarios e chama 
    //a funcao que os registra
    public void registerItinerary(ArrayList<Integer> path) {
        String table = "itineraries";
        int n_rows = 0;
        int id_itinerary = 0;
        ResultSet rs, rs2;
        Statement stmt;
        try {
            //conta quantos registros ha na tabela
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table);
            rs.next();
            n_rows = rs.getInt("COUNT(*)");
            rs.close();
            //se nao houver registros o id sera 1
            if (n_rows == 0)
                id_itinerary = 1;
            //se ja houver registros captura o id do último e o incrementa
            else {
                rs2 = stmt.executeQuery("SELECT id_itinerary FROM " + table);
                rs2.last();
                id_itinerary = rs2.getInt("id_itinerary") + 1;
                rs2.close();
            }
            //realiza o registro
            writeRows(id_itinerary, path);
            stmt.close();
        } catch (SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    //registra no banco de dados o resultado de uma pesquisa
    public void writeRows(int id, ArrayList<Integer> path) {
        int amtOfSteps = path.size() - 1;
        try {
            for (int step = 1; step <= amtOfSteps; step++) {
                String query = "INSERT INTO itineraries VALUES (?,?,?,?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, id);
                stmt.setInt(2, step);
                stmt.setInt(3, path.get(step - 1) + 1);
                stmt.setInt(4, path.get(step) + 1);
                stmt.execute();
                stmt.close();
            }
        } catch (SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    //esvazia a tabela de itinerarios 
    public void clearItineraries() {
        try {
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            stmt.executeUpdate("DELETE FROM itineraries;");
            stmt.close();
        } catch (SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
}
