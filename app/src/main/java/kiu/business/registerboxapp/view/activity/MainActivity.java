package kiu.business.registerboxapp.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.security.NoSuchAlgorithmException;

import core.controller.UserManager;
import core.model.Role;
import core.model.Session;
import core.model.User;
import ips.model.Ips;
import kiu.business.registerboxapp.R;
import kiu.business.registerboxapp.db.AppDb;
import kiu.business.registerboxapp.task.SleepyTask;
import ticket.controller.TicketManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private static class MySleepyTask extends SleepyTask {
        private final Context context;
        public MySleepyTask(long milliseconds, Context context) {
            super(milliseconds);
            this.context = context;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            Intent Login = new Intent();
            Login.setClass(context, LoginActivity.class);
            context.startActivity(Login);
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

        MySleepyTask m = new MySleepyTask(500, this);
        m.execute();
    }
}