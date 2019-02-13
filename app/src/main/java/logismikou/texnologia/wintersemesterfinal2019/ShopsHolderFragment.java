package logismikou.texnologia.wintersemesterfinal2019;


import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import static android.view.View.generateViewId;

public class ShopsHolderFragment extends Fragment {

    ImageView close_shop_holder;
    LinearLayout linear_layout;
    DatabaseReference get_shops;

    String[] reference_of_shop;
    ImageView[] list_of_shops;

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

    private ImageView create_image_view(String image_url, int id){
        // create a new image view
        ImageView imageView = new ImageView(getActivity());
        // load image_url
        Picasso.get().load(image_url).into(imageView);
        // set padding of images
        imageView.setPadding(0, 20, 0, 0);
        // set an id to know which image the user selects
        imageView.setId(id);
        // add imageview to the view
        linear_layout.addView(imageView);

        return imageView;
    }

    private void load_shops(){
        get_shops = FirebaseDatabase.getInstance().getReference().child("Products")
                .child("Shops");

        ValueEventListener get_popularListener = new ValueEventListener() {
            //initialize counter
            int count = 0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // get number of products to init array
                long number_of_products = dataSnapshot.getChildrenCount();
                int array_size = (int)number_of_products;
                // array that contains references to be passed to open ProductsHolderFragment
                reference_of_shop = new String[array_size];
                // array of image views created with create_image_view function
                list_of_shops = new ImageView[array_size];

                for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    // get reference string and save it
                    String shop_reference = postSnapshot.child("Categories").getRef().toString();
                    reference_of_shop[count] = shop_reference;

                    // get image banner for the specific shop and feet the create_image_view function
                    String image_url = postSnapshot.child("Image").getValue().toString();
                    list_of_shops[count] = create_image_view(image_url, count);

                    // add an onclick listener for every image view
                    list_of_shops[count].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int selected_shop = v.getId();
                            goto_shop_product_list(reference_of_shop[selected_shop]);
                        }
                    });
                    count++;
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

    public void goto_shop_product_list(String reference){
        ProductsHolderFragment fragment = new ProductsHolderFragment();
        //create an bundle to send reference string of a specific product
        Bundle arguments = new Bundle();
        arguments.putString("1234", reference);
        fragment.setArguments(arguments);
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_layout, fragment);
        ft.commit();
    }
}

