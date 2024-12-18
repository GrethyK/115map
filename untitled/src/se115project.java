import java.io.*;
import java.util.*;

class City {
    String name;

    public City(String name) {
        this.name = name;
    }
}

class CountryMap {
    private final City[] cities;
    private final int[][] roads;
    private int cityCount;

    public CountryMap(int numberOfCities) {
        cities = new City[numberOfCities];
        roads = new int[numberOfCities][numberOfCities];
        cityCount = 0;
        for (int i = 0; i < numberOfCities; i++) {
            for (int j = 0; j < numberOfCities; j++) {
                roads[i][j] = -1;
            }
        }
        for (int i = 0; i < numberOfCities; i++) {
            Arrays.fill(roads[i], -1);
        }
    }
    public void addCity(String cityName) {
        cities[cityCount++] = new City(cityName);
    }

    public void addRoad(String from, String to, int time) {
        int fromCity = findCityIndex(from);
        int toCity = findCityIndex(to);

        if (fromCity != -1 && toCity != -1) {
            roads[fromCity][toCity] = time;
            roads[toCity][fromCity] = time;
        }
    }

    private int findCityIndex(String cityName) {
        for (int i = 0; i < cityCount; i++) {
            if (cities[i].name.equals(cityName)) {
                return i;
            }
        }
        return -1;
    }

    public int[] shortestPath(String start, String end) {
        int startCity = findCityIndex(start);
        int endCity = findCityIndex(end);

        if (startCity == -1 || endCity == -1) {
            throw new IllegalArgumentException("Start or end city not found.");
        }

        int[] distances = new int[cityCount];
        int[] previous = new int[cityCount];
        boolean[] visited = new boolean[cityCount];

        for (int i = 0; i < cityCount; i++) {
            distances[i] = Integer.MAX_VALUE;
            previous[i] = -1;
            visited[i] = false;
        }
        distances[startCity] = 0;

        for (int i = 0; i < cityCount; i++) {
            int minDistance = Integer.MAX_VALUE;
            int currentCity = -1;

            for (int j = 0; j < cityCount; j++) {
                if (!visited[j] && distances[j] < minDistance) {
                    minDistance = distances[j];
                    currentCity = j;
                }
            }

            if (currentCity == -1) break;

            visited[currentCity] = true;

            for (int k = 0; k < cityCount; k++) {
                if (roads[currentCity][k] != -1 && !visited[k]) {
                    int newDist = distances[currentCity] + roads[currentCity][k];
                    if (newDist < distances[k]) {
                        distances[k] = newDist;
                        previous[k] = currentCity;
                    }
                }
            }
        }

        if (distances[endCity] == Integer.MAX_VALUE) {
            throw new IllegalStateException("No path exists between " + start + " and " + end);
        }

        return recreatePath(previous, endCity);
    }


    private int[] recreatePath(int[] previous, int endIndex) {
        int[] path = new int[cityCount];
        int pathIndex = 0;


        for (int at = endIndex; at != -1; at = previous[at]) {
            path[pathIndex++] = at;
        }


        int[] finalPath = new int[pathIndex];
        for (int i = 0; i < pathIndex; i++) {
            finalPath[i] = path[pathIndex - 1 - i];
        }
        return finalPath;
    }

    public int calculateTotalTime(int[] path) {
        int totalTime = 0;
        for (int i = 0; i < path.length - 1; i++) {
            totalTime += roads[path[i]][path[i + 1]];
        }
        return totalTime;
    }


    public void writeOutputToFile(int[] path, int totalTime, String outputFile) throws IOException {
        FileWriter writer = new FileWriter(outputFile);
        PrintWriter printWriter = new PrintWriter(writer);

        printWriter.println("Map:");
        for (int i = 0; i < cityCount; i++) {
            printWriter.print(cities[i].name + " -> ");
            boolean hasConnections = false;
            for (int j = 0; j < cityCount; j++) {
                if (roads[i][j] != -1) {
                    printWriter.print("[To: " + cities[j].name + ", Time: " + roads[i][j] + "] ");
                    hasConnections = true;
                }
            }
            if (!hasConnections) {
                printWriter.print("No connections");
            }
            printWriter.println();
        }
        printWriter.println();


        printWriter.println("Shortest Path:");
        for (int i = 0; i < path.length - 1; i++) {
            printWriter.print(cities[path[i]].name + " -> ");
        }
        printWriter.println(cities[path[path.length - 1]].name);


        printWriter.println("\nTotal time: " + totalTime);
        printWriter.close();
    }
}
public class se115project {
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Usage: java se115project <input-file>");
            return;
        }

        String inputFile = args[0];
        String outputFile;

        try {
            outputFile = System.getProperty("user.home") + "/Desktop/output.txt";
            File directory = new File(System.getProperty("user.home") + "/Desktop");
            if (!directory.exists()) {
                throw new IOException("Desktop directory does not exist.");
            }
        } catch (Exception e) {
            outputFile = "output.txt";
            System.err.println("Could not access Desktop directory. Using local output.txt instead.");
        }

        try {
            FileReader fr = new FileReader(inputFile);
            Scanner scanner = new Scanner(fr);

            int lineNumber = 0;
            boolean fileValid = true;

            int CityAmount;
            String[] cityLabels;
            int amountofRoads;

            try {
                lineNumber++;
                CityAmount = Integer.parseInt(scanner.nextLine().trim());

                lineNumber++;
                cityLabels = scanner.nextLine().trim().split("\\s+");
                if (cityLabels.length != CityAmount) {
                    throw new IllegalArgumentException("Number of cities does not match the labels provided.");
                }

                lineNumber++;
                amountofRoads = Integer.parseInt(scanner.nextLine().trim());

                CountryMap countryMap = new CountryMap(CityAmount);

                for (String cityLabel : cityLabels) {
                    countryMap.addCity(cityLabel);
                }

                for (int i = 0; i < amountofRoads; i++) {
                    lineNumber++;
                    String[] roadData = scanner.nextLine().trim().split("\\s+");
                    if (roadData.length != 3) {
                        throw new IllegalArgumentException("Invalid road data format. Expected: <city1> <city2> <time>");
                    }

                    try {
                        countryMap.addRoad(roadData[0], roadData[1], Integer.parseInt(roadData[2]));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid road time. Must be an integer.");
                    }
                }

                lineNumber++;
                String[] startEnd = scanner.nextLine().trim().split("\\s+");
                if (startEnd.length != 2) {
                    throw new IllegalArgumentException("Invalid start and end city format. Expected: <start> <end>");
                }

                int[] path = countryMap.shortestPath(startEnd[0], startEnd[1]);
                int totalTime = countryMap.calculateTotalTime(path);

                countryMap.writeOutputToFile(path, totalTime, outputFile);
                System.out.println("File read is successful!");
                System.out.println("Output written to " + outputFile);

            } catch (Exception e) {
                fileValid = false;
                System.out.println("Error Line: " + lineNumber + " " + e.getMessage());
            }

            scanner.close();

            if (!fileValid) {
                System.out.println("The input file contains errors. Please correct them and try again.");
            }

        } catch (IOException e) {
            System.err.println("Error reading the input file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

}

