package muhammed.awad.electronicdelegate.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;
import com.victor.loading.rotate.RotateLoading;

import de.hdodenhof.circleimageview.CircleImageView;
import muhammed.awad.electronicdelegate.Models.MedicineModel;
import muhammed.awad.electronicdelegate.Models.OrderModel;
import muhammed.awad.electronicdelegate.PharmaceuticalActivity;
import muhammed.awad.electronicdelegate.R;

public class RequestsFragment extends Fragment
{
    View view;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    FirebaseRecyclerAdapter<OrderModel, OrderViewholder> firebaseRecyclerAdapter;

    RotateLoading rotateLoading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.requests_fragment, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView = view.findViewById(R.id.doctors_recyclerview);
        rotateLoading = view.findViewById(R.id.rotateloading);


        rotateLoading.start();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.keepSynced(true);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        DisplayallMedicines();
    }

    private void DisplayallMedicines()
    {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("requests")
                .child(getUID())
                .limitToLast(50);

        FirebaseRecyclerOptions<OrderModel> options =
                new FirebaseRecyclerOptions.Builder<OrderModel>()
                        .setQuery(query, OrderModel.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<OrderModel, OrderViewholder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewholder holder, int position, @NonNull final OrderModel model)
            {
                rotateLoading.stop();

                String key = getRef(position).getKey();

                holder.accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "Accept Clicked", Toast.LENGTH_SHORT).show();
                    }
                });

                holder.decline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Toast.makeText(getContext(), "Decline Clicked", Toast.LENGTH_SHORT).show();
                    }
                });

                holder.BindPlaces(model);
            }

            @NonNull
            @Override
            public OrderViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.order_item, parent, false);
                return new OrderViewholder(view);
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        rotateLoading.stop();
    }

    public static class OrderViewholder extends RecyclerView.ViewHolder
    {
        CircleImageView order_image;
        TextView order_name,pharmacy_name,order_location,order_price,order_q;
        Button accept,decline;

        OrderViewholder(View itemView)
        {
            super(itemView);

            order_image = itemView.findViewById(R.id.order_image);
            order_name = itemView.findViewById(R.id.order_name);
            pharmacy_name = itemView.findViewById(R.id.order_pharmacy);
            order_location = itemView.findViewById(R.id.order_location);
            order_price = itemView.findViewById(R.id.order_price);
            order_q = itemView.findViewById(R.id.order_q);
            accept = itemView.findViewById(R.id.accept_btn);
            decline = itemView.findViewById(R.id.decline_btn);
        }

        void BindPlaces(final OrderModel orderModel)
        {
            pharmacy_name.setText("From : " + orderModel.getPharmacy_name());
            order_name.setText(orderModel.getOrder_name());
            order_location.setText(orderModel.getOrder_location());
            order_price.setText("Total : " + orderModel.getOrder_price() + " L.E");
            order_q.setText(orderModel.getOrder_quantity() + " Items");

            Picasso.get()
                    .load(orderModel.getOrder_image())
                    .placeholder(R.drawable.addphoto)
                    .error(R.drawable.addphoto)
                    .into(order_image);
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (firebaseRecyclerAdapter != null)
        {
            firebaseRecyclerAdapter.startListening();
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();

        if (firebaseRecyclerAdapter != null)
        {
            firebaseRecyclerAdapter.stopListening();
        }
    }

    public String getUID()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        return userId;
    }
}
