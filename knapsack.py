import random


items = [
    {'weight': 2, 'value': 3},
    {'weight': 3, 'value': 4},
    {'weight': 4, 'value': 5},
    {'weight': 5, 'value': 6},
    {'weight': 9, 'value': 10}
]
max_weight = 10


population_size = 10
generations = 100
mutation_rate = 0.1
crossover_rate = 0.7


def create_individual():
    return [random.randint(0, 1) for _ in range(len(items))]

def create_population():
    return [create_individual() for _ in range(population_size)]


def fitness(individual):
    weight = sum(individual[i] * items[i]['weight'] for i in range(len(individual)))
    value = sum(individual[i] * items[i]['value'] for i in range(len(individual)))
    
    if weight > max_weight:
        return 0  
    return value


def selection(population):
    fitness_values = [fitness(ind) for ind in population]
    total_fitness = sum(fitness_values)
    if total_fitness == 0:
        return random.choice(population)
    selection_probs = [f / total_fitness for f in fitness_values]
    return population[random.choices(range(len(population)), selection_probs, k=1)[0]]


def crossover(parent1, parent2):
    if random.random() > crossover_rate:
        return parent1.copy(), parent2.copy()
    
    point = random.randint(1, len(parent1) - 1)
    child1 = parent1[:point] + parent2[point:]
    child2 = parent2[:point] + parent1[point:]
    return child1, child2


def mutate(individual):
    for i in range(len(individual)):
        if random.random() < mutation_rate:
            individual[i] = 1 - individual[i]
    return individual


def genetic_algorithm():
    population = create_population()
    
    for generation in range(generations):
        
        new_population = []
        while len(new_population) < population_size:
            parent1 = selection(population)
            parent2 = selection(population)
            child1, child2 = crossover(parent1, parent2)
            new_population.extend([mutate(child1), mutate(child2)])
        
        population = new_population[:population_size]
        
        
        best_individual = max(population, key=fitness)
        best_fitness = fitness(best_individual)
        
        print(f"Generation {generation+1}: Best Fitness = {best_fitness}")

    
    best_individual = max(population, key=fitness)
    return best_individual, fitness(best_individual)


best_solution, best_value = genetic_algorithm()
print(f"\nBest solution (item selection): {best_solution}")
print(f"Total value: {best_value}")
