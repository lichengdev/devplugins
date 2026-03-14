package pers.bc.chat.menu.idea;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {
	public static void main(String[] args) {
		String format = new SimpleDateFormat("yyyyMMddHHmmssss").format(new Date());
		System.err.print(format);
	}
}
