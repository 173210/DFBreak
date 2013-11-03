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
import java.io.InputStream;
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
	Spinner offset_spinner;
	EditText offset_editText;
	CheckBox verfying_checkBox;
	Button exec_button;
	Button reboot_button;
	TextView out_textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		int i;
		String[] offset_spinner_entries = getResources().getStringArray(R.array.offset_spinner_entries);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		offset_spinner = (Spinner)findViewById(R.id.offset_spinner);
		offset_editText = (EditText)findViewById(R.id.offset_editText);
		verfying_checkBox = (CheckBox)findViewById(R.id.verifying_checkBox); 
		exec_button = (Button)findViewById(R.id.exec_button);
		reboot_button = (Button)findViewById(R.id.reboot_button);
		out_textView = (TextView)findViewById(R.id.out_textView);
		
		out_textView.setMovementMethod(ScrollingMovementMethod.getInstance());

		offset_spinner.setOnItemSelectedListener(this);
		exec_button.setOnClickListener(this);
		reboot_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            	pm.reboot(null);
            }
        });
		
		for (i = 0; i < offset_spinner_entries.length - 1; i++) {
			if (Build.DISPLAY.equals(offset_spinner_entries[i])) {
				out_textView.append(getString(R.string.out_offset_found));
				offset_spinner.setSelection(i);
				return;
			}
		}
		out_textView.append(getString(R.string.out_offset_notfound));
		offset_spinner.setSelection(i);
	}

	@Override
	public void onClick(View v) {
		int buffer;
		long offset;

		FileReader orig_su;
		InputStream insert_su;
		RandomAccessFile dev;
		
		exec_button.setEnabled(false);
		try {
			offset = Long.decode(offset_editText.getText().toString());
			out_textView.append(getString(R.string.out_dev_opening));
			dev = new RandomAccessFile("/dev/block/mmcblk0", "rw");
			if (verfying_checkBox.isChecked()) {
				out_textView.append(getString(R.string.out_orig_su_opening));
				orig_su = new FileReader("/system/xbin/su");
				out_textView.append(getString(R.string.out_seeking));
				dev.seek(offset);
				out_textView.append(getString(R.string.out_verifying));
				while ((buffer = orig_su.read()) != -1) {
					if (buffer != dev.read()) {
						out_textView.append(Html.fromHtml(
							"<font color=\"red\">" +
								getString(R.string.out_invalid) + "</font><br>"));
						dev.close();
						orig_su.close();
						exec_button.setEnabled(true);
						return;
					}
				}
				out_textView.append(getString(R.string.out_orig_su_closing));
				orig_su.close();
			}
			out_textView.append(getString(R.string.out_seeking));
			dev.seek(offset);
			out_textView.append(getString(R.string.out_insert_su_opening));
			insert_su = getResources().getAssets().open("su");
			out_textView.append(getString(R.string.out_writing));
			while ((buffer = insert_su.read()) != -1) dev.write(buffer);
			out_textView.append(getString(R.string.out_dev_closing));
			dev.close();
			out_textView.append(getString(R.string.out_insert_su_closing));
			insert_su.close();
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
		String offset_spinner_selected =
				(String)offset_spinner.getSelectedItem();
		
		if (offset_spinner_selected.equals(
				getString(R.string.offset_spinner_entry_201301111156))) {
			offset_editText.setText("0xD9CB8870");
			offset_editText.setFocusable(false);
		}
		else if (offset_spinner_selected.equals(
				getString(R.string.offset_spinner_entry_201307221456))) {
			offset_editText.setText("0xD9CC7BD0");
			offset_editText.setFocusable(false);
		}
		else offset_editText.setFocusable(true);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}
}
