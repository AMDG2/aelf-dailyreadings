package co.epitre.aelf_lectures.bible;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import co.epitre.aelf_lectures.R;

public class BibleBookListAdapter extends RecyclerView.Adapter<BibleBookListAdapter.ViewHolder> {
    /**
     * Book list
     */
    private BiblePart mBiblePart;

    BibleBookListAdapter(@NonNull BiblePart biblePart) {
        mBiblePart = biblePart;
    }

    @Override
    public int getItemViewType(int position) {
        BibleBookEntry bookEntry = mBiblePart.getBibleBookEntries().get(position);
        return bookEntry.getType().getValue();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Decode item type
        BibleBookEntryType bookEntryType = BibleBookEntryType.fromValue(viewType);

        // Create a view holder of this type
        int layoutId = R.layout.item_bible_book_name;
        switch (bookEntryType) {
            case SECTION:
                layoutId = R.layout.item_bible_section_name;
                break;
            case BOOK:
                layoutId = R.layout.item_bible_book_name;
                break;
        }
        View v = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BibleBookEntry bookEntry = mBiblePart.getBibleBookEntries().get(position);

        // Set the Title
        TextView textView = holder.itemView.findViewById(R.id.title);
        textView.setText(bookEntry.getName());
    }

    @Override
    public int getItemCount() {
        return mBiblePart.getBibleBookEntries().size();
    }

    // When an item is a title, the grid layout will give it a ful row
    boolean isTitle(int position) {
        if (position >= getItemCount()) {
            return false;
        }

        return mBiblePart.getBibleBookEntries().get(position).getType() == BibleBookEntryType.SECTION;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
