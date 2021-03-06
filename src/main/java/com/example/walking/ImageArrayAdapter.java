package com.example.walking;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
	private Context context;
	private int icnum = -1;

	public ImageArrayAdapter(Context context, int resourceId, ArrayList<Integer> items) {
		super(context, resourceId, items);

		this.context = context;
		this.resourceId = resourceId;
		this.items = items;
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public ImageArrayAdapter(Context context, int resourceId, ArrayList<Integer> items, int icnum) {
		super(context, resourceId, items);
		this.context = context;
		this.resourceId = resourceId;
		this.items = items;
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.icnum = icnum;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView != null) {
			imageView = (ImageView)convertView;
		} else {
			imageView = (ImageView)this.inflater.inflate(this.resourceId, null);
		}

		Resources r = context.getResources();
		Bitmap item = BitmapFactory.decodeResource(r,MainActivity.color[items.get(position)]);

		if(icnum == position){
			imageView.setBackgroundColor(Color.RED);
		}

		// アイコンをセット
		imageView.setImageBitmap(item);
		return imageView;
	}
}
