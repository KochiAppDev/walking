package com.example.walking;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;

public class NameSet implements View.OnClickListener {
	private MainActivity context;
	private Account account = null;

	public NameSet(MainActivity context){
		this.context = context;
	}

	public void MysetContentView(){
		this.context.setContentView(R.layout.activity_sub);
		this.context.findViewById(R.id.name_decision).setOnClickListener(this);
	}
	public void MysetContentView(Account account){
		this.account = account;
		this.context.setContentView(R.layout.activity_sub);
		this.context.findViewById(R.id.name_decision).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.name_decision){
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
			int tp = sp.getInt("tp",-1);
			if(tp == 0 && account == null){
				MainActivity.notice.MysetContentView();
			}else{
				sp.edit().putBoolean("InitState", false).apply();
				context.onResume();
			}
		}
	}
}
