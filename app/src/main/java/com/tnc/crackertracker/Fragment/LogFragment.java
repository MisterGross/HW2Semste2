package com.tnc.crackertracker.Fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tnc.crackertracker.Model.FoodItem;
import com.tnc.crackertracker.Adapter.MyAdapter;
import com.tnc.crackertracker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class LogFragment extends Fragment {

    private Button Add, SubmitButton;
    private EditText FoodNameInput, GramInput;

    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<FoodItem> itemList;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;
    private ProgressDialog loadingDialog;

    public LogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId).child("foods");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_log, container, false);

        Add = view.findViewById(R.id.add_button);
        SubmitButton = view.findViewById(R.id.submint_button);
        FoodNameInput = view.findViewById(R.id.Food_Input);
        GramInput = view.findViewById(R.id.Gram_input);
        recyclerView = view.findViewById(R.id.selected_foods_recyclerview);
        itemList = new ArrayList<>();

        adapter = new MyAdapter(itemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDataToRecyclerView();
            }
        });

        SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeRecyclerViewDataInDatabase();
            }
        });

        return view;
    }

    private void addDataToRecyclerView() {
        String FoodName = FoodNameInput.getText().toString().trim();
        String Grams = GramInput.getText().toString().trim();

        if (!FoodName.isEmpty() && !Grams.isEmpty()) {
            FoodItem newData = new FoodItem(FoodName, Integer.parseInt(Grams));
            adapter.addData(newData);
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            FoodNameInput.setText("");
            GramInput.setText("");
        } else {
            Toast.makeText(getContext(), "Please enter both Name and Grams", Toast.LENGTH_SHORT).show();
        }
    }

    private void storeRecyclerViewDataInDatabase() {
        showLoadingIndicator();
        for (FoodItem item : itemList) {
//            databaseHelper.addFoodItem(item);
        }
        // Store in Firebase Realtime Database
        if (currentUser != null) {
            String userId = currentUser.getUid();
            for (FoodItem item : itemList) {
                databaseReference.push().setValue(item);
            }
        }
        hideLoadingIndicator();
        Toast.makeText(getContext(), "Data stored in database", Toast.LENGTH_SHORT).show();
    }


    private void showLoadingIndicator() {
        hideLoadingIndicator();
        loadingDialog = new ProgressDialog(getContext());
        loadingDialog.setMessage("Saving Data...");
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
