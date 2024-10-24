package com.z_iti_271304_u2_e03;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.z_iti_271304_u2_e03.simulation.SimulationEvent;
import com.z_iti_271304_u2_e03.simulation.SimulationEvents;
import com.z_iti_271304_u2_e03.simulation.SimulationListener;
import com.z_iti_271304_u2_e03.simulation.SimulationThread;

public class MainActivity extends AppCompatActivity {

    private Button startSimulationButton;
    private TextView generationCountTextView;
    private TextView populationCountTextView;
    private TextView activeEventsTextView;
    private LinearLayout radioGroupContainer;
    private LinearLayout checkboxGroupContainer;

    private final String[] mutationOptions = new String[]{"Piel", "Orejas", "Dientes"};
    private final SimulationEvent[] eventOptions = new SimulationEvent[]{
            new SimulationEvent("Depredador", SimulationEvents.PREDATOR_EVENT),
            new SimulationEvent("Alimento bajo", SimulationEvents.LOW_FOOD_EVENT),
            new SimulationEvent("Alimento malo", SimulationEvents.BAD_FOOD_EVENT)
    };

    private CheckBox predatorCheckBox;
    private CheckBox lowFoodCheckBox;
    private CheckBox badFoodCheckBox;

    private SimulationThread simulationThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        startSimulationButton = findViewById(R.id.start_simulation);
        generationCountTextView = findViewById(R.id.generation_count);
        populationCountTextView = findViewById(R.id.population_count);
        activeEventsTextView = findViewById(R.id.active_events);
        radioGroupContainer = findViewById(R.id.radio_group_container);
        checkboxGroupContainer = findViewById(R.id.checkbox_container);

        generateRadioGroups();
        generateCheckBoxes();

        startSimulationButton.setOnClickListener(view -> {
            // diálogo personalizado en caso de recibir un error en el listener
            Dialog dialog = new Dialog(this);
            TextView dialogMessage;
            Button dialogButton;

            dialog.setContentView(R.layout.message_dialog);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            // controles del diálogo
            dialogMessage = dialog.findViewById(R.id.message_text);
            dialogButton = dialog.findViewById(R.id.ok_button);

            simulationThread = new SimulationThread();
            Thread thread = new Thread(simulationThread);
            thread.start();

            simulationThread.addListener(new SimulationListener() {
                @Override
                public void onUpdate(int generationCount, double populationCount, boolean predatorEventActive, boolean limitedFoodEventActive) {
                    runOnUiThread(() -> {
                        generationCountTextView.setText("generation_count: " + generationCount);
                        populationCountTextView.setText("population_count: " + populationCount);
                        activeEventsTextView.setText("Events: " + (predatorEventActive ? "Predator Active" : "No Predator") + ", " + (limitedFoodEventActive ? "Limited Food" : "Food Available"));
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> {
                        dialogMessage.setText(errorMessage);
                        dialog.show();
                        dialogButton.setOnClickListener(v -> dialog.dismiss());
                    });
                }

                @Override
                public void onFinish() {
                    runOnUiThread(() -> {
                        resetForm();
                    });
                }
            });
        });
    }

    /**
     * Reinicia los controles de la interfaz
     */
    private void resetForm() {
        for (int i = 0; i < radioGroupContainer.getChildCount(); i++) {
            View view = radioGroupContainer.getChildAt(i);

            // Los radioGroups tienen TextViews así que hay que filtrar los que no sean RadioGroups
            if (view instanceof RadioGroup) {
                RadioGroup radioGroup = (RadioGroup) radioGroupContainer.getChildAt(i);
                radioGroup.clearCheck();
            }
        }

        for (int i = 0; i < checkboxGroupContainer.getChildCount(); i++) {
            CheckBox checkBox = (CheckBox) checkboxGroupContainer.getChildAt(i);
            checkBox.setChecked(false);
        }
    }

    /**
     * Genera los RadioGroups con las opciones de mutación
     */
    // TODO Hay que bloquear los radiogroups después de iniciar la simulación, ya que no se pueden
    //  cambiar las mutaciones una vez iniciada la simulación
    private void generateRadioGroups() {
        for (String option : mutationOptions) {
            RadioGroup radioGroup = new RadioGroup(this);
            TextView textView = new TextView(this);
            textView.setText(option);

            RadioButton type1 = new RadioButton(this);
            type1.setText("Tipo 1");

            RadioButton type2 = new RadioButton(this);
            type2.setText("Tipo 2");

            radioGroup.setOrientation(LinearLayout.HORIZONTAL);
            radioGroup.addView(type1);
            radioGroup.addView(type2);

            radioGroupContainer.addView(textView);
            radioGroupContainer.addView(radioGroup);
        }
    }

    /**
     * Genera los CheckBoxes con las opciones de eventos
     */
    private void generateCheckBoxes() {
        for (SimulationEvent simulationEvent : eventOptions) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(simulationEvent.name);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (simulationThread != null) {
                    simulationThread.triggerEvent(simulationEvent.type);
                }
            });
            checkboxGroupContainer.addView(checkBox);
        }
    }
}
