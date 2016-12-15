package com.example.walking;

import android.view.View;

public class Setting implements View.OnClickListener {
	private MainActivity context;
	private Account account;

	public Setting(MainActivity context){
		this.context = context;
	}

	public void MysetContentView(Account account){
		this.account = account;
		this.context.setContentView(R.layout.setting);
		this.context.findViewById(R.id.notice_button).setOnClickListener(this);
		this.context.findViewById(R.id.InputMode_button).setOnClickListener(this);
		this.context.findViewById(R.id.root_button).setOnClickListener(this);
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
				break;
			case R.id.name_button:
				MainActivity.nameSet.MysetContentView(account);
				break;
			default:
		}
	}
}
