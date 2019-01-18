package logismikou.texnologia.wintersemesterfinal2019;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;


public class AccountFragment extends Fragment {

    Button sign_in, sign_up, sign_out, goto_account_info,
            goto_my_orders, goto_payment_info;
    TextView text_unsigned, display_email, display_name;

    FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        sign_in = v.findViewById(R.id.sign_in);
        sign_up = v.findViewById(R.id.sign_up);
        goto_account_info = v.findViewById(R.id.goto_account_info);
        goto_my_orders = v.findViewById(R.id.goto_my_orders);
        goto_payment_info = v.findViewById(R.id.goto_payment_info);


        text_unsigned = v.findViewById(R.id.text_unsigned);
        display_email = v.findViewById(R.id.display_email);
        display_name = v.findViewById(R.id.display_name);


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
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                            new MyOrdersFragment()).commit();
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
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                            new PaymentInfoFragment()).commit();
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
}
