package kiu.business.registerboxapp.view.holder;

import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import core.model.ticket.ITicket;
import kiu.business.registerboxapp.databinding.FragmentShowTicketsListBinding;
import kiu.business.registerboxapp.databinding.TicketItemLayoutBinding;
import kiu.business.registerboxapp.view.listener.OnclickTicketItem;

public class TicketItemHolder extends RecyclerView.ViewHolder {
    private final TextView tvDate;
    private final TextView tvUser;
    private final View root;
    private final FragmentManager fragmentManager;

    public TicketItemHolder(TicketItemLayoutBinding binding, FragmentManager fragmentManager) {
        super(binding.getRoot());

        root = binding.getRoot();
        tvDate = binding.textViewTicketDate;
        tvUser = binding.textViewTicketUser;
        this.fragmentManager = fragmentManager;
    }

    public void fillHolder(FragmentShowTicketsListBinding binding, ITicket ticket) {
        tvDate.setText(ticket.getCreatedDate().toString());
        tvUser.setText(ticket.getUser().getFullName());
        root.setOnClickListener(new OnclickTicketItem(binding, fragmentManager, ticket));
    }
}
