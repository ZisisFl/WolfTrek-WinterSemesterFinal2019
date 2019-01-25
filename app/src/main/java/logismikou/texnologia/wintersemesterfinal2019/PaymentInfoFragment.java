package logismikou.texnologia.wintersemesterfinal2019;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PaymentInfoFragment extends Fragment {

    Button goto_add_card;
    ImageView close10;


    FirebaseAuth firebaseAuth;

    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_payment_info, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(getActivity());

        close10 = v.findViewById(R.id.close10);
        goto_add_card = v.findViewById(R.id.goto_add_card);

        goto_add_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                //        new CardFormFragment()).commit();

                // pass false value to not trigger filling fields
                boolean credit_card_found = false;
                CardFormFragment fragment = new CardFormFragment();
                Bundle arguments = new Bundle();
                arguments.putBoolean("1234", credit_card_found);
                fragment.setArguments(arguments);
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.main_layout, fragment);
                ft.commit();
            }
        });

        close10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                        new AccountFragment()).commit();
            }
        });

        return v;
    }
}
