package logismikou.texnologia.wintersemesterfinal2019;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class NotSignedFragment extends Fragment {

    Button goto_sign_in2, goto_sign_up2;
    ImageView close6;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_not_signed, container, false);

        goto_sign_in2 = v.findViewById(R.id.goto_sign_in2);
        goto_sign_up2 = v.findViewById(R.id.goto_sign_up2);

        close6 = v.findViewById(R.id.close6);

        goto_sign_in2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                        new SignInFragment()).commit();
            }
        });

        goto_sign_up2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                        new SignUpFragment()).commit();
            }
        });

        close6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                        new AccountFragment()).commit();
            }
        });

        return v;
    }
}
