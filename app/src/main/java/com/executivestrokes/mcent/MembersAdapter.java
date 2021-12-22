package com.executivestrokes.mcent;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
public class MembersAdapter  extends RecyclerView.Adapter<MembersAdapter.ViewHolder>{
    private Context context;
    private List<phoneBookModel> userList;
    public MembersAdapter(Context context, List<phoneBookModel> userList) {
        this.context = context;
        this.userList = userList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.row_add_participants ,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final  phoneBookModel user = userList.get(position);

        holder.userName.setText(user.getParticipantsName());
        holder.userNo.setText(user.getParticipantsNumber());

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView userName, userNo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName   = itemView.findViewById(R.id.participantsName);
            userNo = itemView.findViewById(R.id.participantsphoneNumber);

        }
    }
}
