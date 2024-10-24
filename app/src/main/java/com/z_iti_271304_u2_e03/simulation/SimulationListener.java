package com.z_iti_271304_u2_e03.simulation;

public interface SimulationListener {
    void onUpdate(int generationCount, double populationCount, boolean predatorEventActive, boolean limitedFoodEventActive);
    void onError(String errorMessage);
    void onFinish();
}
