package com.tford.skatelines.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tford.skatelines.R;
import com.tford.skatelines.model.Session;
import com.tford.skatelines.model.Skill;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tford on 1/22/17.
 */

public class SkillAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Skill> skills = new ArrayList<Skill>();

    public SkillAdapter(Context context, List<Skill> skills) {
        this.skills = skills;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Skill skill = (Skill) getItem(position);
        if (view == null) {
            view = inflater.inflate(R.layout.skill_data, null);
        }
        TextView skillIdView = (TextView) view.findViewById(R.id.skill_id_data);
        skillIdView.setText(String.valueOf(skill.getId()));
        TextView skillDescriptionView = (TextView) view.findViewById(R.id.skill_description_data);
        skillDescriptionView.setText(skill.getDescription());
        return view;
    }

    @Override
    public Object getItem(int position) {
        return skills.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return skills.size();
    }

    public void setSkills(List<Skill> data) {
        skills = new ArrayList<Skill>();
        skills.addAll(data);
        notifyDataSetChanged();
    }
}
