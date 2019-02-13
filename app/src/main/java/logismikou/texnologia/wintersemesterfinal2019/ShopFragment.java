package logismikou.texnologia.wintersemesterfinal2019;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static android.support.constraint.Constraints.TAG;

public class ShopFragment extends Fragment {

    ImageView prodImage1, prodImage2, prodImage3, prodImage4, prodImage5, prodImage6;
    TextView product_name1, product_name2, product_name3, product_name4, product_name5,
            product_name6, product_price1, product_price2, product_price3, product_price4,
            product_price5, product_price6;
    CardView cardView1, cardView2, cardView3, cardView4, cardView5, cardView6;

    String[] reference_pop_product = new String[6];


    DatabaseReference get_popular;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shop, container, false);

        prodImage1 = v.findViewById(R.id.prodImage1);
        prodImage2 = v.findViewById(R.id.prodImage2);
        prodImage3 = v.findViewById(R.id.prodImage3);
        prodImage4 = v.findViewById(R.id.prodImage4);
        prodImage5 = v.findViewById(R.id.prodImage5);
        prodImage6 = v.findViewById(R.id.prodImage6);

        product_name1 = v.findViewById(R.id.product_name1);
        product_name2 = v.findViewById(R.id.product_name2);
        product_name3 = v.findViewById(R.id.product_name3);
        product_name4 = v.findViewById(R.id.product_name4);
        product_name5 = v.findViewById(R.id.product_name5);
        product_name6 = v.findViewById(R.id.product_name6);

        product_price1 = v.findViewById(R.id.product_price1);
        product_price2 = v.findViewById(R.id.product_price2);
        product_price3 = v.findViewById(R.id.product_price3);
        product_price4 = v.findViewById(R.id.product_price4);
        product_price5 = v.findViewById(R.id.product_price5);
        product_price6 = v.findViewById(R.id.product_price6);

        cardView1 = v.findViewById(R.id.cardView1);
        cardView2 = v.findViewById(R.id.cardView2);
        cardView3 = v.findViewById(R.id.cardView3);
        cardView4 = v.findViewById(R.id.cardView4);
        cardView5 = v.findViewById(R.id.cardView5);
        cardView6 = v.findViewById(R.id.cardView6);

        display_pop_product_info();
        choose_from_popular();

        return v;
    }

    public void display_pop_product_info(){
        get_popular = FirebaseDatabase.getInstance().getReference().child("Products")
                    .child("Popular");

        ValueEventListener get_popularListener = new ValueEventListener() {
            //initialize counter
            int count = 0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    // Get PopularProducts object and use the values to update the UI
                    PopularProducts popularProducts = postSnapshot.getValue(PopularProducts.class);

                    //loop through children and put info in a different cardviews
                    load_in_components(count, popularProducts);
                    //save reference of the product
                    reference_pop_product[count] = popularProducts.reference;
                    //update counter
                    count ++;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        get_popular.addValueEventListener(get_popularListener);
    }

    private void load_in_components(int number, PopularProducts popularProducts){
        switch (number){
            case 0:
                Picasso.get().load(popularProducts.image_url).into(prodImage1);
                product_name1.setText(popularProducts.name);
                product_price1.setText(popularProducts.price+" €");
                break;
            case 1:
                Picasso.get().load(popularProducts.image_url).into(prodImage2);
                product_name2.setText(popularProducts.name);
                product_price2.setText(popularProducts.price+" €");
                break;
            case 2:
                Picasso.get().load(popularProducts.image_url).into(prodImage3);
                product_name3.setText(popularProducts.name);
                product_price3.setText(popularProducts.price+" €");
                break;
            case 3:
                Picasso.get().load(popularProducts.image_url).into(prodImage4);
                product_name4.setText(popularProducts.name);
                product_price4.setText(popularProducts.price+" €");
                break;
            case 4:
                Picasso.get().load(popularProducts.image_url).into(prodImage5);
                product_name5.setText(popularProducts.name);
                product_price5.setText(popularProducts.price+" €");
                break;
            case 5:
                Picasso.get().load(popularProducts.image_url).into(prodImage6);
                product_name6.setText(popularProducts.name);
                product_price6.setText(popularProducts.price+" €");
                break;
        }
    }

    public void choose_from_popular(){
        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ref_number = 0;
                goto_product_preview(ref_number);
            }
        });

        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ref_number = 1;
                goto_product_preview(ref_number);
            }
        });

        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ref_number = 2;
                goto_product_preview(ref_number);
            }
        });

        cardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ref_number = 3;
                goto_product_preview(ref_number);
            }
        });

        cardView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ref_number = 4;
                goto_product_preview(ref_number);
            }
        });

        cardView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ref_number = 5;
                goto_product_preview(ref_number);
            }
        });
    }

    private void goto_product_preview(int ref_number){
        ProductPreviewFragment fragment = new ProductPreviewFragment();
        //create an bundle to send reference string of the specific product
        Bundle arguments = new Bundle();
        arguments.putString("123", reference_pop_product[ref_number]);
        fragment.setArguments(arguments);
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_layout, fragment);
        ft.commit();
    }
}
