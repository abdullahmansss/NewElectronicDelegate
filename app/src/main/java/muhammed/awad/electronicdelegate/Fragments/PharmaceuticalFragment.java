package muhammed.awad.electronicdelegate.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
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
import muhammed.awad.electronicdelegate.PharmaceuticalActivity;
import muhammed.awad.electronicdelegate.R;

public class PharmaceuticalFragment extends Fragment
{
    View view;

    FloatingActionButton add_new_pharmaceutical;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    FirebaseRecyclerAdapter<MedicineModel, pharmaceuticalViewholder> firebaseRecyclerAdapter;

    RotateLoading rotateLoading;

    public final static String EXTRA_EDIT_PHARMA = "edit_pharma";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.pharmaceutical_fragment, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        recyclerView = view.findViewById(R.id.doctors_recyclerview);
        rotateLoading = view.findViewById(R.id.rotateloading);
        add_new_pharmaceutical = view.findViewById(R.id.add_new_pharmaceutical);

        add_new_pharmaceutical.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getContext(), PharmaceuticalActivity.class);
                startActivity(intent);
            }
        });

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
                .child("pharmaceutical")
                .child(getUID())
                .limitToLast(50);

        FirebaseRecyclerOptions<MedicineModel> options =
                new FirebaseRecyclerOptions.Builder<MedicineModel>()
                        .setQuery(query, MedicineModel.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<MedicineModel, pharmaceuticalViewholder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull pharmaceuticalViewholder holder, int position, @NonNull final MedicineModel model)
            {
                rotateLoading.stop();

                final String key = getRef(position).getKey();

                holder.details.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent= new Intent(getContext(), PharmaceuticalActivity.class);
                        intent.putExtra(EXTRA_EDIT_PHARMA, key);
                        startActivity(intent);
                    }
                });

                holder.BindPlaces(model);
            }

            @NonNull
            @Override
            public pharmaceuticalViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.pharmaceutical_item, parent, false);
                return new pharmaceuticalViewholder(view);
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        rotateLoading.stop();
    }

    public static class pharmaceuticalViewholder extends RecyclerView.ViewHolder
    {
        CircleImageView medicine_image;
        TextView medicine_name,medicine_price;
        MaterialRippleLayout details;

        pharmaceuticalViewholder(View itemView)
        {
            super(itemView);

            medicine_image = itemView.findViewById(R.id.medicine_image);
            medicine_name = itemView.findViewById(R.id.medicine_name);
            medicine_price = itemView.findViewById(R.id.medicine_price);
            details = itemView.findViewById(R.id.details_btn);
        }

        void BindPlaces(final MedicineModel medicineModel)
        {
            medicine_name.setText(medicineModel.getName());
            medicine_price.setText("Price : " + medicineModel.getPrice());

            Picasso.get()
                    .load(medicineModel.getImageurl())
                    .placeholder(R.drawable.addphoto)
                    .error(R.drawable.addphoto)
                    .into(medicine_image);
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
