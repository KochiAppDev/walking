package com.example.walking;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RadioGroup;

public class Explanation implements View.OnClickListener {

	private MainActivity context;

	public Explanation(MainActivity context){
		this.context = context;
	}

	public void MysetContentView(){
		this.context.setContentView(R.layout.explanation);
		this.context.findViewById(R.id.title_decision).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.title_decision){
			context.setContentView(R.layout.activity_main);
			context.findViewById(R.id.selection_decision).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(v.getId() == R.id.selection_decision){
						RadioGroup radioGroup = (RadioGroup) context.findViewById(R.id.radio);
						int checkedId = radioGroup.getCheckedRadioButtonId();
						if (-1 != checkedId) {
							if (checkedId == R.id.type0) {
								MainActivity.sp.edit().putInt("tp", 0).apply();
							} else {
								MainActivity.sp.edit().putInt("tp", 1).apply();
							}
							MainActivity.nameSet.MysetContentView();
						}
					}
				}
			});
		}
	}
}
