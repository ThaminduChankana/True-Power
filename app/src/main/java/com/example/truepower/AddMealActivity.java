package com.example.truepower;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AddMealActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button add;
    private DatabaseReference meal;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);

        final Spinner mealCat = (Spinner) findViewById(R.id.spinner_meal_category);
        final Spinner mealType = (Spinner) findViewById(R.id.spinner_meal_type);

        mealCat.setOnItemSelectedListener(this);
        mealType.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> mealCategories = new ArrayList<String>();
        mealCategories.add("Meal Category");
        mealCategories.add("Main");
        mealCategories.add("Snack");
        mealCategories.add("Soup");
        mealCategories.add("Dessert");
        mealCategories.add("Drink");
        mealCategories.add("Salad");
        mealCategories.add("Meat");
        mealCategories.add("Other");

        List<String> mealTypes = new ArrayList<String>();
        mealTypes.add("Meal Type");
        mealTypes.add("Breakfast");
        mealTypes.add("Lunch");
        mealTypes.add("Brunch");
        mealTypes.add("Dinner");
        mealTypes.add("Other");

        // Creating adapter for spinner
        ArrayAdapter<String> mealCatDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mealCategories);
        ArrayAdapter<String> mealTypeDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mealTypes);

        // Drop down layout style - list view with radio button
        mealCatDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealCat.setAdapter(mealCatDataAdapter);

        mealTypeDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealType.setAdapter(mealTypeDataAdapter);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_home);
        bottomNavigationView.setSelectedItemId(R.id.meal);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {


            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.health:
                        startActivity(new Intent(getApplicationContext(), HealthActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.meal:
                        startActivity(new Intent(getApplicationContext(), MealActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.routine:
                        startActivity(new Intent(getApplicationContext(), RoutineActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.workout:
                        startActivity(new Intent(getApplicationContext(), WorkoutActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                }

                return false;
            }
        });

        auth=FirebaseAuth.getInstance();
        meal= FirebaseDatabase.getInstance().getReference().child("meal").child(auth.getCurrentUser().getUid());
        add=findViewById(R.id.btn_meal_submit);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMeal();
            }


        });


    }

    private void addMeal(){
        final EditText mealName = findViewById(R.id.et_meal_name);
        final Spinner mealCategory =  findViewById(R.id.spinner_meal_category);
        final Spinner mealType = findViewById((R.id.spinner_meal_type));
        final EditText calories = findViewById(R.id.et_meal_calories);

        String m_name = mealName.getText().toString();
        String m_category = mealCategory.getSelectedItem().toString();
        String m_type = mealType.getSelectedItem().toString();
        String m_calories = calories.getText().toString();

        if(TextUtils.isEmpty(m_name)){
            Toast.makeText(AddMealActivity.this,"Meal name is required !",Toast.LENGTH_SHORT).show();
        }
        if(m_category.equals("Meal Category")){
            Toast.makeText(AddMealActivity.this,"Select a valid Meal Category",Toast.LENGTH_SHORT).show();
        }
        if(m_type.equals("Meal Type")){
            Toast.makeText(AddMealActivity.this,"Select a valid Meal Type",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(m_calories)){
            mealName.setError("Calorie count is required !");
        }
        else{
            String id =meal.push().getKey();
            Meal newMeal = new Meal(id,m_name,m_category,m_type,m_calories);
            meal.child(id).setValue(newMeal).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        try {
                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                            r.play();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(AddMealActivity.this,"Meal added successfully",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddMealActivity.this,MealActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(AddMealActivity.this,"Error",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }


    public void backToMyMeal(View view) {
        Intent intent = new Intent(this, MealActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}