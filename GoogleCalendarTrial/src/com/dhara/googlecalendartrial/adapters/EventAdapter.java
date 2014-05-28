package com.dhara.googlecalendartrial.adapters;

import java.util.List;

import com.dhara.googlecalendartrial.R;
import com.google.api.services.calendar.model.Event;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class EventAdapter extends ArrayAdapter<Event>{
	private Context mContext;
	private int mResource;
	private List<Event> mEvents;
	
	public EventAdapter(Context context, int resource, List<Event> objects) {
		super(context, resource, objects);
		mContext = context;
		mResource = resource;
		mEvents = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder vh = null;
		
		if(view == null) {
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(mResource, parent, false);
			vh = new ViewHolder();
			vh.txtEventName = (TextView)view.findViewById(R.id.txtEventName);
			view.setTag(vh);
		}else {
			vh = (ViewHolder)view.getTag();
		}
		
		Event event = mEvents.get(position);
		vh.txtEventName.setText(event.getDescription());
		
		return view;
	}
	
	static class ViewHolder {
		TextView txtEventName;
	}
}
