package com.z_iti_271304_u2_e03.simulation;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SimulationThread implements Runnable {

    private double populationCount = 2; // Población inicial
    private final float K = 0.002f;
    private final int MAX = 1450;
    private boolean limitedFoodEventActive = false;
    private boolean predatorEventActive = false;

    private int generationCount = 0;
    private int generationIntervalMillis = 1000;

    // Lista de listeners
    private List<SimulationListener> listeners = new ArrayList<>();

    public void addListener(SimulationListener listener) {
        listeners.add(listener);
    }

    @Override
    public void run() {
        try {
            // el hilo muere cuando la población alcanza el límite
            while (populationCount < MAX) {
                Thread.sleep(generationIntervalMillis); // duración de cada generación

                if (predatorEventActive) {
                    populationCount *= 0.1; // Reducir la población en un 90%
                    predatorEventActive = false;
                    Log.d("SimulationThread", "Población reducida:" + populationCount);

                    // si la población es menor a 2, la simulación termina
                    if (populationCount < 2) {
                        notifyErrorListeners("Toda la población ha muerto");
                    }
                }

                // TODO aqui van más eventos
                // TODO implementar mutaciones

                /* Modelo de crecimiento
                 * Ver: https://homework.study.com/explanation/6-solve-using-differential-equations-population-growth-let-n-t-represents-the-number-of-rabbits-in-a-habitat-after-t-month-the-growth-rate-of-the-rabbits-is-k-given-the-model-n-t-kn-t-2500-n.html
                 *
                 * Si la población nunca se ve afectada por eventos, la población sige creciendo
                 * exponencialmente hasta alcanzar 6 generaciones.
                 */
                populationCount += K * populationCount * (MAX - populationCount);
                generationCount++;

                Log.d("SimulationThread", "Generación " + generationCount + ", Población: " + populationCount);

                // Notificar a los listeners sobre el estado actual
                notifyListeners();
            }
        } catch (InterruptedException e) {
            Log.d("SimulationThread", "Hilo interrumpido");
        }

        // Si el bucle termina, los conejos han dominado el mundo
        notifyErrorListeners("Los conejos han dominado el mundo.");
    }

    private void notifyErrorListeners(String errorMessage) {
        for (SimulationListener listener : listeners) {
            listener.onError(errorMessage);
        }
    }

    private void notifyListeners() {
        for (SimulationListener listener : listeners) {
            listener.onUpdate(generationCount, populationCount, predatorEventActive, limitedFoodEventActive);
        }
    }

    public void triggerEvent(SimulationEvents event) {
        if (event == SimulationEvents.LIMITED_FOOD_EVENT) {
            limitedFoodEventActive = true;
            Log.d("SimulationThread", "Evento de comida limitada activado");
        } else if (event == SimulationEvents.PREDATOR_EVENT) {
            predatorEventActive = !predatorEventActive;
            Log.d("SimulationThread", "Evento de depredador activado");
        }
    }
}
