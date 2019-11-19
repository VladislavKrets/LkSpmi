package com.re.lkspmi;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ru.spmi.lk.entities.orders.Order;

public class OrdersFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.orders_fragment, null);

        try {
            List<Order> orders = new OrdersTask().execute().get();
            if (orders != null){
                TableLayout tableLayout = view.findViewById(R.id.table_orders);
                TableRow row = new TableRow(getContext());
                row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                Resources r = getContext().getResources();
                float px = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        6,
                        r.getDisplayMetrics()
                );
                String[] lines = new String[] {"Дата", "Номер", "Название", "Дата действия", "Описание"};

                for (String s : lines){
                    addRowItem(row, layoutParams, px, s);
                }
                tableLayout.addView(row);

                for (Order order : orders){
                    row = new TableRow(getContext());
                    addRowItem(row, layoutParams, px, order.getDate());
                    addRowItem(row, layoutParams, px, order.getNumber());
                    addRowItem(row, layoutParams, px, order.getTitle() == null ? "" : order.getTitle());
                    addRowItem(row, layoutParams, px, order.getDate_approve() == null ? "" : order.getDate_approve());
                    addRowItem(row, layoutParams, px, order.getAction());
                    tableLayout.addView(row);
                }

            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return view;
    }

    private void addRowItem(TableRow row, LinearLayout.LayoutParams layoutParams, float px, String s) {
        LinearLayout linearLayout;
        TextView textView;
        linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(20, 20, 20, 20);
        textView = new TextView(getContext());
        textView.setMaxWidth(500);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(px);
        textView.setText(s);
        linearLayout.addView(textView);
        row.addView(linearLayout);
    }

    class OrdersTask extends AsyncTask<Void, Void, List<Order>>{

        @Override
        protected List<Order> doInBackground(Void... voids) {
            try {
                List<Order> orders = LkSingleton.getInstance().getLkSpmi().getOrders();
                return orders;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
