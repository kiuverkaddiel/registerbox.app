package kiu.business.registerboxapp.view.holder;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import core.model.product.IProduct;
import kiu.business.registerboxapp.R;
import kiu.business.registerboxapp.view.listener.OnclickTicketProductListItem;

public class TicketProductListItemHolder extends RecyclerView.ViewHolder {

    private final TextView tvProductName;
    private final TextView tvProductPriceAndCount;
    private final TextView tvProductId;
    private final LinearLayout linearLayoutProductItem;

    private int productPos;

    @SuppressLint("ResourceType")
    public TicketProductListItemHolder(@NonNull View itemView) {
        super(itemView);

        tvProductName = itemView.findViewById(R.id.textViewProductName);
        tvProductPriceAndCount = itemView.findViewById(R.id.textViewProductPriceAndCount);
        tvProductId = itemView.findViewById(R.id.textViewProductId);
        linearLayoutProductItem = itemView.findViewById(R.id.linearLayoutProductItem);
    }

    public void fillHolder(OnclickTicketProductListItem onclickTicketProductListItem) {

        IProduct product = onclickTicketProductListItem.getProduct();
        int count = onclickTicketProductListItem.getProductCount();

        tvProductName.setText(product.getName());
        tvProductPriceAndCount.setText(product.getPrice() + "$x" + count);
        tvProductId.setText(product.getProductId());

        linearLayoutProductItem.setOnClickListener(onclickTicketProductListItem);
    }
}
