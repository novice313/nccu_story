package com.mclab1.palaca.parsehelper;

import android.content.Context;

import com.parse.Parse;

public class ParseHelper {
	public static void initParse(Context context) {
		try {
			Parse.initialize(context,
					"wtSFcggR896xMJQUGblYuphkF6EVw4ChcLcpSowP",
					"IwJ3gTRBe8cARlxMf3xh97eai2a7MNLP68vdL3IY");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
