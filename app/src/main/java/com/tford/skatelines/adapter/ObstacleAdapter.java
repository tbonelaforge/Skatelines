package com.tford.skatelines.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tford.skatelines.R;
import com.tford.skatelines.model.Obstacle;
import com.tford.skatelines.model.Session;
import com.tford.skatelines.model.Skill;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tford on 1/22/17.
 */

public class ObstacleAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Obstacle> obstacles = new ArrayList<Obstacle>();

    public ObstacleAdapter(Context context, List<Obstacle> obstacles) {
        this.obstacles = obstacles;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Obstacle obstacle = (Obstacle) getItem(position);
        if (view == null) {
            view = inflater.inflate(R.layout.obstacle_data, null);
        }
        TextView obstacleIdView = (TextView) view.findViewById(R.id.obstacle_id_data);
        obstacleIdView.setText(String.valueOf(obstacle.getId()));
        TextView obstacleDescriptionView = (TextView) view.findViewById(R.id.obstacle_description_data);
        obstacleDescriptionView.setText(obstacle.getDescription());
        return view;
    }

    @Override
    public Object getItem(int position) {
        return obstacles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return obstacles.size();
    }

    public void setObstacles(List<Obstacle> data) {
        obstacles = new ArrayList<Obstacle>();
        obstacles.addAll(data);
        notifyDataSetChanged();
    }
}