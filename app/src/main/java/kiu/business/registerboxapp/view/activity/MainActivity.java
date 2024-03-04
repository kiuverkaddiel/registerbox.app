package kiu.business.registerboxapp.view.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.security.NoSuchAlgorithmException;

import core.controller.UserManager;
import core.model.Role;
import core.model.Session;
import core.model.TicketStatus;
import core.model.User;
import core.model.ticket.ITicket;
import ips.model.Ips;
import kiu.business.registerboxapp.R;
import kiu.business.registerboxapp.db.AppDb;
import kiu.business.registerboxapp.task.SleepyTask;
import product.model.Product;
import product.model.ProductList;
import ticket.controller.TicketManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    class MySleepyTask extends SleepyTask {
        public MySleepyTask(long milliseconds) {
            super(milliseconds);
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            Intent Login = new Intent();
            Login.setClass(MainActivity.this, LoginActivity.class);
            startActivity(Login);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        AppDb db = new AppDb(this);
        UserManager.initializer(db);
        Session.initializer(db);
        Ips.initializer(db, db);
        TicketManager.initializer(db);

        TicketManager tm = TicketManager.getInstance();
        Ips ips = Ips.getInstance();

        ips.fillProductList(tm.getTickets());
        tm.attachTicketObserver(ips);

//        insert(db);

        MySleepyTask m = new MySleepyTask(500);
        m.execute();
    }

    private void insert(AppDb db) {
//        try {
//            db.save(new User("kaylet", "Kaylet", Role.EMPLOYEE, true, null));
//            db.save(new User("eddy", "Eddy", Role.EMPLOYEE, true, null));
//            db.save(new User("misley", "Misley", Role.ADMIN, true, null));
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
    }
}