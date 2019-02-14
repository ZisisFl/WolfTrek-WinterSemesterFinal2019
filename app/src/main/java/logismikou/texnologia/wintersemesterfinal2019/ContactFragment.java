package logismikou.texnologia.wintersemesterfinal2019;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ContactFragment extends Fragment {

    Button call_btn, email_btn, feedback_btn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contact, container, false);

        call_btn = v.findViewById(R.id.call_btn);
        email_btn = v.findViewById(R.id.email_btn);
        feedback_btn = v.findViewById(R.id.feedback_btn);

        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = "+125646337745";
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });

        email_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","abc@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contact Support");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello WolfTrek support im writing you...");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });

        feedback_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                        new FeedbackFragment()).commit();
            }
        });


        return v;
    }
}
