package com.example.walking;

public class Account {
	private int range;
	private int sph;

	public Account(){}
	public Account(int range, int sph){
		this.range = range;
		this.sph = sph;
	}

	public void setRange(int range){
		this.range = range;
	}
	public int getRange(){
		return range;
	}

	public void setSph(int sph){
		this.sph = sph;
	}
	public int getSph(){
		return sph;
	}
}
