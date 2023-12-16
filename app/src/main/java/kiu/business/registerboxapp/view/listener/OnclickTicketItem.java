package kiu.business.registerboxapp.view.listener;

import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import core.model.ticket.ITicket;
import kiu.business.registerboxapp.databinding.FragmentShowTicketsListBinding;
import kiu.business.registerboxapp.view.dialog.ShowContentTicketDialog;

public class OnclickTicketItem implements View.OnClickListener {
    private final FragmentManager fragmentManager;
    private final ITicket ticket;
    private final TextView ticketContent;

    public OnclickTicketItem(FragmentShowTicketsListBinding binding, FragmentManager fragmentManager, ITicket ticket) {
        ticketContent = binding.textViewTicketContent;
        this.fragmentManager = fragmentManager;
        this.ticket = ticket;
    }

    @Override
    public void onClick(View v) {
        if (ticketContent.getVisibility() == View.GONE) {
            ShowContentTicketDialog p = new ShowContentTicketDialog(ticket);
            p.show(fragmentManager, ShowContentTicketDialog.TAG);
        } else {
            ticketContent.setText(ticket.toString());
        }
    }
}
