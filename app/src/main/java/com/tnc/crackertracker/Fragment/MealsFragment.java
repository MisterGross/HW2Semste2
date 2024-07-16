package com.tnc.crackertracker.Fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.tnc.crackertracker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MealsFragment extends Fragment {


    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    public MealsFragment() {
        // Required empty public constructor
    }
    private ProgressDialog loadingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_meals, container, false);

        LinearLayout ingredientsContainer = view.findViewById(R.id.ingredients_container);
        AppCompatButton addIngredientsButton = view.findViewById(R.id.add_ingredients);
        AppCompatButton submitButton = view.findViewById(R.id.submit_btn);
        EditText mealNameEditText = view.findViewById(R.id.meal_name);

        addIngredientsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View ingredientView = inflater.inflate(R.layout.ingredient_layout, ingredientsContainer, false);
                ingredientsContainer.addView(ingredientView);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mealName = mealNameEditText.getText().toString().trim();
                if (TextUtils.isEmpty(mealName)) {
                    Toast.makeText(getContext(), "Please enter a meal name", Toast.LENGTH_SHORT).show();
                    return;
                }

                ArrayList<Map<String, String>> ingredientsList = new ArrayList<>();
                for (int i = 0; i < ingredientsContainer.getChildCount(); i++) {
                    View ingredientView = ingredientsContainer.getChildAt(i);
                    EditText foodInput = ingredientView.findViewById(R.id.food_Input);
                    EditText gramInput = ingredientView.findViewById(R.id.Gram_input);

                    String food = foodInput.getText().toString().trim();
                    String grams = gramInput.getText().toString().trim();

                    if (TextUtils.isEmpty(food) || TextUtils.isEmpty(grams)) {
                        Toast.makeText(getContext(), "Please fill all ingredient fields", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    showLoadingIndicator();

                    Map<String, String> ingredient = new HashMap<>();
                    ingredient.put("food", food);
                    ingredient.put("grams", grams);
                    ingredientsList.add(ingredient);
                }

                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    String uid = currentUser.getUid();
                    DatabaseReference userRef = mDatabase.child("users").child(uid).child("meals");

                    Map<String, Object> mealData = new HashMap<>();
                    mealData.put("mealName", mealName);
                    mealData.put("ingredients", ingredientsList);

                    userRef.push().setValue(mealData)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    hideLoadingIndicator();
                                    Toast.makeText(getContext(), "Meal added successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    hideLoadingIndicator();
                                    Toast.makeText(getContext(), "Failed to add meal", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        return view;
    }
    private void showLoadingIndicator() {
        hideLoadingIndicator();
        loadingDialog = new ProgressDialog(getContext());
        loadingDialog.setMessage("Saving Meal...");
        loadingDialog.setCancelable(false);
        loadingDialog.show();
    }

    private void hideLoadingIndicator() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }
}