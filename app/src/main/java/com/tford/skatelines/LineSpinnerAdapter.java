package com.tford.skatelines;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tford on 12/28/16.
 */

public class LineSpinnerAdapter extends BaseAdapter {
    private List<Line> lines;
    private LayoutInflater layoutInflater;


    public LineSpinnerAdapter(Context context, List<Line> lines) {
        this.lines = lines;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Line line = (Line) getItem(position);
        if (view == null) {
            view = layoutInflater.inflate(R.layout.line_spinner_item, null);
        }
        ((TextView) view).setText(line.getDescription());
        return view;
    }

    @Override
    public Object getItem(int position) {
        return lines.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return lines.size();
    }

    public void setLines(List<Line> data) {
        lines = new ArrayList<Line>();
        lines.addAll(data);
        notifyDataSetChanged();
    }

}
