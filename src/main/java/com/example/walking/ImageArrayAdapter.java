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

//ImageをListに入れるためのAdapterクラス
public class ImageArrayAdapter extends ArrayAdapter<Integer>{
	private int resourceId;
	private ArrayList<Integer> items;
	private LayoutInflater inflater;

	public ImageArrayAdapter(Context context, int resourceId, ArrayList<Integer> items) {
		super(context, resourceId, items);

		this.resourceId = resourceId;
		this.items = items;
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView != null) {
			imageView = (ImageView)convertView;
		} else {
			imageView = (ImageView)this.inflater.inflate(this.resourceId, null);
		}

		Bitmap item = MainActivity.color[items.get(position)];

		// アイコンをセット
		imageView.setImageBitmap(item);
		return imageView;
	}
}
