package com.tford.skatelines;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tford on 12/28/16.
 */

public class SessionAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Session> sessions = new ArrayList<Session>();

    private static SimpleDateFormat dateDisplayFormat = new SimpleDateFormat("MM-dd");

    public SessionAdapter(Context context, List<Session> sessions) {
        this.sessions = sessions;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Session session = (Session) getItem(position);
        if (view == null) {
            view = inflater.inflate(R.layout.session_data, null);
        }
        TextView sessionLineIdView = (TextView) view.findViewById(R.id.session_line_id_data);
        sessionLineIdView.setText(session.getLine().getDescription());
        TextView sessionDateView = (TextView) view.findViewById(R.id.session_date_data);
        String sessionDateText = dateDisplayFormat.format(session.getDate());
        sessionDateView.setText(sessionDateText);
        return view;
    }

    @Override
    public Object getItem(int position) {
        return sessions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return sessions.size();
    }

    public void setSessions(List<Session> data) {
        sessions = new ArrayList<Session>();
        sessions.addAll(data);
        notifyDataSetChanged();
    }
}
