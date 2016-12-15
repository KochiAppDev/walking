package com.example.walking;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;

public class ImageArrayAdapter extends ArrayAdapter<Bitmap>{
	private int resourceId;
	private ArrayList<Bitmap> items;
	private LayoutInflater inflater;

	public ImageArrayAdapter(Context context, int resourceId, ArrayList<Bitmap> items) {
		super(context, resourceId, items);

		this.resourceId = resourceId;
		this.items = items;
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		if (convertView != null) {
			view = convertView;
		} else {
			view = this.inflater.inflate(this.resourceId, null);
		}

		Bitmap item = this.items.get(position);

		// アイコンをセット
		ImageView appInfoImage = (ImageView)view.findViewById(R.id.image);
		appInfoImage.setImageBitmap(item);

		return view;
	}
}
