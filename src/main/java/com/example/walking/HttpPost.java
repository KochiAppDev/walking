package com.example.walking;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

//HttpPost用のクラス
public final class HttpPost extends AsyncTask<String, Void, Void> {

	public JSONObject jsonObject;
	public JSONArray jsonArray;

	@Override
	protected Void doInBackground(String... params) {
		HttpURLConnection con = null;
		URL url = null;

		try {
			// URLの作成
			url = new URL(params[0]);
			// 接続用HttpURLConnectionオブジェクト作成
			con = (HttpURLConnection)url.openConnection();
			// リクエストメソッドの設定
			con.setRequestMethod("POST");
			// リダイレクトを自動で許可しない設定
			con.setInstanceFollowRedirects(false);
			// URL接続からデータを読み取る場合はtrue
			con.setDoInput(true);
			// URL接続にデータを書き込む場合はtrue
			con.setDoOutput(true);
			// 接続
			con.connect();

			// データを送信
			OutputStream out = con.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
			writer.write(params[1]);
			writer.flush();
			writer.close();
			out.close();

			//データを受信
			InputStream in = con.getInputStream();
			StringBuffer sb = new StringBuffer();
			String st = null;
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			while((st = br.readLine()) != null){
				sb.append(st);
			}
			String readSt = sb.toString();
			in.close();

			Object json = new JSONTokener(readSt).nextValue();
			//取ってきたものが配列かの確認
			if (json instanceof JSONObject) {
				jsonObject = (JSONObject) json;
			}else if (json instanceof JSONArray){
				jsonArray = (JSONArray) json;
			}

		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}
		//処理停止の解放
		MainActivity.mDone.countDown();
		return null;
	}
}
