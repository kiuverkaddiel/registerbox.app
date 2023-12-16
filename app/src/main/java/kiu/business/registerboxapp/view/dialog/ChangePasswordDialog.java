package kiu.business.registerboxapp.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import core.controller.UserManager;
import core.model.Session;
import core.model.User;
import kiu.business.registerboxapp.R;
import kiu.business.registerboxapp.databinding.ChangePasswordDialogBinding;

public class ChangePasswordDialog extends DialogFragment {

    public static final String TAG = "change password";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        ChangePasswordDialogBinding binding =
                ChangePasswordDialogBinding.inflate(getLayoutInflater());

        User userSession = Session.getInstance().getUser();
        String title = getString(R.string.change_password);
        title += (" (" + userSession.getUserName() + ")");

        // TODO extract string

        return new AlertDialog.Builder(getContext())
                .setView(binding.getRoot())
                .setTitle(title)
                .setNegativeButton(R.string.cancel_text, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.ok_text, (dialog, which) -> {

                    String message = "Contraseña incorrecta.";

                    String currentPassword = binding.editTextCurrentPassword.getText().toString();
                    if (Session.getInstance().getUser().checkPassword(currentPassword)) {
                        String newPassword = binding.editTextNewPassword.getText().toString();
                        String repeatPassword = binding.editTextTextRepeatPassword.getText().toString();
                        if (newPassword.equals(repeatPassword)) {
                            userSession.changePassword(newPassword);
                            UserManager.getInstance().addUser(userSession);
                            message = "Contraseña actualizada.";
                        } else
                            message = "Nueva contraseña incorrecta.";
                    }

                    // TODO extract text
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();

                    dialog.dismiss();

                }).create();
    }
}
