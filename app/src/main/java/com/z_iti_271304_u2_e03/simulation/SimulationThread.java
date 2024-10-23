package com.z_iti_271304_u2_e03;

import java.util.ArrayList;
import java.util.List;

public class SimulationThread implements Runnable {

    private double populationCount = 2; // Población inicial
    private final float K = 0.002f;
    private final int MAX = 1450;
    private boolean limitedFoodEventActive = false; // Estado del evento de comida limitada
    private boolean predatorEventActive = false;

    private int generationCount = 0;
    private int generationIntervalMillis = 1000;

    // Lista de listeners
    private List<SimulationListener> listeners = new ArrayList<>();

    // Método para agregar listeners
    public void addListener(SimulationListener listener) {
        listeners.add(listener);
    }

    @Override
    public void run() {
        try {
            // Detener el hilo cuando la población haya superado el máximo
            while (populationCount < MAX) {
                // Duración de cada generación
                Thread.sleep(generationIntervalMillis);

                // Si el evento de depredador está activo, reducir la población en un 90%
                if (predatorEventActive) {
                    populationCount *= 0.1; // Reducir la población en un 90%
                    predatorEventActive = false; // Desactivar el evento después de aplicarlo
                    System.out.println("Población reducida:" + populationCount);

                    // Verificar si la población es menor a 2
                    if (populationCount < 2) {
                        throw new PopulationTooLowException("La población es demasiado baja después del ataque de depredadores.");
                    }
                }

                // Calcular el crecimiento de la población si el evento de depredador no está activo
                populationCount += K * populationCount * (MAX - populationCount); // Modelo de crecimiento
                generationCount++;

                System.out.println("Generation " + generationCount + " of " + populationCount);

                // Notificar a los listeners sobre el estado actual
                notifyListeners();
            }
        } catch (InterruptedException e) {
            System.out.println("Simulación interrumpida");
        }

        // Si el bucle termina, los conejos han dominado el mundo
        throw new WorldDominationException("Los conejos han dominado al mundo");
    }

    // Método para notificar a los listeners
    private void notifyListeners() {
        for (SimulationListener listener : listeners) {
            listener.onUpdate(generationCount, populationCount, predatorEventActive, limitedFoodEventActive);
        }
    }

    public void triggerEvent(SimulationEvents event) {
        if (event == SimulationEvents.LIMITED_FOOD_EVENT) {
            limitedFoodEventActive = true;
            System.out.println(event.toString());
        } else if (event == SimulationEvents.PREDATOR_EVENT) {
            predatorEventActive = !predatorEventActive;
            System.out.println(event);
        }
    }

    public static void main(String[] args) {
        SimulationThread simulationThread = new SimulationThread();
        Thread thread = new Thread(simulationThread);

        // Agregar un listener que imprimirá el estado de la simulación
        simulationThread.addListener((generationCount, populationCount, predatorActive, limitedFoodActive) -> {
            System.out.println("Listener: Generación " + generationCount + ", Población: " + populationCount +
                    ", Depredador activo: " + predatorActive + ", Comida limitada: " + limitedFoodActive);
        });

        thread.start();

        // Para probar el evento de depredador
        try {
            Thread.sleep(5000); // Simular algunas generaciones
            simulationThread.triggerEvent(SimulationEvents.PREDATOR_EVENT);
            Thread.sleep(1000);
            simulationThread.triggerEvent(SimulationEvents.PREDATOR_EVENT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
