package muhammed.awad.electronicdelegate.PatientApp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import muhammed.awad.electronicdelegate.R;

public class DrugsFragment extends Fragment
{
    View view;

    Button sign_out_btn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.patient_drugs_fragment, container, false);

        return view;
    }
}
