package logismikou.texnologia.wintersemesterfinal2019;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class ProductsHolderFragment extends Fragment {

    RecyclerView product_list;
    DatabaseReference databaseReference;
    FirebaseRecyclerOptions<ProductInfo> options;
    FirebaseRecyclerAdapter<ProductInfo, ProductViewHolder> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_products_holder, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products").
                child("Shops").child("Everest").child("Categories").child("Drinks");// na prosthesw

        product_list = v.findViewById(R.id.product_list);
        product_list.setHasFixedSize(true);

        options = new FirebaseRecyclerOptions.Builder<ProductInfo>()
                .setQuery(databaseReference, ProductInfo.class).build();

        adapter = new FirebaseRecyclerAdapter<ProductInfo, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull ProductInfo model) {

                Picasso.get().load(model.getImage_url()).into(holder.product_image_view, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                holder.description_view.setText(model.getDescription());
                holder.title_view.setText(model.getName());
                holder.price_view.setText(String.valueOf(model.getPrice())+" €");
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate
                        (R.layout.product_cardview, parent, false);

                return new ProductViewHolder(view);
            }
        };
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        product_list.setLayoutManager(mLayoutManager);
        adapter.startListening();
        product_list.setAdapter(adapter);

        return v;
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder
    {
        TextView description_view, title_view, price_view;
        ImageView product_image_view;

        public ProductViewHolder(View itemView)
        {
            super(itemView);

            description_view = itemView.findViewById(R.id.description_view);
            title_view = itemView.findViewById(R.id.title_view);
            price_view = itemView.findViewById(R.id.price_view);
            product_image_view = itemView.findViewById(R.id.product_image_view);
        }

    }
}
