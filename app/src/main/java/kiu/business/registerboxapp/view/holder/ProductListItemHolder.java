package kiu.business.registerboxapp.view.holder;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import core.model.product.IProduct;
import kiu.business.registerboxapp.R;
import kiu.business.registerboxapp.view.listener.OnclickProductItem;

public class ProductListItemHolder extends RecyclerView.ViewHolder {

    private final TextView tvProductName;
    private final TextView tvProductPriceAndCount;
    private final TextView tvProductId;
    private final LinearLayout linearLayoutProductItem;

    @SuppressLint("ResourceType")
    public ProductListItemHolder(@NonNull View itemView) {
        super(itemView);

        tvProductName = itemView.findViewById(R.id.textViewProductName);
        tvProductPriceAndCount = itemView.findViewById(R.id.textViewProductPriceAndCount);
        tvProductId = itemView.findViewById(R.id.textViewProductId);
        linearLayoutProductItem = itemView.findViewById(R.id.linearLayoutProductItem);
    }

    public void fillHolder(OnclickProductItem onclickProductItem) {
        IProduct product = onclickProductItem.getProduct();
        int count = onclickProductItem.getCount();
        tvProductName.setText(product.getName());
        tvProductPriceAndCount.setText(product.getPrice() + "$x" + count);
        tvProductId.setText(product.getProductId());
        linearLayoutProductItem.setOnClickListener(onclickProductItem);
    }
}
