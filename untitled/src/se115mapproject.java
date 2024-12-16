class City {
    String name;

    public City(String name) {
        this.name = name;
    }
}

class CountryMap {
    private final City[] cities;
    private final int[][] routes;
    private int amountOfCities;

    public CountryMap(int numberOfCities) {
        cities = new City[numberOfCities];
        routes = new int[numberOfCities][numberOfCities];
        amountOfCities = 0;
        for (int i = 0; i < numberOfCities; i++) {
            for (int j = 0; j < numberOfCities; j++) {
                routes[i][j] = -1;
            }
        }
    }
}





















public class se115mapproject {
    public static void main(String[] args){

    }
}
