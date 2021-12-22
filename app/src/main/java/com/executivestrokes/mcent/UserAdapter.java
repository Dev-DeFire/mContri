package com.executivestrokes.mcent;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter  extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private Context context;
    private List<GroupModel> userList;
    UserRecyclerViewOnClickListner listner;

    public UserAdapter(Context context, List<GroupModel> userList, UserRecyclerViewOnClickListner listner) {
        this.context = context;
        this.userList = userList;
        this.listner=listner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.upcoming_plans_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final  GroupModel user = userList.get(position);

        holder.tenure.setText(user.getpDuration());
        holder.userGrpTitle.setText(user.getGroupTitle());
        holder.amount.setText(user.gettAmount());
        holder.grpMembers.setText(user.getTotalMember());
        holder.startDate.setText(user.getStartDate());
        holder.endDate.setText(user.getEndDate());
        holder.itemView.setOnClickListener(view ->{
            listner.onIttemClick(user.getGroupTitle(),user.getGroupID(),user.getGroupDescription(),user.gettAmount(),user.getTotalMember(),user.getpDuration(),user.getStartDate(),user.getEndDate());
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView userGrpTitle, grpMembers,tenure,amount,startDate,endDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userGrpTitle   = itemView.findViewById(R.id.name);
            grpMembers = itemView.findViewById(R.id.members);
            tenure=itemView.findViewById(R.id.monthly);
            amount=itemView.findViewById(R.id.amount_up);
            startDate=itemView.findViewById(R.id.start_date);
            endDate=itemView.findViewById(R.id.end_date);

        }
    }
    public interface UserRecyclerViewOnClickListner{
        void onIttemClick(String name,String UID,String desc,String amount,String totalMembers,String pDuration,String sDate,String eDate);
    }
}
