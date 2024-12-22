import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class TravelingSalesman {

    private static final double EARTH_RADIUS = 6371; 
    private static Map<String, Double[]> cities = new HashMap<>();

    public static void main(String[] args) {
        
        loadCities("california_cities.csv");

        
        geneticAlgorithm(500, 100, 0.1);
    }

    
    private static void loadCities(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine(); 
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 4) continue;

                String city = data[1].trim(); 
                try {
                    double lat = Double.parseDouble(data[2].trim());  
                    double lon = Double.parseDouble(data[3].trim()); 

                    cities.put(city, new Double[]{lat, lon});
                    System.out.println("Loaded city: " + city + " at coordinates: (" + lat + ", " + lon + ")");
                } catch (NumberFormatException e) {
                    System.out.println("Skipping invalid data for city: " + city);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (cities.isEmpty()) {
            System.out.println("No cities found.");
        } else {
            System.out.println("Cities loaded: " + cities.size());
        }
    }

    
    private static double haversine(Double[] coord1, Double[] coord2) {
        double lat1 = coord1[0];
        double lon1 = coord1[1];
        double lat2 = coord2[0];
        double lon2 = coord2[1];

        double dlat = Math.toRadians(lat2 - lat1);
        double dlon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2) + 
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * 
                   Math.sin(dlon / 2) * Math.sin(dlon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    
    private static double totalDistance(List<String> path) {
        double distance = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            String city1 = path.get(i);
            String city2 = path.get(i + 1);
            distance += haversine(cities.get(city1), cities.get(city2));
        }
        distance += haversine(cities.get(path.get(path.size() - 1)), cities.get(path.get(0)));
        return distance;
    }

    
    private static List<List<String>> createPopulation(int popSize) {
        List<List<String>> population = new ArrayList<>();
        List<String> cityList = new ArrayList<>(cities.keySet());

        for (int i = 0; i < popSize; i++) {
            List<String> individual = new ArrayList<>(cityList);
            Collections.shuffle(individual);
            population.add(individual);
        }
        return population;
    }

    
    private static double fitness(List<String> individual) {
        double distance = totalDistance(individual);
        return distance == 0 ? 0 : 1 / distance;
    }

    
    private static List<String> crossover(List<String> parent1, List<String> parent2) {
        Random rand = new Random();
        int size = parent1.size();
        int start = rand.nextInt(size);
        int end = rand.nextInt(size);
        if (start > end) {
            int temp = start;
            start = end;
            end = temp;
        }

        Set<String> segment = new HashSet<>(parent1.subList(start, end + 1));
        List<String> child = new ArrayList<>(parent2);

        for (int i = start; i <= end; i++) {
            child.set(i, parent1.get(i));
        }

        int index = end + 1;
        for (String city : parent2) {
            if (!segment.contains(city)) {
                if (index == size) index = 0;
                child.set(index, city);
                index++;
            }
        }
        return child;
    }

    
    private static List<String> mutate(List<String> individual, double mutationRate) {
        Random rand = new Random();
        for (int i = 0; i < individual.size(); i++) {
            if (rand.nextDouble() < mutationRate) {
                int j = rand.nextInt(individual.size());
                Collections.swap(individual, i, j);
            }
        }
        return individual;
    }

    
    private static void geneticAlgorithm(int generations, int popSize, double mutationRate) {
        List<List<String>> population = createPopulation(popSize);

        for (int generation = 0; generation < generations; generation++) {
            population.sort(Comparator.comparingDouble(TravelingSalesman::totalDistance));
            List<List<String>> newPopulation = new ArrayList<>(population.subList(0, 2));

            while (newPopulation.size() < popSize) {
                List<String> parent1 = population.get(new Random().nextInt(popSize));
                List<String> parent2 = population.get(new Random().nextInt(popSize));

                List<String> child = crossover(parent1, parent2);
                mutate(child, mutationRate);
                newPopulation.add(child);
            }

            population = newPopulation;
            System.out.println("Generation " + generation + ": Best Distance = " + totalDistance(population.get(0)));
        }

        List<String> bestSolution = population.get(0);
        System.out.println("Best Solution: " + bestSolution);
        System.out.println("Best Distance: " + totalDistance(bestSolution));
    }
}
