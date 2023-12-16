package kiu.business.registerboxapp.view.dialog;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import core.model.ticket.ITicket;

public class ShowContentTicketDialog extends DialogFragment {

    public static final String TAG = "Show content ticket";

    private final ITicket ticket;

    public ShowContentTicketDialog(ITicket ticket) {
        this.ticket = ticket;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // TODO put all the text message in string resources

        return new AlertDialog.Builder(requireContext())
                .setTitle("Información del Vale")
                .setMessage(ticket.toString())
                .setNegativeButton("Cerrar", (dialog, which) -> dialog.dismiss())
                .create();
    }
}
