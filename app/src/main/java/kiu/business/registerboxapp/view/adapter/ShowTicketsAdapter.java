package kiu.business.registerboxapp.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import core.model.ticket.ITicket;
import kiu.business.registerboxapp.R;
import kiu.business.registerboxapp.databinding.FragmentShowTicketsListBinding;
import kiu.business.registerboxapp.databinding.TicketItemLayoutBinding;
import kiu.business.registerboxapp.view.holder.TicketItemHolder;
import ticket.controller.TicketManager;

public class ShowTicketsAdapter extends RecyclerView.Adapter<TicketItemHolder> {

    public final int PAYED = 1;
    public final int NO_PAYED = 0;

    private final List<ITicket> tickets;
    private final FragmentManager fragmentManager;
    private final FragmentShowTicketsListBinding binding;

    public ShowTicketsAdapter(FragmentShowTicketsListBinding binding, FragmentManager fragmentManager) {
        this.binding = binding;
        this.fragmentManager = fragmentManager;
        tickets = TicketManager.getInstance().getTickets();
    }

    @Override
    public int getItemViewType(int position) {
        ITicket ticket = tickets.get(position);
        return ticket.getCash() >= ticket.getPrice() ? PAYED : NO_PAYED;
    }

    @NonNull
    @Override
    public TicketItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        TicketItemLayoutBinding binding = TicketItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false);
        if (viewType == NO_PAYED)
            binding.getRoot().setBackground(AppCompatResources.getDrawable(context, R.drawable.item_ticket_not_pay));
        return new TicketItemHolder(binding, fragmentManager);
    }

    @Override
    public void onBindViewHolder(final TicketItemHolder holder, int position) {
        holder.fillHolder(binding, tickets.get(position));
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }


}