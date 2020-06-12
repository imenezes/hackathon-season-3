package example.com.hack_poi.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import example.com.hack_poi.Model.FinalDetails;
import example.com.hack_poi.R;

public class DisplayDetailsAdapter extends RecyclerView.Adapter<DisplayDetailsAdapter.MyViewHolder> {
    Context context;
    List<FinalDetails> detailsList;
    public DisplayDetailsAdapter(List<FinalDetails> detailsList, Context context) {
        this.detailsList = detailsList;
        this.context = context;
    }

    @Override
    public DisplayDetailsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.details_items_view, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DisplayDetailsAdapter.MyViewHolder holder, int position) {
        holder.txtMerchantName.setText(detailsList.get(position).getMerchant_name());
        holder.txtMerchantNumber.setText(detailsList.get(position).getMobileNumber());
    }

    @Override
    public int getItemCount() {
        return detailsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtMerchantName, txtMerchantNumber;
        public MyViewHolder(View itemView) {
            super(itemView);
            txtMerchantName=itemView.findViewById(R.id.txtMerchantName);
            txtMerchantNumber=itemView.findViewById(R.id.txtMerchantNumber);
        }
    }
}
