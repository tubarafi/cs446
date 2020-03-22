package com.example.refresh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.refresh.database.AppDatabase;
import com.example.refresh.database.model.ShopItem;

public class AddShopItemActivity extends AppCompatActivity {

    private EditText nameEditText, quantityEditText;
    private Button createButton, cancelButton;

    private AppDatabase db;
    private ShopItem shopItem;
    private boolean update = false;
    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shop_item);
        nameEditText = findViewById(R.id.shopNameEditText);
        quantityEditText = findViewById(R.id.quantityEditText);
        createButton = findViewById(R.id.createButton);
        cancelButton = findViewById(R.id.cancelButton);
        db = AppDatabase.getAppDatabase(AddShopItemActivity.this);
        pos = getIntent().getIntExtra("position", -1);
        if ((shopItem = (ShopItem) getIntent().getSerializableExtra("shop_item")) != null) {
            getSupportActionBar().setTitle("Update Shop Item");
            update = true;

            createButton.setText("Update");
            nameEditText.setText(shopItem.getName());
            quantityEditText.setText(String.valueOf(shopItem.getQuantity()));
        } else {
            getSupportActionBar().setTitle("Create Shop Item");
        }
        createButton.setOnClickListener(view -> {
            Context context = getApplicationContext();
            String itemName = nameEditText.getText().toString();
            String itemQuantity = quantityEditText.getText().toString();
            if (itemName.equals("")) {
                Toast.makeText(context, "Please enter an item name.", Toast.LENGTH_LONG).show();
            } else if (itemQuantity.equals("")) {
                Toast.makeText(context, "Please enter a quantity.", Toast.LENGTH_LONG).show();
            } else {
                if (update) {
                    shopItem.setName(nameEditText.getText().toString());
                    shopItem.setQuantity(Integer.parseInt(quantityEditText.getText().toString()));
                    try {
                        db.shopItemDAO().update(shopItem);
                        setResult(shopItem, 2); // update
                        Toast.makeText(context, "Updated " + shopItem.getName() + ".", Toast.LENGTH_LONG).show();
                    } catch (Exception ex) {
                        Log.e("Update Shop failed", ex.getMessage() != null ? ex.getMessage() : "");
                    }
                } else {
                    shopItem = new ShopItem(nameEditText.getText().toString(), Integer.parseInt(quantityEditText.getText().toString()));
                    try {
                        db.shopItemDAO().insert(shopItem);
                        setResult(shopItem, 1); //create
                        Toast.makeText(context, "Added " + shopItem.getName() + " to shopping list.", Toast.LENGTH_LONG).show();
                    } catch (Exception ex) {
                        Log.e("Add Shop failed", ex.getMessage() != null ? ex.getMessage() : "");
                    }
                }
            }
        });

        cancelButton.setOnClickListener(view -> finish());
    }

    private void setResult(ShopItem shop, int flag) {
        setResult(flag, new Intent().putExtra("shop_item", shop).putExtra("position", pos));
        finish();
    }
}
