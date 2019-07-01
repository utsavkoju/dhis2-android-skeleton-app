package com.example.android.androidskeletonapp.ui.enrollment_form;

import android.view.ViewGroup;

import com.example.android.androidskeletonapp.data.service.forms.FormField;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FormAdapter extends RecyclerView.Adapter<FieldHolder> {

    private List<FormField> fields;

    public FormAdapter(OnValueSaved valueSavedListener) {
        this.fields = new ArrayList<>();
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return fields.get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return fields.size();
    }

    public void updateData(List<FormField> updates) {
        // TODO update data
    }

    @NonNull
    @Override
    public FieldHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // TODO Create view holder depending on the field value type
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull FieldHolder holder, int position) {
        // TODO bind view holder
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface OnValueSaved {
        void onValueSaved(String fieldUid, String value);
    }
}
