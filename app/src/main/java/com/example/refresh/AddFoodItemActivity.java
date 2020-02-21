package com.example.refresh;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.refresh.database.AppDatabase;
import com.example.refresh.database.model.FoodItem;

import java.util.Calendar;

public class AddFoodItemActivity extends AppCompatActivity {

    private EditText nameEditText, quantityEditText, remindDateEditText, noteEditText;
    private Button createButton, cancelButton;
    private DatePickerDialog picker;

    private AppDatabase db;
    private FoodItem foodItem;
    private boolean update = false;
    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_item);
        nameEditText = findViewById(R.id.foodNameEditText);
        quantityEditText = findViewById(R.id.quantityEditText);
        remindDateEditText = findViewById(R.id.remindDateEditText);
        remindDateEditText.setInputType(InputType.TYPE_NULL);
        noteEditText = findViewById(R.id.noteEditText);
        createButton = findViewById(R.id.createButton);
        cancelButton = findViewById(R.id.cancelButton);
        db = AppDatabase.getAppDatabase(AddFoodItemActivity.this);
        pos = getIntent().getIntExtra("position", -1);

        if ((foodItem = (FoodItem) getIntent().getSerializableExtra("food_item")) != null) {
            getSupportActionBar().setTitle("Update Food Item");
            update = true;

            createButton.setText("Update");
            nameEditText.setText(foodItem.getName());
            quantityEditText.setText(String.valueOf(foodItem.getQuantity()));
            remindDateEditText.setText(foodItem.getRemindMeOnDate());
            noteEditText.setText(foodItem.getNote());
        } else {
            getSupportActionBar().setTitle("Create Food Item");
        }

        remindDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                picker = new DatePickerDialog(AddFoodItemActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        remindDateEditText.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (update) {
                    foodItem.setName(nameEditText.getText().toString());
                    foodItem.setQuantity(Integer.valueOf(quantityEditText.getText().toString()));
                    //TODO: need to change to datetime picker in "activity_add_food_item.xml, implement related callbacks"
                    foodItem.setRemindMeOnDate(remindDateEditText.getText().toString());
                    foodItem.setNote(noteEditText.getText().toString());

                    try {
                        db.foodItemDAO().update(foodItem);
                        setResult(foodItem, 2); // update
                    } catch (Exception ex) {
                        Log.e("Update Food failed", ex.getMessage());
                    }
                } else {
                    foodItem = new FoodItem(nameEditText.getText().toString(), remindDateEditText.getText().toString(), Integer.valueOf(quantityEditText.getText().toString()), noteEditText.getText().toString());
                    try {
                        db.foodItemDAO().insert(foodItem);
                        setResult(foodItem, 1); //create
                    } catch (Exception ex) {
                        Log.e("Add Food failed", ex.getMessage());
                    }
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setResult(FoodItem food, int flag) {
        setResult(flag, new Intent().putExtra("food_item", food).putExtra("position", pos));
        finish();
    }
}
