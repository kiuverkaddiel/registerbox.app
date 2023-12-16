package kiu.business.registerboxapp.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import core.model.product.IProduct;
import core.model.ticket.ITicket;
import kiu.business.registerboxapp.R;
import kiu.business.registerboxapp.view.holder.TicketProductListItemHolder;
import kiu.business.registerboxapp.view.listener.OnclickTicketProductListItem;
import ticket.controller.TicketManager;
import ticket.model.Ticket;

public class TicketProductListAdapter extends RecyclerView.Adapter<TicketProductListItemHolder> {

    private final FragmentManager fragmentManager;
    private final List<IProduct> products;
    private final Ticket ticket;

    public TicketProductListAdapter(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        this.ticket = TicketManager.getInstance().getCurrentTicket();
        this.products = new ArrayList<>(ticket.getProductList().getProducts().keySet());
    }

    @NonNull
    @Override
    public TicketProductListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        int layoutId = R.layout.product_item_layout;

        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        view.setBackgroundResource(R.drawable.item_ticket);
        return new TicketProductListItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketProductListItemHolder holder, int position) {
        IProduct item = products.get(position);
        int count = ticket.getProductList().getProducts().getOrDefault(item, 0);
        holder.fillHolder(new OnclickTicketProductListItem(fragmentManager, item, position, count));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
