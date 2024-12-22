
import java.util.*;

public class KnapsackProblem {
    static class Item {
        int weight, value;

        Item(int weight, int value) {
            this.weight = weight;
            this.value = value;
        }
    }

    static final int POPULATION_SIZE = 10;
    static final int GENERATIONS = 100;
    static final double MUTATION_RATE = 0.1;
    static final double CROSSOVER_RATE = 0.7;

    static List<Item> items = Arrays.asList(
        new Item(2, 3),
        new Item(3, 4),
        new Item(4, 5),
        new Item(5, 6),
        new Item(9, 10)
    );

    static final int MAX_WEIGHT = 10;

    public static List<Integer> createIndividual() {
        List<Integer> individual = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < items.size(); i++) {
            individual.add(random.nextInt(2));
        }
        return individual;
    }

    public static List<List<Integer>> createPopulation() {
        List<List<Integer>> population = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            population.add(createIndividual());
        }
        return population;
    }

    public static int fitness(List<Integer> individual) {
        int weight = 0, value = 0;
        for (int i = 0; i < individual.size(); i++) {
            if (individual.get(i) == 1) {
                weight += items.get(i).weight;
                value += items.get(i).value;
            }
        }
        return (weight > MAX_WEIGHT) ? 0 : value;
    }

    public static List<Integer> selection(List<List<Integer>> population) {
        List<Integer> selected = null;
        int totalFitness = population.stream().mapToInt(KnapsackProblem::fitness).sum();
        if (totalFitness == 0) {
            return population.get(new Random().nextInt(population.size()));
        }

        int randomPoint = new Random().nextInt(totalFitness);
        int runningSum = 0;
        for (List<Integer> individual : population) {
            runningSum += fitness(individual);
            if (runningSum >= randomPoint) {
                selected = individual;
                break;
            }
        }
        return selected;
    }

    public static List<Integer> mutate(List<Integer> individual) {
        Random random = new Random();
        for (int i = 0; i < individual.size(); i++) {
            if (random.nextDouble() < MUTATION_RATE) {
                individual.set(i, 1 - individual.get(i));
            }
        }
        return individual;
    }

    public static List<List<Integer>> crossover(List<Integer> parent1, List<Integer> parent2) {
        Random random = new Random();
        if (random.nextDouble() > CROSSOVER_RATE) {
            return Arrays.asList(new ArrayList<>(parent1), new ArrayList<>(parent2));
        }

        int point = random.nextInt(parent1.size() - 1) + 1;
        List<Integer> child1 = new ArrayList<>(parent1.subList(0, point));
        child1.addAll(parent2.subList(point, parent2.size()));

        List<Integer> child2 = new ArrayList<>(parent2.subList(0, point));
        child2.addAll(parent1.subList(point, parent1.size()));

        return Arrays.asList(child1, child2);
    }

    public static void main(String[] args) {
        List<List<Integer>> population = createPopulation();

        for (int generation = 1; generation <= GENERATIONS; generation++) {
            List<List<Integer>> newPopulation = new ArrayList<>();
            while (newPopulation.size() < POPULATION_SIZE) {
                List<Integer> parent1 = selection(population);
                List<Integer> parent2 = selection(population);
                List<List<Integer>> children = crossover(parent1, parent2);
                newPopulation.add(mutate(children.get(0)));
                if (newPopulation.size() < POPULATION_SIZE) {
                    newPopulation.add(mutate(children.get(1)));
                }
            }
            population = newPopulation;

            List<Integer> bestIndividual = Collections.max(population, Comparator.comparingInt(KnapsackProblem::fitness));
            int bestFitness = fitness(bestIndividual);
            System.out.println("Generation " + generation + ": Best Fitness = " + bestFitness);
        }

        List<Integer> bestIndividual = Collections.max(population, Comparator.comparingInt(KnapsackProblem::fitness));
        System.out.println("\nBest solution (item selection): " + bestIndividual);
        System.out.println("Total value: " + fitness(bestIndividual));
    }
}