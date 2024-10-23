package com.z_iti_271304_u2_e03;

public interface SimulationListener {
    void onUpdate(int generationCount, double populationCount, boolean predatorEventActive, boolean limitedFoodEventActive);
}
