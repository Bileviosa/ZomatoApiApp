package com.uas.restaurantsearch.adapters;
import androidx.paging.PagedListAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import 	androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uas.restaurantsearch.R;
import com.uas.restaurantsearch.comp.PicassoTransformation;
import com.uas.restaurantsearch.models.Restaurants;
import com.uas.restaurantsearch.models.Utility;
import com.squareup.picasso.Picasso;


public class RestaurantPagedListAdapter extends PagedListAdapter<Restaurants, RestaurantPagedListAdapter.RestaurantViewHolder> {

    AdapterInterface adapterInterface;
    Context context;

    public RestaurantPagedListAdapter(AdapterInterface adapterInterface)
    {
        super(DIFF_CALLBACK);
        this.adapterInterface = adapterInterface;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.restaurant_row_item, viewGroup, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int i) {
        Restaurants.Restaurant restaurant = getItem(i).getRestaurant();

        holder.name.setText(restaurant.getName());
        if(Utility.isValidStr(restaurant.getThumb()))
            Picasso.get().load(restaurant.getThumb()).placeholder(R.drawable.ic_restaurant_black_24dp).transform(new PicassoTransformation()).into(holder.imageView);

        holder.cuisines.setText(restaurant.getCuisines());
        holder.locality.setText(restaurant.getLocation().getLocality_verbose());

        holder.rating.setText(restaurant.getUser_rating().getAggregate_rating());
        Drawable drawable = holder.rating.getBackground();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.parseColor("#" + restaurant.getUser_rating().getRating_color()));
        holder.rating.setBackground(drawable);
        if(restaurant.getIs_delivering_now().equals("1"))
            holder.onlinedeliv.setVisibility(View.VISIBLE);
        else
            holder.onlinedeliv.setVisibility(View.GONE);

        if(Utility.isValidStr(restaurant.getAverage_cost_for_two())) {
            holder.avgCost.setText(context.getString(R.string.avg_cost_short, restaurant.getCurrency()+" "+restaurant.getAverage_cost_for_two()));
        }
        else
        {
            holder.avgCost.setText(context.getString(R.string.avg_cost_short, "1000"));
        }
        holder.mView.setTag(restaurant);
        holder.mView.setOnClickListener(clickListener);
    }

    public class RestaurantViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imageView;
        TextView name, cuisines, locality, rating, avgCost,onlinedeliv;
        View mView;

        public RestaurantViewHolder(View view)
        {
            super(view);
            imageView = view.findViewById(R.id.image);
            name = view.findViewById(R.id.name);
            avgCost = view.findViewById(R.id.avg_cost);
            locality = view.findViewById(R.id.locality);
            cuisines = view.findViewById(R.id.cuisines);
            rating = view.findViewById(R.id.ratings);
            onlinedeliv = view.findViewById(R.id.onlinedeliv);
            mView = view;
        }
    }

    public static DiffUtil.ItemCallback<Restaurants> DIFF_CALLBACK = new DiffUtil.ItemCallback<Restaurants>()
    {
        @Override
        public boolean areItemsTheSame(@NonNull Restaurants oldItem, @NonNull Restaurants newItem)
        {
            return oldItem.getRestaurant().getId() == newItem.getRestaurant().getId();
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Restaurants oldItem, @NonNull Restaurants newItem)
        {
            return oldItem.equals(newItem);
        }
    };

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Restaurants.Restaurant restaurant = (Restaurants.Restaurant) view.getTag();
            adapterInterface.onClick(restaurant);
        }
    };
}
