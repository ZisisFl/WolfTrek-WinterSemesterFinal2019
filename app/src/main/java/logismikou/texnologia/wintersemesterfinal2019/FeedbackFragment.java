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

public class FeedbackFragment extends Fragment {

    Button submit_btn;
    EditText store_sug_text, gen_sug_text;
    ImageView close7;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_feedback, container, false);

        submit_btn = v.findViewById(R.id.submit_btn);

        store_sug_text = v.findViewById(R.id.store_sug_text);
        gen_sug_text = v.findViewById(R.id.gen_sug_text);

        close7 = v.findViewById(R.id.close7);

        close7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                        new FeedbackFragment()).commit();
            }
        });

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_feedback();
            }
        });

        return v;
    }

    public void send_feedback(){
        String store_suggestion = store_sug_text.getText().toString();
        String general_suggestion = gen_sug_text.getText().toString();

        if (!store_suggestion.equals("") && general_suggestion.equals("")){
            Toast.makeText(getActivity(), "con1", Toast.LENGTH_SHORT).show();
            //send store suggestion
        }
        else if (store_suggestion.equals("") && !general_suggestion.equals("")){
            Toast.makeText(getActivity(), "con2", Toast.LENGTH_SHORT).show();
            //send general suggestion
        }
        else if (!store_suggestion.equals("") && !general_suggestion.equals("")){
            Toast.makeText(getActivity(), "con3", Toast.LENGTH_SHORT).show();
            //send both
        }
    }
}
