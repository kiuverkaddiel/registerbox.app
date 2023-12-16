package kiu.business.registerboxapp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import core.controller.UserManager;
import core.model.Session;
import core.model.User;
import ips.model.Ips;
import kiu.business.registerboxapp.R;
import product.model.ProductList;
import ticket.controller.TicketManager;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onResume() {
        super.onResume();

        EditText userName = findViewById(R.id.editTextUserName);
        EditText password = findViewById(R.id.editTextPassword);

        findViewById(R.id.buttonLogin).setOnClickListener(v -> {
            User user = UserManager.getInstance().getUsers().get(
                    userName.getText().toString()
//                    "admin"
            );

            if (user != null && user.checkPassword(password.getText().toString())) {
                Session.getInstance().open(user);
                Ips.getInstance().setOpenBy(user);
                TicketManager.getInstance().createTicket(new ProductList());
                Intent workingArea = new Intent();
                workingArea.setClass(this, WorkingAreaActivity.class);
                userName.setEnabled(false);
                password.setEnabled(false);
                v.setEnabled(false);
                userName.getText().clear();
                password.getText().clear();
                startActivity(workingArea);
            } else {
                String text = getResources().getString(R.string.wrong_user_or_password_text);
                Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }
}