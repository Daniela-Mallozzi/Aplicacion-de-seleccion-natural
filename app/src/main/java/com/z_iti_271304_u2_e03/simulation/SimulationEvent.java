package com.z_iti_271304_u2_e03.simulation;

import androidx.annotation.NonNull;

public class SimulationEvent {
    public String name;
    public SimulationEvents type;

    public SimulationEvent(String name, SimulationEvents type) {
        this.name = name;
        this.type = type;
    }

    @NonNull
    @Override
    public String toString() {
        return name + ";" + type;
    }
}
