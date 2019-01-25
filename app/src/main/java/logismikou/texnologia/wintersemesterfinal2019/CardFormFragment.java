package logismikou.texnologia.wintersemesterfinal2019;


import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.support.constraint.Constraints.TAG;

public class CardFormFragment extends Fragment {

    EditText card_number, card_holder, cvv, exp_month, exp_year;
    Button add_card_btn;
    ImageView close9, save_credit_info, edit_credit_info;

    FirebaseAuth firebaseAuth;
    DatabaseReference mDatabase;
    DatabaseReference credit_card_info;

    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_card_form, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(getActivity());

        card_number = v.findViewById(R.id.card_number);
        card_holder = v.findViewById(R.id.card_holder);
        cvv = v.findViewById(R.id.cvv);
        exp_month = v.findViewById(R.id.exp_month);
        exp_year = v.findViewById(R.id.exp_year);

        add_card_btn = v.findViewById(R.id.add_card_btn);

        save_credit_info = v.findViewById(R.id.save_credit_info);
        edit_credit_info = v.findViewById(R.id.edit_credit_info);
        close9 = v.findViewById(R.id.close9);

        add_card_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate_credit_card();
            }
        });

        edit_credit_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // make fields available again
                fields_available();
            }
        });

        save_credit_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate_credit_card();
                fields_unavailable();
            }
        });

        close9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                        new AccountFragment()).commit();
            }
        });

        load_users_credit_card();

        return v;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void validate_credit_card(){

        String c_card_number = card_number.getText().toString();
        String c_card_holder = card_holder.getText().toString();
        String c_cvv = cvv.getText().toString();
        String c_exp_month = exp_month.getText().toString();
        String c_exp_year = exp_year.getText().toString();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR); // get current year
        int month = calendar.get(Calendar.MONTH) + 1; // in java month starts from 0 not from 1 so for december 11+1 = 12

        String regx = "^[\\p{L} .'-]+$";
        // \\p{L} is a Unicode Character Property that matches any kind of letter from any language
        Pattern pattern = Pattern.compile(regx);


        if(c_card_number.equals("")){
            Toast.makeText(getActivity(),"Blank card number field",Toast.LENGTH_SHORT).show();
        }
        else if(c_card_number.length() != 16){
            Toast.makeText(getActivity(),"Card number must have 16 digits",Toast.LENGTH_SHORT).show();
        }
        else if(c_card_holder.equals("")){
            Toast.makeText(getActivity(),"Blank card holder field",Toast.LENGTH_SHORT).show();
        }
        else if(c_cvv.equals("")){
            Toast.makeText(getActivity(),"Blank security code field",Toast.LENGTH_SHORT).show();
        }
        else if(c_cvv.length() != 3){
            Toast.makeText(getActivity(),"Security code must have 3 digits",Toast.LENGTH_SHORT).show();
        }
        else if(c_exp_month.equals("")){
            Toast.makeText(getActivity(),"Blank expiration month field",Toast.LENGTH_SHORT).show();
        }
        else if(c_exp_month.length() != 2){
            Toast.makeText(getActivity(),"Expiration month field must have mm format",Toast.LENGTH_SHORT).show();
        }
        else if((Integer.parseInt(c_exp_month) > 12) || ((Integer.parseInt(c_exp_month) < 1))){
            Toast.makeText(getActivity(),"Invalid month input",Toast.LENGTH_SHORT).show();
        }
        else if((Integer.parseInt(c_exp_month) < month) &&
                (Integer.parseInt(c_exp_year) <= year)){ //if card exp month is < than current month
            Toast.makeText(getActivity(),"Credit card has expired",Toast.LENGTH_SHORT).show();
        }
        else if(c_exp_year.equals("")){
            Toast.makeText(getActivity(),"Blank expiration year field",Toast.LENGTH_SHORT).show();
        }
        else if(c_exp_year.length() != 4){
            Toast.makeText(getActivity(),"Expiration year field must have yyyy format",Toast.LENGTH_SHORT).show();
        }
        else if(Integer.parseInt(c_exp_year) < year){ //if card exp year is < than current year
            Toast.makeText(getActivity(),"Invalid expiration year",Toast.LENGTH_SHORT).show();
        }
        else if((Integer.parseInt(c_exp_year) > 2040)){
            Toast.makeText(getActivity(),"Invalid year input",Toast.LENGTH_SHORT).show();
        }
        else
        {
            CharSequence card_holder_name = card_holder.getText().toString();
            Matcher matcher = pattern.matcher(card_holder_name);
            if(matcher.matches())
            {
                // if fields are valid register data
                progressDialog.setMessage("Registering data...");
                progressDialog.show();
                register_credit_card(c_card_number, c_card_holder, c_cvv, c_exp_month,
                        c_exp_year);
            }
            else
            {
                Toast.makeText(getActivity(),"Invalid card holder name format",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void register_credit_card(String c_card_number, String c_card_holder, String c_cvv, String c_exp_month,
                                     String c_exp_year){
        String userId = firebaseAuth.getCurrentUser().getUid();

        CreditCard creditCard = new CreditCard(c_card_number, c_card_holder, c_cvv, c_exp_month,
                c_exp_year);

        mDatabase.child("Users").child(userId).child("Credit card info").setValue(creditCard)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(),"Success",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),"An error occurred",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void load_users_credit_card(){
        //get uid
        String user_id = firebaseAuth.getCurrentUser().getUid();
        //get reference
        credit_card_info = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("Credit card info");
        // open bundle
        Bundle arguments = getArguments();
        boolean credit_card_found = arguments.getBoolean("1234");

        if(credit_card_found){
            // if there is a credit card make view changes

            ValueEventListener credit_cardListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get CreditCard object and use the values to update the UI
                    CreditCard creditCard = dataSnapshot.getValue(CreditCard.class);

                    card_number.setText(creditCard.card_number);
                    card_holder.setText(creditCard.card_holder);
                    cvv.setText(creditCard.cvv);
                    exp_month.setText(creditCard.exp_month);
                    exp_year.setText(creditCard.exp_year);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    // ...
                }
            };
            credit_card_info.addValueEventListener(credit_cardListener);
            fields_unavailable();
        }
    }

    public void fields_unavailable(){
        add_card_btn.setVisibility(View.GONE);
        edit_credit_info.setVisibility(View.VISIBLE);
        save_credit_info.setVisibility(View.GONE);

        // make fields unavailable
        card_number.setTag(card_number.getKeyListener());
        card_number.setKeyListener(null);
        card_number.setFocusableInTouchMode(false);

        card_holder.setTag(card_holder.getKeyListener());
        card_holder.setKeyListener(null);
        card_holder.setFocusableInTouchMode(false);

        cvv.setTag(cvv.getKeyListener());
        cvv.setKeyListener(null);
        cvv.setFocusableInTouchMode(false);

        exp_month.setTag(exp_month.getKeyListener());
        exp_month.setKeyListener(null);
        exp_month.setFocusableInTouchMode(false);

        exp_year.setTag(exp_year.getKeyListener());
        exp_year.setKeyListener(null);
        exp_year.setFocusableInTouchMode(false);
    }

    public void fields_available(){
        card_number.setKeyListener((KeyListener) card_number.getTag());
        card_number.setFocusableInTouchMode(true);

        card_holder.setKeyListener((KeyListener) card_holder.getTag());
        card_holder.setFocusableInTouchMode(true);

        cvv.setKeyListener((KeyListener) cvv.getTag());
        cvv.setFocusableInTouchMode(true);

        exp_month.setKeyListener((KeyListener) exp_month.getTag());
        exp_month.setFocusableInTouchMode(true);

        exp_year.setKeyListener((KeyListener) exp_year.getTag());
        exp_year.setFocusableInTouchMode(true);

        edit_credit_info.setVisibility(View.GONE);
        save_credit_info.setVisibility(View.VISIBLE);
    }
}