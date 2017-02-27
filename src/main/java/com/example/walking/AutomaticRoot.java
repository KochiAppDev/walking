package com.example.walking;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AutomaticRoot extends AsyncTask<String, Void, Void> {

	private JSONObject jsonObject;
	public double[][] rootArray;

	@Override
	protected Void doInBackground(String... params) {
		HttpURLConnection con = null;
		URL url = null;

		try {
			// URLの作成
			url = new URL(params[0]);
			// 接続用HttpURLConnectionオブジェクト作成
			con = (HttpURLConnection) url.openConnection();
			// リクエストメソッドの設定
			con.setRequestMethod("GET");
			// リダイレクトを自動で許可しない設定
			con.setInstanceFollowRedirects(false);
			// URL接続からデータを読み取る場合はtrue
			con.setDoInput(true);
			// URL接続にデータを書き込む場合はtrue
			con.setDoOutput(true);
			// 接続
			con.connect();

			//データを受信
			InputStream in = con.getInputStream();
			StringBuffer sb = new StringBuffer();
			String st = null;
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			while ((st = br.readLine()) != null) {
				sb.append(st);
			}
			String readSt = sb.toString();
			in.close();

			jsonObject = (JSONObject) new JSONTokener(readSt).nextValue();
			JSONArray routes = jsonObject.getJSONArray("routes");
			JSONObject route  = routes.getJSONObject(0);
			JSONArray legs = route.getJSONArray("legs");
			JSONObject leg = legs.getJSONObject(0);
			JSONArray steps = leg.getJSONArray("steps");
			JSONObject step;
			double lat = 0.f;
			double lon = 0.f;
			ArrayList<double[]> rt = new ArrayList<>();
			for(int i=0; i<steps.length(); i++){
				step = steps.getJSONObject(i);
				int distance = step.getJSONObject("distance").getInt("value");
				JSONObject start_location = step.getJSONObject("start_location");
				double _lat = start_location.getDouble("lat");
				double _lon = start_location.getDouble("lng");
				if(lat != _lat || lon != _lon){
					rt.add(new double[]{_lat, _lon});
				}
				JSONObject end_location = step.getJSONObject("end_location");
				lat = end_location.getDouble("lat");
				lon = end_location.getDouble("lng");

				int moveCount = distance / 10;
				double lat_diff = (lat - _lat) / moveCount;
				double lon_diff = (lon - _lon) / moveCount;
				for(int j=0; j<moveCount; j++){
					_lat += lat_diff;
					_lon += lon_diff;
					rt.add(new double[]{_lat, _lon});
				}
			}
			rootArray = new double[rt.size()][2];
			for(int i=0; i<rt.size(); i++){
				rootArray[i] = rt.get(i);
			}
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}
		//処理停止の解放
		MainActivity.mDone.countDown();
		return null;
	}
}
