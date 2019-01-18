package logismikou.texnologia.wintersemesterfinal2019;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.method.KeyListener;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class AccountInfoFragment extends Fragment {

    Button goto_reset_email, goto_reset_password2, sign_out;
    ImageView edit_info_btn, save_info_btn, close1;
    EditText firstname_acc_info, surname_acc_info;

    FirebaseAuth firebaseAuth;

    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account_info, container, false);
        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(getActivity());

        firstname_acc_info = v.findViewById(R.id.firstname_acc_info);
        surname_acc_info = v.findViewById(R.id.surname_acc_info);

        goto_reset_email = v.findViewById(R.id.goto_reset_email);
        goto_reset_password2 = v.findViewById(R.id.goto_reset_password2);
        sign_out = v.findViewById(R.id.sign_out);

        edit_info_btn = v.findViewById(R.id.edit_info_btn);
        save_info_btn = v.findViewById(R.id.save_info_btn);
        close1 = v.findViewById(R.id.close1);

        close1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                        new AccountFragment()).commit();
            }
        });

        goto_reset_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                        new ResetEmailFragment()).commit();
            }
        });

        goto_reset_password2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                        new ResetPassFragment()).commit();
            }
        });

        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if user is already logged in
                if(firebaseAuth.getCurrentUser() != null){
                    firebaseAuth.signOut();
                    Toast.makeText(getActivity(), "Signed out", Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                            new AccountFragment()).commit();
                }
                else
                    Toast.makeText(getActivity(), "Already signed out", Toast.LENGTH_SHORT).show();
            }
        });


        edit_info_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // make edit text editable
                firstname_acc_info.setKeyListener((KeyListener) firstname_acc_info.getTag());
                firstname_acc_info.setFocusableInTouchMode(true);

                surname_acc_info.setKeyListener((KeyListener) surname_acc_info.getTag());
                surname_acc_info.setFocusableInTouchMode(true);

                edit_info_btn.setVisibility(View.GONE);
                save_info_btn.setVisibility(View.VISIBLE);
            }
        });

        save_info_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (firstname_acc_info.getText().toString().equals(""))
                {
                    Toast.makeText(getActivity(),"Blank name field",Toast.LENGTH_SHORT).show();
                }
                else if (surname_acc_info.getText().toString().equals(""))
                {
                    Toast.makeText(getActivity(),"Blank surname field",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    progressDialog.setMessage("Applying changes");
                    progressDialog.show();

                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(firstname_acc_info.getText().toString()+" "+
                                    surname_acc_info.getText().toString())
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()){
                                        Toast.makeText(getActivity(),"Success",Toast.LENGTH_SHORT).show();
                                        fields_unavailable();
                                    }
                                    else
                                        Toast.makeText(getActivity(),"Error occurred",Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        check_if_signed();
        fields_unavailable();

        return v;
    }

    public void check_if_signed(){
        if(firebaseAuth.getCurrentUser() != null){

            String name = firebaseAuth.getCurrentUser().getDisplayName();
            String[] split_name = name.split("\\s+");
            String firstname = split_name[0];
            String surname = split_name[1];

            firstname_acc_info.setText(firstname);
            surname_acc_info.setText(surname);
        }
    }

    public void fields_unavailable(){
        // make edit texts unavailable
        firstname_acc_info.setTag(firstname_acc_info.getKeyListener());
        firstname_acc_info.setKeyListener(null);
        firstname_acc_info.setFocusableInTouchMode(false);

        surname_acc_info.setTag(surname_acc_info.getKeyListener());
        surname_acc_info.setKeyListener(null);
        surname_acc_info.setFocusableInTouchMode(false);

        // hide save btn show edit btn
        edit_info_btn.setVisibility(View.VISIBLE);
        save_info_btn.setVisibility(View.GONE);
    }
}
