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

import java.io.FileReader;
import java.io.RandomAccessFile;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class AlarmShutDownNotification extends Activity implements OnClickListener, OnItemSelectedListener {
	Spinner addr_spinner;
	EditText addr_editText;
	CheckBox verfying_checkBox;
	Button exec_button;
	Button reboot_button;
	TextView out_textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		int i;
		String[] addr_spinner_entries = getResources().getStringArray(R.array.addr_spinner_entries);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		addr_spinner = (Spinner)findViewById(R.id.addr_spinner);
		addr_editText = (EditText)findViewById(R.id.addr_editText);
		verfying_checkBox = (CheckBox)findViewById(R.id.verifying_checkBox); 
		exec_button = (Button)findViewById(R.id.exec_button);
		reboot_button = (Button)findViewById(R.id.reboot_button);
		out_textView = (TextView)findViewById(R.id.out_textView);
		
		out_textView.setMovementMethod(ScrollingMovementMethod.getInstance());

		addr_spinner.setOnItemSelectedListener(this);
		exec_button.setOnClickListener(this);
		reboot_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            	pm.reboot(null);
            }
        });
		
		for (i = 0; i < addr_spinner_entries.length - 1; i++) {
			if (Build.DISPLAY.equals(addr_spinner_entries[i])) {
				out_textView.append(getString(R.string.out_addr_found));
				addr_spinner.setSelection(i);
				return;
			}
		}
		out_textView.append(getString(R.string.out_addr_notfound));
		addr_spinner.setSelection(i);
	}

	@Override
	public void onClick(View v) {
		int buf;
		long addr;
		
		RandomAccessFile dev;
		FileReader su;
		
		exec_button.setEnabled(false);
		try {
			addr = Long.decode(addr_editText.getText().toString());
			out_textView.append(getString(R.string.out_dev_opening));
			dev = new RandomAccessFile("/dev/block/mmcblk0", "rw");
				if (verfying_checkBox.isChecked()) {
				out_textView.append(getString(R.string.out_su_opening));
				su = new FileReader("/system/xbin/su");
				out_textView.append(getString(R.string.out_seeking));
				dev.seek(addr);
				out_textView.append(getString(R.string.out_verifying));
				while ((buf = su.read()) != -1) {
					if (buf != dev.read()) {
						out_textView.append(Html.fromHtml(
							"<font color=\"red\">" +
								getString(R.string.out_invalid) + "</font><br>"));
						dev.close();
						su.close();
						exec_button.setEnabled(true);
						return;
					}
				}
				out_textView.append(getString(R.string.out_su_closing));
				su.close();
			}
			out_textView.append(getString(R.string.out_seeking));
			dev.seek(addr);
			out_textView.append(getString(R.string.out_writing));
			dev.write(Binaries.su);
			out_textView.append(getString(R.string.out_dev_closing));
			dev.close();
			out_textView.append(getString(R.string.out_complete));
		} catch (Exception e) {
			out_textView.append(Html.fromHtml(
					"<font color=\"red\">" + e.getLocalizedMessage() + "</font><br>"));
			exec_button.setEnabled(true);
		}
		
		exec_button.setEnabled(true);
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String addr_spinner_selected =
				(String)addr_spinner.getSelectedItem();
		
		if (addr_spinner_selected.equals(
				getString(R.string.addr_spinner_entry_201301111156))) {
			addr_editText.setText("0xD9CB8870");
			addr_editText.setFocusable(false);
		}
		else if (addr_spinner_selected.equals(
				getString(R.string.addr_spinner_entry_201307221456))) {
			addr_editText.setText("0xD9CC7BD0");
			addr_editText.setFocusable(false);
		}
		else addr_editText.setFocusable(true);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}
}
