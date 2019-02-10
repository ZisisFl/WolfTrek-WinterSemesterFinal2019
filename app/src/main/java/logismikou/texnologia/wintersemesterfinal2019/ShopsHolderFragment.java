package logismikou.texnologia.wintersemesterfinal2019;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static android.support.constraint.Constraints.TAG;

public class ShopsHolderFragment extends Fragment {

    ImageView close_shop_holder;
    LinearLayout linear_layout;
    DatabaseReference get_shops;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shops_holder, container, false);

        linear_layout = v.findViewById(R.id.linear_layout);
        close_shop_holder = v.findViewById(R.id.close_shop_holder);

        // start loading brand images
        load_shops();

        close_shop_holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                        new ShopFragment()).commit();
            }
        });

        return v;
    }

    private void create_image_view(String image_url){
        ImageView imageView = new ImageView(getActivity());

        //imageView.setImageResource(R.drawable.ic_broken_image);
        Picasso.get().load(image_url).into(imageView);

        imageView.setPadding(0, 20, 0, 0);
        linear_layout.addView(imageView);
    }

    private void load_shops(){
        get_shops = FirebaseDatabase.getInstance().getReference().child("Products")
                .child("Shops");

        ValueEventListener get_popularListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    String image_url = postSnapshot.child("Image").getValue().toString();
                    create_image_view(image_url);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        get_shops.addValueEventListener(get_popularListener);
    }
}

