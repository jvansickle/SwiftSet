package com.michaeloles.swiftset;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.ArrayList;

public class WorkoutViewer extends AppCompatActivity {

    private WorkoutDBHandler dbHandler;
    private String name = "";
    final private int menuOrder = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_viewer);
        setTitle("Workouts");
        ArrayList<String> exerciseList = SavedExercises.getSavedExerciseList();
        addExerciseButtons(exerciseList);
    }

    //Called when the user saves a workout
    //Gets a name for the workout and then saves it to the sqlite database
    public void saveWorkout(View view) {
        final Context context = view.getContext();
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Saving Workout");
        alert.setMessage("Name Your Workout");

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                name = input.getText().toString();
                Workout w = new Workout(name, SavedExercises.getSavedExerciseList());
                dbHandler = new WorkoutDBHandler(context, null, null, 1);
                dbHandler.addWorkout(w);
                SavedExercises.clearSavedList(context);
                Toast.makeText(context, "Workout " + name + " saved", Toast.LENGTH_SHORT).show();
            }
        });

        alert.show();
    }

    //Delete a workout from the database
    public void deleteWorkout(View view) {

    }

    //Removes all exercise from the workout and clears the exercies from the activity
    public void clearWorkout(View view){
        SavedExercises.clearSavedList(this);
        Button save = (Button) findViewById(R.id.saveWorkoutButton);
        Button clear = (Button) findViewById(R.id.clearButton);
        save.setVisibility(View.GONE);
        clear.setVisibility(View.GONE);
        LinearLayout l = (LinearLayout) findViewById(R.id.workoutExerciseList);
        l.removeAllViews();
    }

    //Allows the user to view list of workouts they've already saved
    public void viewSavedWorkouts(View view){
        PopupMenu menu = new PopupMenu(this, view);
        dbHandler = new WorkoutDBHandler(view.getContext(), null, null, 1);
        final ArrayList<Workout> workouts = dbHandler.getWorkouts();

        for(Workout w:workouts) {
            menu.getMenu().add(w.getName());
        }

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String name = item.getTitle().toString();
                for (Workout w : workouts) {
                    if (w.getName().equals(name)) {
                        Log.v("olesy",Integer.toString(w.getExerciseNames().size()));
                        addExerciseButtons(w.getExerciseNames());
                    }
                }
                return true;
            }
        });

        menu.show();
    }

    public void addExerciseButtons(ArrayList<String> exerciseList){
        LinearLayout l = (LinearLayout) findViewById(R.id.workoutExerciseList);
        l.removeAllViews();
        exerciseList.remove("");

        //Dont show the save and clear buttons if there are no exercises to save or clear
        if(exerciseList.size()==0){
            Button save = (Button) findViewById(R.id.saveWorkoutButton);
            Button clear = (Button) findViewById(R.id.clearButton);
            save.setVisibility(View.GONE);
            clear.setVisibility(View.GONE);
        }

        for (final String exerciseName:exerciseList) {
            Button newButton = new Button(this);
            newButton.setText(exerciseName);

            //sends the selected exercise to the exerise viewer on click
            newButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ExerciseViewer.class);
                    intent.putExtra("selected_exercise",exerciseName);
                    startActivity(intent);
                }
            });
            l.addView(newButton);
        }
    }
}
