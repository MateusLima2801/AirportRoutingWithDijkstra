package com.example;

import java.util.*;
import javafx.util.*;

public class App {
    final  int line_size = 50;
    ArrayList<Airport> collection;

    public App(ArrayList<Airport> collection_)
    {
        collection = collection_;
    }

    /*
     * Conecta-se com o banco de dados para ler as informaçoes 
     * dos aeroportos, apresenta menu para ler a rota da
     * entrada-padrao, gera matriz de custos para as conexoes 
     * entre dois desses aeroportos, calcula o caminho de
     * custo minimo e registra-o no banco de dados.
     */
    public double GetShortestRouteDistance(Pair<int> route) {
        double[][] distances = createMatrixOfDistances(collection, route.getKey(), route.getValue());
        ArrayList<Integer> path = ShorterPath(route.getKey(), route.getValue(), distances, collection.size());
        //connectorDB.registerItinerary(path);
        return calculateTotalDistance(path, distances); 
        // line("");
        // line("RESULT");
        // line("");

        // for(int i = 0; i< path.size() -1; i++)
        // {
        //     System.out.printf("[%s] %s (%s) -> [%s] %s (%s)\n",
        //     collection.get(path.get(i)).Code,
        //     collection.get(path.get(i)).City,
        //     collection.get(path.get(i)).State,
        //     collection.get(path.get(i+1)).Code,
        //     collection.get(path.get(i+1)).City,
        //     collection.get(path.get(i+1)).State
        // );



        }

    }
    
    public  double calculateTotalDistance(ArrayList<Integer> path, double[][] distances)
    {
        double totalDistance = 0.;
        for(int i = 0; i<path.length()-1; i++)
        {
            totalDistance += distances[path.get(i)][path.get(i+1)];
        }
        return totalDistance;
    }

    /*
     * Com base no codigo do aeroporto extrai o indice
     * deste na lista de aeroportos
     */
    public  int getIndex(ArrayList<Airport> collection, String code) {
        for (Airport airport : collection) {
            if (airport.Code.equals(code)) {
                return collection.indexOf(airport);
            }
        }
        return -1;
    }

    /*
     * Apresenta as alternativas de aeroportos e extrai o codigo
     * do aeroporto selecionado.
     */
    public  String getCode(ArrayList<Airport> collection, boolean isDestination, String origin, Scanner input) {

        SortedSet<String> states = new TreeSet<String>();
        Set<String> codes = new HashSet<String>();
        String code = "XXX";

        for (Airport e : collection) {
            states.add(e.State);
        }

        int i = 1;
        for (String state : states) {
            System.out.printf("(%d) %s\n", i, state);
            i++;
        }
        
        boolean keys[] = { true, true, true };
        int choice;
        String state;
        do {
            do {
                keys[0] = false;
                line("");
                System.out.printf("Choose the state (number): ");
                choice = input.nextInt();
                line("");
                if (choice < 1 || choice > states.size()) {
                    System.out.printf("Invalid option!\n");
                    keys[0] = true;
                }
            } while (keys[0]);

            state = states.toArray(new String[states.size()])[--choice];

            for (Airport a : collection) {
                if (a.State.equals(state)) {
                    System.out.printf("(%s) %s\n", a.Code, a.City);
                    codes.add(a.Code);
                }
            }

            do {
                keys[1] = false;
                keys[2] = false;
                line("");
                System.out.printf("Choose the city (LLL): ");
                input.reset();
                code = input.next();
                line("");

                if (!codes.contains(code)) {
                    System.out.printf("Invalid option!\n");
                    keys[1] = true;
                } else if (isDestination && code.equals(origin)) {
                    System.out.printf("Invalid option!\n");
                    keys[2] = true;
                }
            } while (keys[1]);
        } while (keys[2]);
        return code;
    }

    /*
     * Apresenta o menu de entrada e retorna a rota, o par
     * (origin, destination).
     */
    public  int[] getRoute(ArrayList<Airport> collection) {
        Scanner input = new Scanner(System.in);
        String[] route_code = { " ", " " };
        int[] route = { -1, -1 };
        line("");
        line("SHORTEST PATH WITH MORE THAN 1 STEP");
        line("");
        line("ORIGIN");
        line("");
        route_code[0] = getCode(collection, false, "", input);
        route[0] = getIndex(collection, route_code[0]);
        line("");
        line("DESTINATION");
        line("");
        route_code[1] = getCode(collection, true, route_code[0], input);
        route[1] = getIndex(collection, route_code[1]);
        input.close();

        return route;
    }

    /*
     * Cria matriz de adjacencia do grafo de aeroportos
     * ponderada com o custo de cada aresta.
     */
    public  double[][] createMatrixOfDistances(ArrayList<Airport> list, int origin, int destination) {
        double[][] distances = new double[list.size()][list.size()];

        for (int i = 0; i < list.size(); i++) {
            distances[i][i] = 0;
            for (int j = i + 1; j < list.size(); j++) {
                distances[i][j] = list.get(i).Point.calculateDistance(list.get(j).Point);
                distances[j][i] = distances[i][j];
            }
        }

        // condição para evitar vôos diretos
        distances[origin][destination] = Double.POSITIVE_INFINITY;
        distances[destination][origin] = Double.POSITIVE_INFINITY;

        return distances;
    }

    /*
     * Calcula o caminho de menor custo entre origem
     * e destino, baseado na matriz de custos.
     */
    public  ArrayList<Integer> ShorterPath(int origin, int destination, double[][] costs, int size) {
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

    /*
     * Avalia qual o indice do no aberto (não analisado)
     * de menor custo
     */
    public  int smallerFalseIndex(double[] array, boolean[] isClosed, int size) {
        int smallerFalse = -1;
        int i = 0;
        while (i < size) {
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

    /*
     * Imprime linha de simbolos com texto ao meio 
     */
    public  void line(String text)
    {
        String symbol = "=";
        if(text.length()%2 == 1) text += " ";
        int span = (line_size - text.length() - 2)/2;
        for(int i = 1; i<=span; i++) System.out.print(symbol);
        if(text != "") System.out.print(" " + text + " ");
        else System.out.print(symbol+symbol);
        for(int i = 1; i<=span; i++) System.out.print(symbol);
        System.out.println();
    }
}
