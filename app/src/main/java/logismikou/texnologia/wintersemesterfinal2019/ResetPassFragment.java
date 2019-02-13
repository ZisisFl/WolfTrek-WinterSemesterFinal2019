package logismikou.texnologia.wintersemesterfinal2019;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassFragment extends Fragment {

    Button send_reset;
    EditText email_reset;
    ImageView close5;

    FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reset_pass, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        send_reset = v.findViewById(R.id.send_reset);
        email_reset = v.findViewById(R.id.email_reset);

        close5 = v.findViewById(R.id.close5);

        send_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset_password();
            }
        });

        close5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firebaseAuth.getCurrentUser() != null){
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                            new AccountInfoFragment()).commit();
                }
                else {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                            new SignInFragment()).commit();
                }
            }
        });
        return v;
    }

    public void reset_password(){
        String email = email_reset.getText().toString();

        if (email.equals(""))
        {
            Toast.makeText(getActivity(),"Provide an email address",Toast.LENGTH_SHORT).show();
        }
        else
        {
            firebaseAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getActivity(),"An email was sent to your address",Toast.LENGTH_SHORT).show();
                                // return to account fragment
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                                        new AccountFragment()).commit();
                            }
                            else
                                Toast.makeText(getActivity(),"This email address doesn't exist",Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
