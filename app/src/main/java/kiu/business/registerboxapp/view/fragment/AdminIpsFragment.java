package kiu.business.registerboxapp.view.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import core.model.product.IProduct;
import ips.model.Ips;
import kiu.business.registerboxapp.databinding.FragmentAdminIpsBinding;
import kiu.business.registerboxapp.view.adapter.ProductListAdapter;
import kiu.business.registerboxapp.view.dialog.DialogNotifierProductChange;
import kiu.business.registerboxapp.view.dialog.EditProductListDialog;

public class AdminIpsFragment extends Fragment implements DialogNotifierProductChange {

    private FragmentAdminIpsBinding binding;
    private final Ips ips = Ips.getInstance();

    private EditText editTextCash;
    private EditText editTextInfo;

    private List<IProduct> products;
    private ProductListAdapter productListAdapter;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentAdminIpsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextCash = binding.editTextCash;
        editTextInfo = binding.editTextInfo;

        products = new ArrayList<>(ips.getOriginProductList().getProducts().keySet());
        productListAdapter = new ProductListAdapter(getChildFragmentManager(), products);

        RecyclerView rv = binding.rvProductList;
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(productListAdapter);

        loadData();
        editTextCash.addTextChangedListener(new TextWatchListener());
        editTextInfo.addTextChangedListener(new TextWatchListener());

        binding.btCancel.setOnClickListener(v ->
                binding.linearLayoutButtons.setVisibility(View.GONE));

        binding.floatingButton.setOnClickListener(v ->
                new EditProductListDialog(null, 0, products.size()).show(getChildFragmentManager(),
                EditProductListDialog.TAG));

        binding.btSave.setOnClickListener(v -> {
            String strCash = editTextCash.getText().toString();
            if (!strCash.isEmpty()) {
                float cash = Float.parseFloat(strCash);
                ips.setCash(cash);
            }

            String info = editTextInfo.getText().toString();
            if (!info.isEmpty()) {
                ips.setInfo(info);
            }

            boolean saved = ips.save();

            String message = "No se pudo salvar!";
            if (saved) {
                message = "Salvado correctamente!";
                loadData();
                binding.linearLayoutButtons.setVisibility(View.GONE);
            }

            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

        });

    }

    private void loadData() {
        binding.textViewDate.setText(ips.getDate().toString());
        binding.textViewCreatedBy.setText(ips.getCreatedBy().getFullName());

        if (ips.getCash() != 0)
            editTextCash.setText(String.valueOf(ips.getCash()));

        if (!ips.getInfo().isEmpty())
            editTextInfo.setText(ips.getInfo());
    }

    @Override
    public void notifyProductChangeAt(int pos) {
        productListAdapter.notifyItemChanged(pos);
        binding.textViewDate.setText(ips.getDate().toString());
        binding.textViewCreatedBy.setText(ips.getCreatedBy().getFullName());
        // TODO extract text
        Toast.makeText(getContext(), "Producto modificado", Toast.LENGTH_LONG).show();
    }

    @Override
    public void notifyProductInsertedAt(IProduct product, int pos) {
        products.add(product);
        productListAdapter.notifyItemInserted(pos);
        binding.textViewDate.setText(ips.getDate().toString());
        binding.textViewCreatedBy.setText(ips.getCreatedBy().getFullName());
        // TODO extract text
        Toast.makeText(getContext(), "Producto insertado", Toast.LENGTH_LONG).show();
    }

    class TextWatchListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            binding.linearLayoutButtons.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}