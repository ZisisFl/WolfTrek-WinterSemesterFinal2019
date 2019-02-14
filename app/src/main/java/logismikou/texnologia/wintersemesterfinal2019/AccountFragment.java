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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AccountFragment extends Fragment {

    boolean credit_card_found = false;

    Button sign_in, sign_up, sign_out, goto_account_info,
            goto_my_orders, goto_payment_info;
    TextView text_unsigned, display_email, display_name;
    ConstraintLayout blank_frame;

    ProgressDialog progressDialog;

    FirebaseAuth firebaseAuth;
    DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(getActivity());

        sign_in = v.findViewById(R.id.sign_in);
        sign_up = v.findViewById(R.id.sign_up);
        goto_account_info = v.findViewById(R.id.goto_account_info);
        goto_my_orders = v.findViewById(R.id.goto_my_orders);
        goto_payment_info = v.findViewById(R.id.goto_payment_info);


        text_unsigned = v.findViewById(R.id.text_unsigned);
        display_email = v.findViewById(R.id.display_email);
        display_name = v.findViewById(R.id.display_name);

        blank_frame = v.findViewById(R.id.blank_frame);


        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                        new SignInFragment()).commit();
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                        new SignUpFragment()).commit();
            }
        });


        goto_account_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firebaseAuth.getCurrentUser() != null){
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                            new AccountInfoFragment()).commit();
                }
                else{
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                            new NotSignedFragment()).commit();
                }
            }
        });

        goto_my_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firebaseAuth.getCurrentUser() != null){
                    MyOrdersFragment fragment2 = new MyOrdersFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                            fragment2, "my_orders_fragment").commit();
                }
                else {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                            new NotSignedFragment()).commit();
                }
            }
        });

        goto_payment_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firebaseAuth.getCurrentUser() != null){
                    check_users_card();
                }
                else {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                            new NotSignedFragment()).commit();
                }
            }
        });

        check_if_signed();

        return v;
    }

    public void check_if_signed(){
        //check if user is logged in
        if(firebaseAuth.getCurrentUser() != null){
            String email = firebaseAuth.getCurrentUser().getEmail();
            String name = firebaseAuth.getCurrentUser().getDisplayName();

            display_email.setText(email);
            display_name.setText(name);

            sign_in.setVisibility(View.GONE);
            sign_up.setVisibility(View.GONE);
            text_unsigned.setVisibility(View.GONE);

            display_email.setVisibility(View.VISIBLE);
            display_name.setVisibility(View.VISIBLE);
        }
        else{
            sign_in.setVisibility(View.VISIBLE);
            sign_up.setVisibility(View.VISIBLE);
            text_unsigned.setVisibility(View.VISIBLE);

            display_email.setVisibility(View.GONE);
            display_name.setVisibility(View.GONE);
        }
    }

    public void check_users_card(){
        // load screen until data is fetched
        blank_frame.setVisibility(View.VISIBLE);
        progressDialog.setMessage("Retrieving Data...");
        progressDialog.show();

        String userId = firebaseAuth.getCurrentUser().getUid();

        mDatabase.child("Users").child(userId).child("Credit card info")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            //user has a credit card
                            blank_frame.setVisibility(View.GONE);
                            progressDialog.dismiss();
                            //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                            //        new CardFormFragment()).commit();

                            // pass a boolean to trigger filling fields
                            credit_card_found = true;
                            CardFormFragment fragment = new CardFormFragment();
                            Bundle arguments = new Bundle();
                            arguments.putBoolean("1234", credit_card_found);
                            fragment.setArguments(arguments);
                            final FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.replace(R.id.main_layout, fragment);
                            ft.commit();
                        } else {
                            //user has not a credit card
                            progressDialog.dismiss();
                            blank_frame.setVisibility(View.GONE);
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                                    new PaymentInfoFragment()).commit();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
