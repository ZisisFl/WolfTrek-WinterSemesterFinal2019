package logismikou.texnologia.wintersemesterfinal2019;


import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.support.constraint.Constraints.TAG;

public class FinalizeOrderFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference, orderReference, cartReference;

    ProgressDialog progressDialog;

    TextView card_holder_textview, credit_card_textview;
    EditText security_code, card_number2, card_holder2, exp_year2, exp_month2, cvv2;
    Button finalize_btn, new_card, finalize_btn_2;
    String cvv, c_exp_month, c_exp_year;
    ConstraintLayout layout1, layout2, layout3;
    ImageView close_finalize, close_new_card;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_finalize_order, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(getActivity());

        card_number2 = v.findViewById(R.id.card_number2);
        card_holder2 = v.findViewById(R.id.card_holder2);
        exp_year2 = v.findViewById(R.id.exp_year2);
        exp_month2 = v.findViewById(R.id.exp_month2);
        cvv2 = v.findViewById(R.id.cvv2);

        card_holder_textview = v.findViewById(R.id.card_holder_textview);
        credit_card_textview = v.findViewById(R.id.credit_card_textview);
        security_code = v.findViewById(R.id.security_code);
        finalize_btn = v.findViewById(R.id.finalize_btn);
        finalize_btn_2 = v.findViewById(R.id.finalize_btn_2);
        close_finalize = v.findViewById(R.id.close_finalize);
        new_card = v.findViewById(R.id.new_card);
        close_new_card = v.findViewById(R.id.close_new_card);


        layout1 = v.findViewById(R.id.layout1);
        layout2 = v.findViewById(R.id.layout2);
        layout3 = v.findViewById(R.id.layout3);

        check_users_credit_card();

        finalize_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate_existing_credit_card();
            }
        });

        finalize_btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate_new_credit_card();
            }
        });

        new_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout1.setVisibility(View.GONE);
                layout3.setVisibility(View.VISIBLE);
            }
        });

        close_finalize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                        new CheckOutFragment()).commit();
            }
        });

        close_new_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout3.setVisibility(View.GONE);
                layout1.setVisibility(View.VISIBLE);
            }
        });
        return v;
    }

    public void check_users_credit_card(){
        String userId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(userId).child("Credit card info");

        ValueEventListener check_credit_card = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    //Toast.makeText(getActivity(), "YOLO", Toast.LENGTH_SHORT).show();
                    CreditCard creditCard = dataSnapshot.getValue(CreditCard.class);

                    //get credit card and card holder
                    String card_number = creditCard.card_number;
                    credit_card_textview.setText("Bill to credit card: "
                            +generate_hidden_string(card_number, 0));


                    String card_holder = creditCard.card_holder;
                    // split string in with spaces
                    String[] separated = card_holder.split(" ");

                    String surname = separated[0];
                    String firstname = separated[1];

                    card_holder_textview.setText("Card holder: "
                            +generate_hidden_string(surname, 1)+
                            " "+ generate_hidden_string(firstname, 1));


                    cvv = creditCard.cvv;
                    c_exp_month = creditCard.exp_month;
                    c_exp_year = creditCard.exp_year;

                }
                else{
                    layout1.setVisibility(View.GONE);
                    layout3.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        databaseReference.addValueEventListener(check_credit_card);
    }

    public String generate_hidden_string(String string, int number) {
        // define a char array
        char hidden_string_arr[] = string.toCharArray();
        // define a empty string
        String hidden_string = "";

        if (number == 0) {
            for (int i = 0; i < hidden_string_arr.length; i++) {
                if (i < hidden_string_arr.length - 2) {
                    hidden_string = hidden_string + "*";
                } else {
                    hidden_string = hidden_string + String.valueOf(hidden_string_arr[i]);
                }
            }
        }
        else {
            for (int i = 0; i < hidden_string_arr.length; i++) {
                if (i != 0) {
                    hidden_string = hidden_string + "*";
                } else {
                    hidden_string = hidden_string + String.valueOf(hidden_string_arr[i]);
                }
            }
        }
        return hidden_string;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void validate_existing_credit_card(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR); // get current year
        int month = calendar.get(Calendar.MONTH) + 1; // in java month starts from 0 not from 1 so for december 11+1 = 12

        String security_code_string = security_code.getText().toString();

        if((Integer.parseInt(c_exp_month) < month) &&
                (Integer.parseInt(c_exp_year) <= year)){ //if card exp month is < than current month
            Toast.makeText(getActivity(),"Credit card has expired",Toast.LENGTH_SHORT).show();
        }
        else if(!security_code_string.equals(cvv)){
            Toast.makeText(getActivity(), "Wrong security code", Toast.LENGTH_SHORT).show();
        }
        else { //success
            progressDialog.setMessage("Processing Order...");
            progressDialog.show();

            create_order();

            layout1.setVisibility(View.GONE);
            layout2.setVisibility(View.VISIBLE);
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void validate_new_credit_card(){

        String c_card_number = card_number2.getText().toString();
        String c_card_holder = card_holder2.getText().toString();
        String c_cvv = cvv2.getText().toString();
        String c_exp_month = exp_month2.getText().toString();
        String c_exp_year = exp_year2.getText().toString();

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
            CharSequence card_holder_name = card_holder2.getText().toString();
            Matcher matcher = pattern.matcher(card_holder_name);
            if(!matcher.matches())
            {
                Toast.makeText(getActivity(),"Invalid card holder name format",Toast.LENGTH_SHORT).show();
            }
            else //success
            {
                progressDialog.setMessage("Processing Order...");
                progressDialog.show();

                create_order();

                layout3.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void create_order(){
        // get user id
        String userId = firebaseAuth.getCurrentUser().getUid();
        // create reference for orders
        orderReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId)
                .child("Orders");
        // create order id
        String oreder_id = orderReference.push().getKey();
        // get current time
        Date currentTime = Calendar.getInstance().getTime();
        String c_time = currentTime.toString();

        // create order obj
        OrderInfo orderInfo = new OrderInfo(oreder_id, c_time, createQR_CODE(oreder_id));

        orderReference.child(oreder_id).setValue(orderInfo)
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

        cartReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId)
                .child("Cart");

        // delete cart after order completed
        //cartReference.removeValue();
    }

    public String createQR_CODE(String id){
        Bitmap bitmap;
        String temp = "";

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(id, BarcodeFormat.QR_CODE, 500, 500);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap =(barcodeEncoder.createBitmap(bitMatrix));

            // encode Bitmap to String

            ByteArrayOutputStream baos = new  ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte [] b=baos.toByteArray();
            temp = Base64.encodeToString(b, Base64.DEFAULT);


        } catch (WriterException e) {
            e.printStackTrace();
        }
        return temp;
    }
}
