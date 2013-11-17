/*
 * Copyright (C) 2013 173210 <root.3.173210@live.com>

 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package com.gsl.szsp.shutdown;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class MainActivity extends Activity {
	public static Handler mHandler;
	public static Resources res;

	public static CheckBox test_checkBox;
	public static Button exec_button;
	public static Button reboot_button;
	public static TextView out_textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		res = getResources();
		
		mHandler = new Handler() {
			public void handleMessage(Message message) {
				out_textView.append(getString(message.what));
			}
		};

		test_checkBox = (CheckBox)findViewById(R.id.test_checkBox); 
		exec_button = (Button)findViewById(R.id.exec_button);
		reboot_button = (Button)findViewById(R.id.reboot_button);
		out_textView = (TextView)findViewById(R.id.out_textView);
		
		out_textView.setMovementMethod(ScrollingMovementMethod.getInstance());

		exec_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	(new Thread(new MainThread(MainActivity.test_checkBox.isChecked()))).start();
            }
        });
		reboot_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            	pm.reboot(null);
            }
        });
	}
}
