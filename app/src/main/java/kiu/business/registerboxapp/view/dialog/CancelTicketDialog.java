package kiu.business.registerboxapp.view.dialog;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import ips.model.Ips;
import kiu.business.registerboxapp.view.activity.NotifierCurrentTicketChange;
import ticket.controller.TicketManager;

public class CancelTicketDialog extends DialogFragment {

    public static final String TAG = "Cancel ticket";

    private NotifierCurrentTicketChange notifierCurrentTicketChange;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Fragment parent = getParentFragment();
        if (parent instanceof NotifierCurrentTicketChange)
            notifierCurrentTicketChange = (NotifierCurrentTicketChange) parent;

        // TODO put all the text message in string resources

        return new AlertDialog.Builder(requireContext())
                .setTitle("Cancelar vale")
                .setMessage("Está seguro que desea cancelar el vale?")
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Si", (dialog, which) -> {

                    Ips.getInstance().getCurrentProductList().addProductsToList(
                            TicketManager.getInstance().getCurrentTicket().getProductList()
                    );

                    TicketManager.getInstance().cancelTicket();

                    if (notifierCurrentTicketChange != null)
                        notifierCurrentTicketChange.notifyAllTicketChange();

                    dialog.dismiss();
                })
                .create();
    }
}
