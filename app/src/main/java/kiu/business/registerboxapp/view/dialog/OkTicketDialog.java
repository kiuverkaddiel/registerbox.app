package kiu.business.registerboxapp.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import kiu.business.registerboxapp.view.activity.NotifierCurrentTicketChange;
import product.model.ProductList;
import ticket.controller.TicketManager;

public class OkTicketDialog extends DialogFragment {

    public static final String TAG = "Ok ticket";

    private NotifierCurrentTicketChange notifierCurrentTicketChange;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Fragment parent = getParentFragment();
        if (parent instanceof NotifierCurrentTicketChange)
            notifierCurrentTicketChange = (NotifierCurrentTicketChange) parent;

        // TODO put all the text message in string resources

        return new AlertDialog.Builder(requireContext())
                .setTitle("Cerrar vale")
                .setMessage("Está seguro que desea cerrar el vale?")
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Si", (dialog, which) -> {

                    TicketManager tm = TicketManager.getInstance();
                    String message = "No se pudo cerrar el vale";

                    if (tm.saveTicket()) {
                        tm.createTicket(new ProductList());
                        if (notifierCurrentTicketChange != null)
                            notifierCurrentTicketChange.notifyAllTicketChange();
                        message = "Vale cerrado.";
                    }

                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();

                    dialog.dismiss();
                })
                .create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof NotifierCurrentTicketChange)
            notifierCurrentTicketChange = (NotifierCurrentTicketChange) context;
    }
}
