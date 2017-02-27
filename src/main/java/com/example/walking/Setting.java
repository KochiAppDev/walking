package com.example.walking;

import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

//設定用のクラス
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
		this.context.findViewById(R.id.InputMode_button).setOnClickListener(this);
		rootbutton = (Button) this.context.findViewById(R.id.root_button);
		if(!this.account.isType()){
			this.context.findViewById(R.id.notice_button).setOnClickListener(this);
			rootbutton.setOnClickListener(this);
		}
		this.context.findViewById(R.id.name_button).setOnClickListener(this);
		this.context.findViewById(R.id.remove_button).setOnClickListener(this);
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
				context.setContentView(R.layout.root_select);
				context.findViewById(R.id.button).setOnClickListener(this);
				context.findViewById(R.id.button1).setOnClickListener(this);
				break;
			case R.id.name_button:
				MainActivity.nameSet.MysetContentView(account);
				break;
			case R.id.remove_button:
				String url = "https://kochi-app-dev-walking.herokuapp.com/remove";
				String req = "id=" + account.getID();
				HttpPost httpPost = new HttpPost();
				httpPost.execute(url,req);
				MainActivity.mDone = new CountDownLatch(1);
				try {
					MainActivity.mDone.await();
				} catch (InterruptedException e) {}
				JSONObject json = httpPost.jsonObject;
				context.onResume();
				break;
			case R.id.button:
				context.root.clear();
				context.myGPS.rootSet();
				context.rFlag = 1;
				context.set_rootID = account.getID();
				context.onResume();
				break;
			case  R.id.button1:
				context.root.clear();
				context.myGPS.rootSet();
				context.rFlag = 2;
				context.set_rootID = account.getID();
				context.onResume();
				break;
			default:
		}
	}
}
