package robot;

import java.io.FileNotFoundException;

import org.opencv.core.Core;

public class Main {

	public static void main(String[] args) {
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		Robot test;
		try {
			test = new Robot();
			test.init();
			test.start();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
