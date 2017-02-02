package com.example.walking;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.Spinner;

//設定用のクラス
class Notice implements View.OnClickListener,CompoundButton.OnCheckedChangeListener {

	private MainActivity context;
	private boolean flag;
	private  Account account;
	private Spinner distance_spinner;
	private Spinner speed_spinner;
	private CompoundButton distance_switch;
	private CompoundButton speed_switch;

	public Notice(MainActivity context){
		this.context = context;
	}

	public void MysetContentView(Account account){
		set();
		this.account = account;
		distance_spinner.setSelection(account.getRange());
		speed_spinner.setSelection(account.getSph());
	}

	//スピナーやスイッチの登録
	private void set(){
		this.context.setContentView(R.layout.notice);
		this.context.findViewById(R.id.notice_decision).setOnClickListener(this);
		distance_spinner = (Spinner)this.context.findViewById(R.id.distance_spinner);
		speed_spinner = (Spinner)this.context.findViewById(R.id.speed_spinner);
		distance_switch = (CompoundButton)this.context.findViewById(R.id.distance_switch);
		distance_switch.setOnCheckedChangeListener(this);
		speed_switch = (CompoundButton)this.context.findViewById(R.id.speed_switch);
		speed_switch.setOnCheckedChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.notice_decision){
			int range = distance_spinner.getSelectedItemPosition();
			int sph = speed_spinner.getSelectedItemPosition();
			boolean ds = distance_switch.isChecked();
			boolean ss = speed_switch.isChecked();
			MainActivity.sp.edit().putInt("距離", range).apply();
			MainActivity.sp.edit().putInt("時速", sph).apply();
			MainActivity.sp.edit().putBoolean("距離", ds).apply();
			MainActivity.sp.edit().putBoolean("時速", ss).apply();
			MainActivity.sp.edit().putBoolean("InitState", false).apply();
			context.onResume();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			buttonView.setText("オン");
		} else {
			buttonView.setText("オフ");
		}
	}
}
