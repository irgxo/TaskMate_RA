package net.penguincoders.doit;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import net.penguincoders.doit.Adapters.ToDoAdapter;


public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private ToDoAdapter adapter;

    public RecyclerItemTouchHelper(ToDoAdapter adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.LEFT) {
            showDeleteConfirmationDialog(position);
        } else {
            adapter.editItem(position);
        }
    }

    private void showDeleteConfirmationDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(adapter.getContext());
        builder.setTitle("Delete Task");
        builder.setMessage("Are you sure you want to delete this Task?");
        builder.setPositiveButton("Confirm", (dialog, which) -> adapter.deleteItem(position));
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> adapter.notifyItemChanged(position));
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;
        drawBackgroundAndIcon(c, itemView, dX);
    }

    private void drawBackgroundAndIcon(Canvas c, View itemView, float dX) {
        Drawable icon;
        if (dX > 0) {
            icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_baseline_edit);
            drawRightSwipeBackgroundAndIcon(c, itemView, dX, icon);
        } else if (dX < 0) {
            icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_baseline_delete);
            drawLeftSwipeBackgroundAndIcon(c, itemView, dX, icon);
        }
    }

    private void drawRightSwipeBackgroundAndIcon(Canvas c, View itemView, float dX, Drawable icon) {
        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        // Draw background
        drawBackground(c, itemView, dX, iconMargin, icon.getIntrinsicWidth(), true);
        // Draw icon
        drawIcon(c, itemView, dX, icon, iconMargin, iconMargin);
    }

    private void drawLeftSwipeBackgroundAndIcon(Canvas c, View itemView, float dX, Drawable icon) {
        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        // Draw background
        drawBackground(c, itemView, dX, iconMargin, icon.getIntrinsicWidth(), false);
        // Draw icon
        drawIcon(c, itemView, dX, icon, iconMargin, -iconMargin - icon.getIntrinsicWidth());
    }

    private void drawBackground(Canvas c, View itemView, float dX, int iconMargin, int iconWidth, boolean isRightSwipe) {
        ColorDrawable background = new ColorDrawable(isRightSwipe ? ContextCompat.getColor(adapter.getContext(), R.color.colorPrimaryDark) : Color.RED);
        int backgroundCornerOffset = 20;
        background.setBounds(isRightSwipe ? itemView.getLeft() : itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                itemView.getTop(), isRightSwipe ? itemView.getLeft() + ((int) dX) + backgroundCornerOffset : itemView.getRight(), itemView.getBottom());
        background.draw(c);
    }

    private void drawIcon(Canvas c, View itemView, float dX, Drawable icon, int iconMargin, int iconLeft) {
        int iconTop = itemView.getTop() + iconMargin;
        int iconBottom = iconTop + icon.getIntrinsicHeight();
        int iconRight = itemView.getRight() - iconMargin;
        int iconLeftAdjusted = isRightSwipe(dX) ? itemView.getLeft() + iconMargin : iconLeft;
        icon.setBounds(iconLeftAdjusted, iconTop, iconRight, iconBottom);
        icon.draw(c);
    }

    private boolean isRightSwipe(float dX) {
        return dX > 0;
    }
}
