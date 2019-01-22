package logismikou.texnologia.wintersemesterfinal2019;


import android.annotation.TargetApi;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CardFormFragment extends Fragment {

    EditText card_number, card_holder, cvv, exp_month, exp_year;
    Button add_card_btn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_card_form, container, false);

        card_number = v.findViewById(R.id.card_number);
        card_holder = v.findViewById(R.id.card_holder);
        cvv = v.findViewById(R.id.cvv);
        exp_month = v.findViewById(R.id.exp_month);
        exp_year = v.findViewById(R.id.exp_year);

        add_card_btn = v.findViewById(R.id.add_card_btn);

        add_card_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate_credit_card();
            }
        });

        return v;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void validate_credit_card(){

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR); // get current year
        int month = calendar.get(Calendar.MONTH) + 1; // in java month starts from 0 not from 1 so for december 11+1 = 12

        String regx = "^[\\p{L} .'-]+$";
        // \\p{L} is a Unicode Character Property that matches any kind of letter from any language
        Pattern pattern = Pattern.compile(regx);


        if(card_number.getText().toString().equals("")){
            Toast.makeText(getActivity(),"Blank card number field",Toast.LENGTH_SHORT).show();
        }
        else if(card_number.getText().toString().length() != 16){
            Toast.makeText(getActivity(),"Card number must have 16 digits",Toast.LENGTH_SHORT).show();
        }
        else if(card_holder.getText().toString().equals("")){
            Toast.makeText(getActivity(),"Blank card holder field",Toast.LENGTH_SHORT).show();
        }
        else if(cvv.getText().toString().equals("")){
            Toast.makeText(getActivity(),"Blank security code field",Toast.LENGTH_SHORT).show();
        }
        else if(cvv.getText().toString().length() != 3){
            Toast.makeText(getActivity(),"Security code must have 3 digits",Toast.LENGTH_SHORT).show();
        }
        else if(exp_month.getText().toString().equals("")){
            Toast.makeText(getActivity(),"Blank expiration month field",Toast.LENGTH_SHORT).show();
        }
        else if(exp_month.getText().toString().length() != 2){
            Toast.makeText(getActivity(),"Expiration month field must have mm format",Toast.LENGTH_SHORT).show();
        }
        else if((Integer.parseInt(exp_month.getText().toString()) < month) &&
                (Integer.parseInt(exp_year.getText().toString()) <= year)){ //if card exp month is < than current month
            Toast.makeText(getActivity(),"Credit card has expired",Toast.LENGTH_SHORT).show();
        }
        else if(exp_year.getText().toString().equals("")){
            Toast.makeText(getActivity(),"Blank expiration year field",Toast.LENGTH_SHORT).show();
        }
        else if(exp_year.getText().toString().length() != 4){
            Toast.makeText(getActivity(),"Expiration year field must have yyyy format",Toast.LENGTH_SHORT).show();
        }
        else if(Integer.parseInt(exp_year.getText().toString()) < year){ //if card exp year is < than current year
            Toast.makeText(getActivity(),"Invalid expiration year",Toast.LENGTH_SHORT).show();
        }
        else
        {
            CharSequence card_holder_name = card_holder.getText().toString();
            Matcher matcher = pattern.matcher(card_holder_name);
            if(matcher.matches())
            {
                Toast.makeText(getActivity(),"Success",Toast.LENGTH_SHORT).show();
                //save data
            }
            else
            {
                Toast.makeText(getActivity(),"Invalid card holder name format",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void register_credit_card(){

    }
}
