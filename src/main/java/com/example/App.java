package com.example;

import java.util.*;

public class App {
    public static void main(String args[]) {
        ConnectorDB connectorDB = new ConnectorDB();
        //connectorDB.clearItineraries();
        ArrayList<Airport> collection = connectorDB.createAirportList();
        int[] route = getRoute(collection);
        double[][] distances = createMatrixOfDistances(collection, route[0], route[1]);
        ArrayList<Integer> path = ShorterPath(route[0], route[1], distances, collection.size());
        connectorDB.registerItinerary(path);
        System.out.printf("===============================================\n");
        System.out.printf("=================== RESULT ====================\n");
        System.out.printf("===============================================\n");

        for(int i = 0; i< path.size() -1; i++)
        {
            System.out.printf("[%s] %s (%s) -> [%s] %s (%s)\n",
            collection.get(path.get(i)).Code,
            collection.get(path.get(i)).City,
            collection.get(path.get(i)).State,
            collection.get(path.get(i+1)).Code,
            collection.get(path.get(i+1)).City,
            collection.get(path.get(i+1)).State
        );
        }

    }

    public static double[][] createMatrixOfDistances(ArrayList<Airport> list, int origin, int destination) {
        double[][] distances = new double[list.size()][list.size()];

        for (int i = 0; i < list.size(); i++) {
            distances[i][i] = 0;
            for (int j = i + 1; j < list.size(); j++) {
                distances[i][j] = list.get(i).Point.calculateDistance(list.get(j).Point);
                distances[j][i] = distances[i][j];
            }
        }

        // condition for non direct flights
        distances[origin][destination] = Double.POSITIVE_INFINITY;
        distances[destination][origin] = Double.POSITIVE_INFINITY;

        return distances;
    }

    public static ArrayList<Integer> ShorterPath(int origin, int destination, double[][] costs, int size) {

        ArrayList<Integer> path = new ArrayList<Integer>();
        double[] estimatedCost = new double[size];
        Integer[] precedents = new Integer[size];
        boolean[] isClosed = new boolean[size];

        Arrays.fill(estimatedCost, Double.POSITIVE_INFINITY);
        Arrays.fill(precedents, -1);
        Arrays.fill(isClosed, false);
        estimatedCost[origin] = 0;

        for (int i = 0; i < size; i++) {
            int smallerFalse = smallerFalseIndex(estimatedCost, isClosed, size);
            isClosed[smallerFalse] = true;

            for (int j = 0; j < size; j++) {
                if (!isClosed[j]) {
                    double supposedCost = estimatedCost[smallerFalse] + costs[smallerFalse][j];
                    if (supposedCost < estimatedCost[j]) {
                        estimatedCost[j] = supposedCost;
                        precedents[j] = smallerFalse;
                    }
                }
            }

        }

        ArrayList<Integer> reversePath = new ArrayList<Integer>();
        int stop = destination;
        reversePath.add(stop);
        while (stop != origin) {
            stop = precedents[stop];
            reversePath.add(stop);
        }

        for (int i = 0; i < reversePath.size(); i++) {
            path.add(reversePath.get(reversePath.size() - i - 1));
        }

        return path;
    }

    public static int smallerFalseIndex(double[] array, boolean[] isClosed, int size) {
        int smallerFalse = -1;
        int i = 0;
        while (smallerFalse == -1 && i < size) {
            if (!isClosed[i++]) {
                smallerFalse = i - 1;
                break;
            }
        }

        for (int j = i; j < size; j++) {
            if (!isClosed[j]) {
                if (array[j] < array[smallerFalse])
                    smallerFalse = j;
            }
        }

        return smallerFalse;
    }

    public static int[] getRoute(ArrayList<Airport> collection) {
        Scanner input = new Scanner(System.in);
        String[] route_code = { " ", " " };
        int[] route = { -1, -1 };
        System.out.printf("===============================================\n");
        System.out.printf("===== SHORTEST PATH WITH MORE THAN 1 STEP =====\n");
        System.out.printf("===============================================\n");
        System.out.printf("==================== ORIGIN ===================\n");
        System.out.printf("===============================================\n");
        route_code[0] = getCode(collection, false, " ", input);
        route[0] = getIndex(collection, route_code[0]);
        System.out.printf("===============================================\n");
        System.out.printf("================= DESTINATION =================\n");
        System.out.printf("===============================================\n");
        route_code[1] = getCode(collection, true, route_code[0], input);
        route[1] = getIndex(collection, route_code[1]);
        input.close();

        return route;
    }

    public static int getIndex(ArrayList<Airport> collection, String code) {
        for (Airport airport : collection) {
            if (airport.Code.equals(code)) {
                return collection.indexOf(airport);
            }
        }
        return -1;
    }

    public static String getCode(ArrayList<Airport> collection, boolean isDestination, String origin, Scanner input) {

        SortedSet<String> states = new TreeSet<String>();
        Set<String> codes = new HashSet<String>();

        for (Airport e : collection) {
            states.add(e.State);
        }

        int i = 1;
        for (String state : states) {
            System.out.printf("(%d) %s\n", i, state);
            i++;
        }
        System.out.printf("===============================================\n");
        boolean key;
        int choice;
        String state;
        do {
            key = false;
            System.out.printf("Choose the state (number): ");
            choice = input.nextInt();
            if (choice < 1 || choice > collection.size()) {
                System.out.printf("Invalid option!\n");
                key = true;
            }
        } while (key);
        System.out.printf("===============================================\n");
        state = states.toArray(new String[states.size()])[--choice];

        for (Airport a : collection) {
            if (a.State == state) {
                System.out.printf("(%s) %s\n", a.Code, a.City);
                codes.add(a.Code);
            }
        }

        String code;
        do {
            key = false;
            System.out.printf("Choose the city (LLL): ");
            input.reset();
            code = input.next();

            if (!codes.contains(code)) {
                if (!isDestination || (isDestination && code != origin)) {
                    System.out.printf("Invalid option!\n");
                    key = true;
                }
            }
        } while (key);

        return code;
    }
}
