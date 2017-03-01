package com.example.walking;


import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

//名前の設定用のクラス
public class NameSet implements View.OnClickListener {
	public MainActivity context;
	private Account account = null;
	private EditText name;
	private GridView icon;
	private int icnum = -1;

	public NameSet(MainActivity context){
		this.context = context;
	}

	public void MysetContentView(){
		this.context.setContentView(R.layout.activity_sub);
		account = new Account(Integer.valueOf(MainActivity.sp.getString("userID","-1")),"",true,0);
		this.context.findViewById(R.id.name_decision).setOnClickListener(this);
		name = (EditText) this.context.findViewById(R.id.name_editText);
		icon = (GridView)this.context.findViewById(R.id.icon_List);
		image();
	}
	public void MysetContentView(Account account){
		this.account = account;
		this.context.setContentView(R.layout.activity_sub);
		name = (EditText) this.context.findViewById(R.id.name_editText);
		icon = (GridView)this.context.findViewById(R.id.icon_List);
		name.setText(account.getUsname());
		icnum = account.getIcon();
		this.context.findViewById(R.id.name_decision).setOnClickListener(this);
		image();
	}

	//アイコンの表示
	private void image(){
		ArrayList<Integer> list = new ArrayList<>();
		for(int i=0; i<8; i++){
			list.add(i);
		}
		ImageArrayAdapter adapter = new ImageArrayAdapter(context, R.layout.listchild, list, icnum);
		icon.setAdapter(adapter);
		icon.setNumColumns(4);
		icon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				if(icnum > -1){
					icon.getChildAt(icnum).setBackgroundColor(Color.WHITE);
				}
				icnum = position;
				icon.getChildAt(icnum).setBackgroundColor(Color.RED);
			}
		});
	}

	@Override
	public void onClick(View v) {
		if(name.getText().length() != 0 && icnum != -1){
			int id = account.getID();
			if(id < 0){return;}
			String url;
			String req;
			if(MainActivity.sp.getBoolean("InitState", true)) {
				url = "https://kochi-app-dev-walking.herokuapp.com/init";
				req = "id=" + id + "&nm=" + name.getText() + "&tp="+ MainActivity.sp.getInt("tp", 0) +"&ic=" + icnum;
			}else{
				url = "https://kochi-app-dev-walking.herokuapp.com/config";
				req = "id=" + id + "&nm=" + name.getText() +"&ic=" + icnum;
			}
			HttpPost httpPost = new HttpPost();
			httpPost.execute(url,req);
			MainActivity.mDone = new CountDownLatch(1);
			try {
				MainActivity.mDone.await();
			} catch (InterruptedException e) {
			}
			MainActivity.sp.edit().putBoolean("InitState", false).apply();
			context.onResume();
		}
	}
}
