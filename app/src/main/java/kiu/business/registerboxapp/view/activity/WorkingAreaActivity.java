package kiu.business.registerboxapp.view.activity;

import static android.os.Environment.getExternalStorageDirectory;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.datatransport.runtime.BuildConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import core.model.Role;
import core.model.Session;
import core.model.product.IProduct;
import ips.model.Ips;
import kiu.business.registerboxapp.R;
import kiu.business.registerboxapp.databinding.ActivityWorkingAreaBinding;
import kiu.business.registerboxapp.tools.DateHelper;
import kiu.business.registerboxapp.view.dialog.ChangePasswordDialog;
import ticket.controller.TicketManager;

public class WorkingAreaActivity extends AppCompatActivity {

    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private static final int PERMISSION_CODE = 1001;
    private AppBarConfiguration appBarConfiguration;

    private final String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        kiu.business.registerboxapp.databinding.ActivityWorkingAreaBinding binding = ActivityWorkingAreaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_working_area);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        requestAppPermissions();
    }

    private void requestAppPermissions() {
        List<String> permissionDenied = new ArrayList<>();

        for (String permission : permissions) {
            if (checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                permissionDenied.add(permission);
            }
        }

        if (!permissionDenied.isEmpty()) {
            requestPermissions(permissionDenied.toArray(new String[0]), 123);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_working_area);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        if (checkSelfPermission(CAMERA_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{CAMERA_PERMISSION}, PERMISSION_CODE);
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        for (int r : grantResults) {
            if (r == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

        if (id == R.id.close_session_menu) {
            // FIXME extract all text from string resource

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Cerrar sesión")
                    .setMessage("̉Seguro que desea cerrar la sesión?")
                    .setNegativeButton("CANCELAR", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Ok", (dialog, which) -> {
                        closeSession();
                        dialog.dismiss();
                    });
            builder.create().show();
            return true;
        }

        if (id == R.id.export_products) {

            HashMap<IProduct, Integer> products = Ips.getInstance().getOriginProductList().getProducts();
            Set<IProduct> productsKey = products.keySet();
            StringBuilder csv = new StringBuilder();

            String fileName = getDate() + "_products.csv";
            String path = getExternalStorageDirectory().getPath() + File.separator + fileName;

            for (IProduct product : productsKey) {
                Integer countProduct = products.get(product);
                csv.append(product.getProductId())
                        .append(",").append(product.getName())
                        .append(",").append(product.getPrice())
                        .append(",").append(product.isOwnProduct())
                        .append(",").append(countProduct == null ? 0 : countProduct)
                        .append("\n");
            }

            String message = "Error al salvar: " + path;
            try (FileOutputStream fos = new FileOutputStream(path)) {
                fos.write(csv.toString().getBytes(StandardCharsets.UTF_8));
                fos.flush();
                message = "Salvado en: " + path;
            } catch (IOException ignored) {
            }

            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            return true;
        }

        if (id == R.id.export_ips) {
            String fileName = getDate() + "_ipv.csv";
            String path = getExternalStorageDirectory().getPath() + File.separator + fileName;
            String message = "Error al salvar: " + path;

            if (exportIps(path))
                message = "Salvado en: " + path;

            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            return true;
        }

        if (id == R.id.lock_session_menu) {
            lockSession();
            return true;
        }

        if (id == R.id.manager_ips) {
            Navigation.findNavController(this, R.id.nav_host_fragment_content_working_area)
                    .navigate(R.id.action_WorkingAreaFragment_to_ManagerIpsFragment);
            return true;
        }

        if (id == R.id.show_tickets) {
            Navigation.findNavController(this, R.id.nav_host_fragment_content_working_area)
                    .navigate(R.id.action_WorkingAreaFragment_to_ShowTicketListFragment);
            return true;
        }

        if (id == R.id.show_ips) {
            Navigation.findNavController(this, R.id.nav_host_fragment_content_working_area)
                    .navigate(R.id.action_WorkingAreaFragment_to_showIpsFragment);
            return true;
        }

        if (id == R.id.change_password) {
            ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog();
            changePasswordDialog.show(getSupportFragmentManager(), ChangePasswordDialog.TAG);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String getDate() {
        return DateHelper.getDate(
                DateHelper.DD,
                DateHelper.MM,
                DateHelper.YY,
                DateHelper.MINUS,
                DateHelper.hh,
                DateHelper.mm,
                DateHelper.ss);
    }

    private boolean exportIps(String path) {

        String csv = Ips.getInstance().getCsv();

        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(csv.getBytes(StandardCharsets.UTF_8));
            fos.flush();
            return true;
        } catch (IOException e) {
            return false;
        }
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