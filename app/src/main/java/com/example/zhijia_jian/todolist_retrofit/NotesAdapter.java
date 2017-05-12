package com.example.zhijia_jian.todolist_retrofit;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private NoteClickListener clickListener;
    private List<Note> dataset;

    public interface NoteClickListener {
        void onNoteClick(int position);
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView text;
        //public TextView comment;

        public NoteViewHolder(View itemView, final NoteClickListener clickListener) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.textTV);
            //comment = (TextView) itemView.findViewById(R.id.timeTV);
            title =(TextView)  itemView.findViewById(R.id.titleTV);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        clickListener.onNoteClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    public NotesAdapter(NoteClickListener clickListener) {
        this.clickListener = clickListener;
        this.dataset = new ArrayList<Note>();
    }

    public void setNotes(@NonNull List<Note> notes) {
        dataset = notes;
        notifyDataSetChanged();
    }

    public Note getNote(int position) {
        return dataset.get(position);
    }

    @Override
    public NotesAdapter.NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(NotesAdapter.NoteViewHolder holder, int position) {
        Note note = dataset.get(position);
        holder.title.setText(note.getTitle());
        holder.text.setText(note.getText());
        //holder.comment.setText(note.getComment());
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}