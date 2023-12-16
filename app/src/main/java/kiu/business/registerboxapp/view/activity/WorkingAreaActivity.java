package kiu.business.registerboxapp.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import core.model.Role;
import core.model.Session;
import ips.model.Ips;
import kiu.business.registerboxapp.R;
import kiu.business.registerboxapp.databinding.ActivityWorkingAreaBinding;
import kiu.business.registerboxapp.view.dialog.ChangePasswordDialog;
import ticket.controller.TicketManager;

public class WorkingAreaActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityWorkingAreaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityWorkingAreaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_working_area);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_working_area);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        // Do nothing.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (Session.getInstance().getUser().getRole().equals(Role.ADMIN))
            getMenuInflater().inflate(R.menu.admin_menu, menu);
        else
            getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        // FIXME check for the action in navigation bar

        switch (id) {
            case R.id.close_session_menu: {
                // FIXME extract all text from string resource

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Cerrar sesión")
                        .setMessage("̉Seguro que desea cerrar la sesión?")
                        .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                closeSession();
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
                return true;
            }
            case R.id.lock_session_menu: {
                lockSession();
                return true;
            }
            case R.id.manager_user: {
                Navigation.findNavController(this, R.id.nav_host_fragment_content_working_area)
                        .navigate(R.id.action_WorkingAreaFragment_to_AdminUserFragment);
                return true;
            }
            case R.id.manager_ips: {
                Navigation.findNavController(this, R.id.nav_host_fragment_content_working_area)
                        .navigate(R.id.action_WorkingAreaFragment_to_ManagerIpsFragment);
                return true;
            }
            case R.id.show_tickets: {
                Navigation.findNavController(this, R.id.nav_host_fragment_content_working_area)
                        .navigate(R.id.action_WorkingAreaFragment_to_ShowTicketListFragment);
                return true;
            }
            case R.id.show_ips: {
                Navigation.findNavController(this, R.id.nav_host_fragment_content_working_area)
                        .navigate(R.id.action_WorkingAreaFragment_to_showIpsFragment);
                return true;
            }
            case R.id.change_password: {
                ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog();
                changePasswordDialog.show(getSupportFragmentManager(), ChangePasswordDialog.TAG);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

        Session session = Session.getInstance();

        if (!session.isEnable())
            lockSession();
        else
            session.update();
    }

    private void lockSession() {
        Intent login = new Intent();
        login.setClass(this, LoginActivity.class);
        startActivity(login);
    }

    private void closeSession() {
        Session.getInstance().close();
        Ips.getInstance().close();
        TicketManager.getInstance().close();
        lockSession();
    }
}