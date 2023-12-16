package kiu.business.registerboxapp.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import kiu.business.registerboxapp.R;
import kiu.business.registerboxapp.view.activity.NotifierCurrentTicketChange;
import ticket.controller.TicketManager;
import ticket.model.Ticket;

public class PutCashDialog extends DialogFragment {

    public static final String TAG = "put cash dialog";

    private NotifierCurrentTicketChange notifierCurrentTicketChange;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = getLayoutInflater().inflate(R.layout.add_cash_dialog, null, false);

        Fragment parent = getParentFragment();
        if (parent instanceof NotifierCurrentTicketChange)
            notifierCurrentTicketChange = (NotifierCurrentTicketChange) parent;

        assert view != null;
        final Ticket currentTicket = TicketManager.getInstance().getCurrentTicket();

        final EditText etCash = view.findViewById(R.id.editTextPutProductId);
        float cash = currentTicket.getCash();
        if (cash != 0f)
            etCash.setText(String.valueOf(cash));

        final EditText etInfo = view.findViewById(R.id.editTextTicketInfo);
        etInfo.setOnClickListener(v -> {
            v.setFocusableInTouchMode(true);
            v.requestFocus();
        });
        String info = currentTicket.getInfo();
        if (info != null && !info.isEmpty())
            etInfo.setText(info);

        final ImageButton imPayOk = view.findViewById(R.id.imageButtonPayOk);
        imPayOk.setOnClickListener(v -> etCash.setText(String.valueOf(currentTicket.getPrice())));

        return new AlertDialog.Builder(getContext())
                .setView(view)
                .setTitle(R.string.put_ticket_cash)
                .setNegativeButton(R.string.cancel_text, (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton(R.string.ok_text, (dialog, which) -> {

                    String strCash = etCash.getText().toString();
                    if (strCash.isEmpty()) {
                        currentTicket.setCash(0f);
                    } else {
                        currentTicket.setCash(Float.parseFloat(strCash));
                    }

                    currentTicket.setInfo(etInfo.getText().toString());

                    if (notifierCurrentTicketChange != null)
                        notifierCurrentTicketChange.ticketPutCash();

                    dialog.dismiss();
                }).create();
    }
}
