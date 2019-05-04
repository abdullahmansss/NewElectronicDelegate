package muhammed.awad.electronicdelegate.PatientApp.Fragments;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import muhammed.awad.electronicdelegate.Listner;
import muhammed.awad.electronicdelegate.Models.CompanyModel;
import muhammed.awad.electronicdelegate.Models.MedicineModel;
import muhammed.awad.electronicdelegate.PatientApp.CheckActivity;
import muhammed.awad.electronicdelegate.PatientApp.PatientMainActivity;
import muhammed.awad.electronicdelegate.PatientApp.PharmacyStore;
import muhammed.awad.electronicdelegate.R;

public class PharmaciesFragment extends Fragment
{
    View view;

    RecyclerView recyclerView;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    LinearLayoutManager layoutManager;
    FirebaseRecyclerAdapter<CompanyModel, pharmaceuticalViewholder> firebaseRecyclerAdapter;

    Spinner g,d;
    Button done_btn;

    String selected_governorate,selected_district;

    public final static String PH_KEY = "pharm";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.patient_pharmacies_fragment, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        recyclerView = view.findViewById(R.id.recyclerview);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        g = view.findViewById(R.id.governorate_spinner);
        d  = view.findViewById(R.id.district_spinner);
        done_btn = view.findViewById(R.id.done_btn);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getContext(),
                R.array.governorate, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        g.setAdapter(adapter1);

        g.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                selected_governorate = String.valueOf(parent.getItemAtPosition(position));

                if (selected_governorate.equals("Cairo"))
                {
                    ArrayAdapter<CharSequence> cairo_adapter = ArrayAdapter.createFromResource(getContext(),
                            R.array.cairodistrict, android.R.layout.simple_spinner_item);
                    // Specify the layout to use when the list of choices appears
                    cairo_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    d.setAdapter(cairo_adapter);
                } else if (selected_governorate.equals("Giza"))
                {
                    ArrayAdapter<CharSequence> giza_adapter = ArrayAdapter.createFromResource(getContext(),
                            R.array.gizadistrict, android.R.layout.simple_spinner_item);
                    // Specify the layout to use when the list of choices appears
                    giza_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    d.setAdapter(giza_adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        d.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                selected_district = String.valueOf(parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        done_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (selected_governorate.equals("Select governorate"))
                {
                    Toast.makeText(getContext(), "please enter governorate name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selected_district.equals("Select district") || selected_district.length() == 0)
                {
                    Toast.makeText(getContext(), "please enter district name", Toast.LENGTH_SHORT).show();
                    return;
                }

                DisplayallMedicines(selected_governorate, selected_district);
                firebaseRecyclerAdapter.startListening();
            }
        });
    }

    private void DisplayallMedicines(String g, String d)
    {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("PharmaciesLocations")
                .child(g)
                .child(d)
                .limitToLast(50);

        FirebaseRecyclerOptions<CompanyModel> options =
                new FirebaseRecyclerOptions.Builder<CompanyModel>()
                        .setQuery(query, CompanyModel.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<CompanyModel, pharmaceuticalViewholder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull pharmaceuticalViewholder holder, int position, @NonNull final CompanyModel model)
            {
                final String key = getRef(position).getKey();

                holder.details.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(getContext(), PharmacyStore.class);
                        intent.putExtra(PH_KEY, key);
                        startActivity(intent);
                    }
                });

                holder.BindPlaces(model);
            }

            @NonNull
            @Override
            public pharmaceuticalViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.pharmacy_item, parent, false);
                return new pharmaceuticalViewholder(view);
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class pharmaceuticalViewholder extends RecyclerView.ViewHolder
    {
        TextView medicine_name;
        MaterialRippleLayout details;

        pharmaceuticalViewholder(View itemView)
        {
            super(itemView);

            medicine_name = itemView.findViewById(R.id.pharm_name);
            details = itemView.findViewById(R.id.details_btn);
        }

        void BindPlaces(final CompanyModel medicineModel)
        {
            medicine_name.setText(medicineModel.getTitle());
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
}
