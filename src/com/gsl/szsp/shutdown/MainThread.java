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

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;

import android.text.Html;

public class MainThread implements Runnable {
	boolean istest;
	public MainThread(boolean istest) {
		this.istest = istest;
	}

	public void run() {
		final long offset;
		byte[] buffer;
		byte[] rbuf = new byte[16777216];

		FileInputStream orig_su;
		InputStream replace_su;
		final RandomAccessFile dev;
		
		MainActivity.mHandler.post(new Runnable() {
			public void run() {
				MainActivity.exec_button.setEnabled(false);
			}
		});

		try {
			MainActivity.mHandler.sendEmptyMessage(R.string.out_orig_su_opening);
			orig_su = new FileInputStream("/system/xbin/su");
			MainActivity.mHandler.sendEmptyMessage(R.string.out_orig_su_allocating);
			buffer = new byte[orig_su.available()];
			MainActivity.mHandler.sendEmptyMessage(R.string.out_orig_su_loading);
			orig_su.read(buffer);
			MainActivity.mHandler.sendEmptyMessage(R.string.out_orig_su_closing);
			orig_su.close();

			MainActivity.mHandler.sendEmptyMessage(R.string.out_dev_opening);
			dev = new RandomAccessFile("/dev/block/mmcblk0", "rw");

			MainActivity.mHandler.sendEmptyMessage(R.string.out_su_searching);
			int i = 0;
			int j = rbuf.length;
			while (i < buffer.length) {
				if (j < rbuf.length - 1)
					j++;
				else {
					dev.read(rbuf);
					j = 0;
				}
				if (rbuf[j] == buffer[i])
					i++;
				else if (rbuf[j] == buffer[0])
					i = 1;
				else
					i = 0;
			}
			
			offset = dev.getFilePointer() - rbuf.length + j - buffer.length + 1;
			
			MainActivity.mHandler.sendEmptyMessage(R.string.out_su_found);
			MainActivity.mHandler.post(new Runnable() {
				public void run() {
					MainActivity.out_textView.append(String.valueOf(offset) + "\n");
				}
			});

			if (!istest) {
				MainActivity.mHandler.sendEmptyMessage(R.string.out_dev_seeking);
				dev.seek(offset);

				MainActivity.mHandler.sendEmptyMessage(R.string.out_replace_su_opening);
				replace_su = MainActivity.res.getAssets().open("su");
				MainActivity.mHandler.sendEmptyMessage(R.string.out_replace_su_allocating);
				buffer = new byte[replace_su.available()];
				MainActivity.mHandler.sendEmptyMessage(R.string.out_replace_su_loading);
				replace_su.read(buffer);
				MainActivity.mHandler.sendEmptyMessage(R.string.out_replace_su_closing);
				replace_su.close();

				MainActivity.mHandler.sendEmptyMessage(R.string.out_writing);
				dev.write(buffer);
			}
			MainActivity.mHandler.sendEmptyMessage(R.string.out_dev_closing);
			dev.close();
			MainActivity.mHandler.sendEmptyMessage(R.string.out_complete);
		} catch (final Exception e) {
			MainActivity.mHandler.post(new Runnable() {
				public void run() {
					MainActivity.out_textView.append(Html.fromHtml(
							"<font color=\"red\">" + e.getLocalizedMessage() + "</font><br>"));
				}
			});
		}
		
		MainActivity.mHandler.post(new Runnable() {
			public void run() {
				MainActivity.exec_button.setEnabled(true);
			}
		});
	}
}
