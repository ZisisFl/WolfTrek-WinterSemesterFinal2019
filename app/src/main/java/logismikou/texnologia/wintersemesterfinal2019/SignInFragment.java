package logismikou.texnologia.wintersemesterfinal2019;


import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInFragment extends Fragment {

    Button verify_sign_in;
    EditText email_sign_in, password_sign_in;
    TextView goto_sign_up, reset_text, goto_reset_password;
    ImageView close3;

    FirebaseAuth firebaseAuth;

    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sign_in, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(getActivity());

        verify_sign_in = v.findViewById(R.id.send_reset);

        email_sign_in = v.findViewById(R.id.email_reset);
        password_sign_in = v.findViewById(R.id.password_sign_in);

        goto_sign_up = v.findViewById(R.id.goto_sign_up);
        reset_text = v.findViewById(R.id.reset_text);
        goto_reset_password = v.findViewById(R.id.goto_reset_password);

        close3 = v.findViewById(R.id.close3);

        verify_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_user();
            }
        });

        goto_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                        new SignUpFragment()).commit();
            }
        });

        // In case of wrong inputs bring reset button
        goto_reset_password.setVisibility(View.INVISIBLE);
        reset_text.setVisibility(View.INVISIBLE);

        goto_reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                        new ResetPassFragment()).commit();
            }
        });

        close3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                        new AccountFragment()).commit();
            }
        });

        return v;
    }

    private void login_user(){
        String email = email_sign_in.getText().toString();
        String password = password_sign_in.getText().toString();

        if (email_sign_in.getText().toString().equals(""))
            Toast.makeText(getActivity(),"Blank email field",Toast.LENGTH_SHORT).show();
        else if (password_sign_in.getText().toString().equals(""))
            Toast.makeText(getActivity(),"Blank password field",Toast.LENGTH_SHORT).show();
        else
        {
            progressDialog.setMessage("Signing in...");
            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(email, password).
                    addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()){
                                // user is successfully signed in
                                Toast.makeText(getActivity(),"Success",Toast.LENGTH_SHORT).show();
                                // return to account fragment
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                                        new AccountFragment()).commit();
                            }
                            else
                            {
                                Toast.makeText(getActivity(),"Wrong email or password",Toast.LENGTH_SHORT).show();
                                //make reset text visible after wrong inputs
                                goto_reset_password.setVisibility(View.VISIBLE);
                                reset_text.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }
    }
}
