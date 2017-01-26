package com.example.walking;

import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class Setting implements View.OnClickListener {
	private MainActivity context;
	private Account account;
	private Button rootbutton;

	public Setting(MainActivity context){
		this.context = context;
	}

	public void MysetContentView(Account account){
		this.account = account;
		this.context.setContentView(R.layout.setting);
		this.context.findViewById(R.id.notice_button).setOnClickListener(this);
		this.context.findViewById(R.id.InputMode_button).setOnClickListener(this);
		rootbutton = (Button) this.context.findViewById(R.id.root_button);
		rootbutton.setOnClickListener(this);
		this.context.findViewById(R.id.name_button).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.notice_button:
				MainActivity.notice.MysetContentView(account);
				break;
			case R.id.InputMode_button:
				break;
			case R.id.root_button:
				if(context.rFlag){
					context.rFlag = false;
					rootbutton.setText("ルートを設定する");
					double[][] rootArray = new double[context.root.size()][2];
					for(int i=0; i<rootArray.length; i++){
						rootArray[i] = context.root.get(i);
					}
					String url = "https://kochi-app-dev-walking.herokuapp.com/route";
					String req = "id=" + MainActivity.sp.getString("userID","-1") + "&rt=" + rootArray;
					HttpPost httpPost = new HttpPost();
					httpPost.execute(url,req);
					MainActivity.mDone = new CountDownLatch(1);
					try {
						MainActivity.mDone.await();
					} catch (InterruptedException e) {}
					JSONObject json = httpPost.jsonObject;
				}else {
					context.rFlag = true;
					context.root.clear();
					rootbutton.setText("ルートを設定中");
				}
				context.onResume();
				break;
			case R.id.name_button:
				MainActivity.nameSet.MysetContentView(account);
				break;
			default:
		}
	}
}
