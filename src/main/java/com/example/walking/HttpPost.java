package com.example.walking;

import org.json.JSONException;
import org.json.JSONObject;

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


public class HttpPost {
	private JSONObject exec_post(String urlSt, JSONObject jsonObject) {
		HttpURLConnection con = null;
		URL url = null;

		try {
			// URLの作成
			url = new URL(urlSt);
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
			writer.write(String.valueOf(jsonObject));
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

			JSONObject json = new JSONObject(readSt);

			return json;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
