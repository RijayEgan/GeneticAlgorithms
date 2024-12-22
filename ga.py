import numpy as np
import random


cities = {
    "Anaheim": (33.8366, -117.9143),
    "Santa Ana": (33.7455, -117.8677),
    "Irvine": (33.6846, -117.8265),
    "Huntington Beach": (33.6595, -117.9988),
    "Fullerton": (33.8704, -117.9243),
    "Costa Mesa": (33.6411, -117.9187),
    "Orange": (33.7879, -117.8531),
    "Garden Grove": (33.7743, -117.9370),
    "Laguna Beach": (33.5427, -117.7854),
    "Mission Viejo": (33.5967, -117.6590)
}


def haversine(coord1, coord2):
    lat1, lon1 = coord1
    lat2, lon2 = coord2
    R = 6371  
    dlat = np.radians(lat2 - lat1)
    dlon = np.radians(lon2 - lon1)
    a = np.sin(dlat / 2) ** 2 + np.cos(np.radians(lat1)) * np.cos(np.radians(lat2)) * np.sin(dlon / 2) ** 2
    c = 2 * np.arctan2(np.sqrt(a), np.sqrt(1 - a))
    return R * c


def total_distance(path):
    return sum(haversine(cities[path[i]], cities[path[i + 1]]) for i in range(len(path) - 1)) + \
           haversine(cities[path[-1]], cities[path[0]])


def create_population(pop_size):
    population = []
    for _ in range(pop_size):
        individual = list(cities.keys())
        random.shuffle(individual)
        population.append(individual)
    return population


def fitness(individual):
    return 1 / total_distance(individual)


def select_parents(population):
    population_fitness = [fitness(ind) for ind in population]
    total_fitness = sum(population_fitness)
    probabilities = [f / total_fitness for f in population_fitness]
    return population[np.random.choice(len(population), p=probabilities)], \
           population[np.random.choice(len(population), p=probabilities)]


def crossover(parent1, parent2):
    start, end = sorted(random.sample(range(len(parent1)), 2))
    child = [None] * len(parent1)
    child[start:end + 1] = parent1[start:end + 1]

    fill_positions = [i for i in range(len(parent2)) if i < start or i > end]
    child_vals = [c for c in parent2 if c not in child]

    for pos, val in zip(fill_positions, child_vals):
        child[pos] = val
    return child


def mutate(individual, mutation_rate=0.1):
    for i in range(len(individual)):
        if random.random() < mutation_rate:
            j = random.randint(0, len(individual) - 1)
            individual[i], individual[j] = individual[j], individual[i]
    return individual


def genetic_algorithm(generations=500, pop_size=10, mutation_rate=0.1):
    population = create_population(pop_size)
    for generation in range(generations):
        population = sorted(population, key=lambda x: total_distance(x))

        new_population = population[:2]  

        while len(new_population) < pop_size:
            parent1, parent2 = select_parents(population)
            child = crossover(parent1, parent2)
            child = mutate(child, mutation_rate)
            new_population.append(child)

        population = new_population

        
        if generation % 50 == 0:
            best_route = population[0]
            print(f"Generation {generation}: Best Distance = {total_distance(best_route):.2f} km")

    best_route = population[0]
    print("\nBest route found:")
    print(" -> ".join(best_route) + f" -> {best_route[0]}")
    print(f"Distance: {total_distance(best_route):.2f} km")


genetic_algorithm()
