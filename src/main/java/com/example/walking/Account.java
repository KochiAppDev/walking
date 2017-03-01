package com.example.walking;

//ユーザー情報の登録用のクラス
public class Account {
	private int ID;
	private String usname;
	private boolean type;
	private int icon;
	private int groupID;
	private int range;
	private int sph;
	private double lat;
	private double lon;
	private double[][] rt;
	private String ts;

	public Account(int ID, String usname, boolean type, int icon){
		this.ID = ID;
		this.usname = usname;
		this.type = type;
		this.icon = icon;
	}

	public int getID() {
		return ID;
	}

	public void setID(int ID) {
		this.ID = ID;
	}

	public String getUsname() {
		return usname;
	}

	public void setUsname(String usname) {
		this.usname = usname;
	}

	public boolean isType() {
		return type;
	}

	public void setType(boolean type) {
		this.type = type;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public int getGroupID() {
		return groupID;
	}

	public void setGroupID(int groupID) {
		this.groupID = groupID;
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

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double[][] getRt() {
		return rt;
	}

	public void setRt(double[][] rt) {
		this.rt = rt;
	}

	public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}
}
