package kiu.business.registerboxapp.view.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import core.model.product.IProduct;
import core.model.product.ProductObserver;
import ips.model.Ips;
import kiu.business.registerboxapp.R;
import kiu.business.registerboxapp.databinding.FragmentWorkingAreaBinding;
import kiu.business.registerboxapp.view.MyBarcodeDetector;
import kiu.business.registerboxapp.view.activity.NotifierCurrentTicketChange;
import kiu.business.registerboxapp.view.activity.NotifierIpsProductListChange;
import kiu.business.registerboxapp.view.adapter.IpsProductListAdapter;
import kiu.business.registerboxapp.view.adapter.TicketProductListAdapter;
import kiu.business.registerboxapp.view.dialog.CancelTicketDialog;
import kiu.business.registerboxapp.view.dialog.OkTicketDialog;
import kiu.business.registerboxapp.view.dialog.PutCashDialog;
import kiu.business.registerboxapp.view.listener.ClickIpsProductListItem;
import ticket.controller.TicketManager;
import ticket.model.Ticket;

public class WorkingAreaFragment extends Fragment implements NotifierCurrentTicketChange,
        NotifierIpsProductListChange, ProductObserver, MyBarcodeDetector.CodeDetectObserver {

    private List<IProduct> products;

    private FragmentWorkingAreaBinding binding;

    private RecyclerView rvProductsList;
    private IpsProductListAdapter ipsProductListAdapter;

    private RecyclerView rvTicketsList;
    private TicketProductListAdapter ticketProductListAdapter;

    private TextView tvPrice;
    private TextView tvCash;
    private TextView tvCashLabel;
    private TextView tvRefund;
    private TextView tvRefundLabel;

    private MyBarcodeDetector myBarcodeDetector;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentWorkingAreaBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvPrice = binding.textViewPrice;
        tvCash = binding.textViewCash;
        tvCashLabel = binding.textViewCashLabel;
        tvRefund = binding.textViewRefund;
        tvRefundLabel = binding.textViewRefundLabel;

        binding.linearLayoutTicketPrice.setOnClickListener(v ->
                ticketActionDialog(new PutCashDialog())
        );

        binding.imageButtonCancel.setOnClickListener(v ->
                ticketActionDialog(new CancelTicketDialog())
        );

        binding.imageButtonOk.setOnLongClickListener(v -> {
            Ticket currentTicket = TicketManager.getInstance().getCurrentTicket();
            if (currentTicket.getCash() > 0 || !currentTicket.getInfo().isEmpty()) {
                // TODO When gonna print ticket change the date in printer action.
//                currentTicket.setCreatedDate(Date.from(Instant.now()));
                ticketActionDialog(new OkTicketDialog());
            } else {
                // TODO extract all text
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Vale no pago")
                        .setMessage("El vale debe de pagarse o tener alguna información de por que no está pago.")
                        .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
                builder.create().show();
            }
            return false;
        });

        binding.imageButtonPrint.setOnClickListener(v -> {

            Ticket currentTicket = TicketManager.getInstance().getCurrentTicket();
            if (currentTicket.getCash() > 0 || !currentTicket.getInfo().isEmpty()) {
                // TODO When gonna print ticket change the date in printer action.

                StringBuilder text = new StringBuilder("<111>DON PAPA CHINO");
                text.append("<110>PANADERIA DULCERIA");

                currentTicket.setCreatedDate(Date.from(Instant.now()));
                String[] lines = currentTicket.toString().split("\n");

                for (String line : lines)
                    text.append("<000>").append(line);

                Intent send = new Intent();
                send.setAction(Intent.ACTION_SEND);
                send.setPackage("mate.bluetoothprint");
                send.putExtra(Intent.EXTRA_TEXT, text.toString());
                send.setType("text/plain");
                startActivity(send);

            } else {
                // TODO extract all text
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Vale no pago")
                        .setMessage("El vale debe de pagarse o tener alguna información de por que no está pago.")
                        .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
                builder.create().show();
            }

        });

        binding.imageButtonPay.setOnClickListener(v -> {
            Ticket currentTicket = TicketManager.getInstance().getCurrentTicket();
            currentTicket.setCash(currentTicket.getPrice());
            ticketPutCash();
        });

        binding.buttonSortBySales.setOnClickListener(v -> updateProducts());

        binding.buttonScan.setOnClickListener(v -> {
            myBarcodeDetector = new MyBarcodeDetector(WorkingAreaFragment.this, binding.previewView);
            myBarcodeDetector.setupCamera();
        });

        rvProductsList = binding.recyclerViewIpsProductList;
        rvProductsList.setLayoutManager(new LinearLayoutManager(getContext()));

        rvTicketsList = binding.recyclerViewTicketProductList;
        rvTicketsList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();

        Ips.getInstance().attachProductObserver(this);
        updateProducts();
    }

    private void updateProducts() {
        products = Ips.getInstance().getProducts();
        notifyAllTicketChange();
    }

    private void ticketActionDialog(DialogFragment dialog) {
        if (TicketManager.getInstance().getCurrentTicket().getProductList().getProducts().size() > 0)
            dialog.show(getChildFragmentManager(), dialog.getClass().getName());
        else
            // TODO put the text message in string resources
            Toast.makeText(getContext(), "No hay ticket.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateTicketPrice() {
        Ticket currentTicket = TicketManager.getInstance().getCurrentTicket();

        float price = currentTicket.getPrice();
        tvPrice.setText(String.valueOf(price));

        int greenColor = ContextCompat.getColor(requireContext(), R.color.teal_700);
        int redColor = ContextCompat.getColor(requireContext(), R.color.teal_800);

        float cash = currentTicket.getCash();
        int cashColor = greenColor;
        if (cash < price)
            cashColor = redColor;
        tvCash.setText(String.valueOf(cash));
        tvCash.setTextColor(cashColor);
        tvCashLabel.setTextColor(cashColor);

        float refund = currentTicket.getRefund();
        int refundColor = greenColor;
        if (refund < 0)
            refundColor = redColor;
        tvRefund.setText(String.valueOf(refund));
        tvRefund.setTextColor(refundColor);
        tvRefundLabel.setTextColor(refundColor);
    }

    private void notifyProductListChange(IProduct product) {
        int productsPosition = products.indexOf(product);
        if (productsPosition != -1)
            ipsProductListAdapter.notifyItemChanged(productsPosition);
    }

    @Override
    public void notifyAllTicketChange() {
        // FIXME make a separated method for notify a adapter to all dataset change

        // FIXME change this method to other notifier that notify all adapters.

        ticketProductListAdapter = new TicketProductListAdapter(getChildFragmentManager());
        rvTicketsList.setAdapter(ticketProductListAdapter);

        // Fixme just pass fragment to the adapter and then get notifier by parent instanceof
        ipsProductListAdapter = new IpsProductListAdapter(this, this, products);
        rvProductsList.setAdapter(ipsProductListAdapter);

        updateTicketPrice();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void ticketProductRemove(IProduct product, int position) {
        ticketProductListAdapter = new TicketProductListAdapter(getChildFragmentManager());
        rvTicketsList.setAdapter(ticketProductListAdapter);
        notifyProductListChange(product);
        updateTicketPrice();
    }

    @Override
    public void ticketPutCash() {
        updateTicketPrice();
    }

    @Override
    public void ticketProductAdded(IProduct product, int position) {
        if (position == -1) {
            ticketProductListAdapter = new TicketProductListAdapter(getChildFragmentManager());
            rvTicketsList.setAdapter(ticketProductListAdapter);
        } else {
            ticketProductListAdapter.notifyItemChanged(position);
            notifyProductListChange(product);
        }
        updateTicketPrice();
    }

    @Override
    public void ipsProductRemoved(IProduct product, int position) {
        // Fixme pass the product is not necessary
        // Fixme make this notifier notify a ticketProductAdded too, so in ips list just notify here.
        ipsProductListAdapter.notifyItemChanged(position);
    }

    @Override
    public void productSaved(IProduct product) {
        notifyProductListChange(product);
    }

    @Override
    public void productRemoved(IProduct product) {

    }

    @Override
    public void codeDetected(String barcode) {
        int index = -1;
        IProduct product = null;
        for (int i = 0; i < products.size(); i++) {
            product = products.get(i);
            if (product.getProductId().equals(barcode)) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            ClickIpsProductListItem clickIpsProductListItem = new ClickIpsProductListItem(
                    product,
                    index,
                    this,
                    this
            );
            clickIpsProductListItem.setContext(getContext());
            clickIpsProductListItem.click();
        } else {
            // TODO extract text
            Toast.makeText(getContext(),
                    "Producto no entontrado", Toast.LENGTH_SHORT)
                    .show();
        }

        assert myBarcodeDetector != null;
        myBarcodeDetector.setupCamera();
    }
}