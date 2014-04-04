/**
 *  @file ITunesScanner.java
 *  @author Gary Read
 *  @date 24 Feb 2013
 *  
 *  @brief This program was intended to quickly move music files that belonged 
 *   to a playlist, it saves huge amounts of time, whereas you'd need to
 *   locate each song in your library what you wanted to move and copy
 *   it over, one by one.
 *  Program takes in command line file name, where the file is a iTunes
 *   playlist text file, a source directory of the music, and the destination
 *   directory for the music files
 *  This program will create a batch file from this and attempt to move these
 *   music files from the source to the destination.
 */

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.ArrayList;

public class ITunesScanner {
	public static void main(String[] args) {
		String fileName = null;
		String fileEncoding = null;
		String sourceDir = null;
		String destinationDir = null;
		
		File playlist = null;
		Scanner in = null;
		
		ArrayList<String> trackNames = null;
		
		try {
			fileName = args[0];
			fileEncoding = "UTF-8";
			sourceDir = args[1];
			destinationDir = args[2];
			
			playlist = new File(fileName);
			in = new Scanner(playlist, fileEncoding);
			trackNames = new ArrayList<String>();
		} catch (Exception e) {
			System.out.println(e);
		}
		
		int found = 0;
		int ignored = 0;
		String validEntries = "";
		String ignoredEntries = "";
		
		in.useDelimiter(".*:");
		while (in.hasNext()) {
			String line = in.next().trim();
			if (line.contains("\\")) {
				line = line.substring(line.lastIndexOf("\\") + 1);
			}
			if ((line.toLowerCase()).matches("(?s).*(mp3|m4a).*")) {
				validEntries += "echo " + found + ") \"" + line + "\" >> log.txt \n";
				trackNames.add(line);
				found++;
			} else {
				ignoredEntries += "echo " + ignored + ") \"" + line + "\" >> log.txt \n";
				ignored++;
			}
		}
		String preOutput = "echo The following entry(s) have been located in your playlist, an attempt to copy the tracks will commence... >>log.txt &echo. >> log.txt \n" + 
							validEntries +
							"echo. >> log.txt &echo The following entry(s) will be ignored: >> log.txt &echo. >> log.txt \n" +
							ignoredEntries +
							"echo. >> log.txt &echo " + found + " valid music tracks found, " + ignored + " entry(s) will be ignored. >> log.txt &echo. >> log.txt \n" +
							"echo - - - - - - - - - - - - - - - - - - - - - - - >> log.txt &echo. >> log.txt \n" +
							"echo Attempting to create batch files... >> log.txt &echo. >> log.txt";
		
		File copytracks = null;
		PrintWriter out = null;
		try {
			try {
				copytracks = new File("copytracks.bat");
				out = new PrintWriter(copytracks);
				
				out.println("@echo off");
				out.println("echo @echo off > delete_me.bat");
				out.println("echo TIMEOUT /T 2 /NOBREAK >> delete_me.bat");
				out.println("echo del /F \"copytracks.bat\" >> delete_me.bat");
				out.println("echo exit >> delete_me.bat");
				out.println("set /a success = 0");
				out.println("set /a fail = 0");
				out.println("echo. > log.txt");
				out.println("echo Gary Read - Swansea University - 2013 >> log.txt  &echo. >> log.txt");
				out.println("echo - - - - - - - - - - - - - - - - - - - - - - - >> log.txt &echo. >> log.txt");
				out.println(preOutput);
				out.println("echo Attempting to copy " + found + " tracks from " + destinationDir + " to " + sourceDir + " >> log.txt &echo. >> log.txt");
				out.println("echo - - - - - - - - - - - - - - - - - - - - - - - >> log.txt &echo. >> log.txt");
				for (String track : trackNames) {
					out.println("if exist \"" + sourceDir + track + "\" (xcopy " + " \"" + sourceDir + track + "\" \"" + destinationDir + "\" && set /a success=success+1) else (echo \"" + track + "\" failed to copy. >> log.txt && set /a fail=fail+1)");
				}
				out.println("echo. >> log.txt &echo - - - - - - - - - - - - - - - - - - - - - - - >> log.txt &echo. >> log.txt");
				out.println("echo Successfully copied %success%; Failed %fail%. >> log.txt");
				//out.println("start log.txt &start cmd /c delete_me.bat &exit");
				
			} catch (Exception e) {
				print(e);
			}
		} finally {
			try {
				out.close();
			} catch (Exception e) {
				print(e);
			}
		}
		
		try {
			Runtime.getRuntime().exec("cmd /c start copytracks.bat");
		} catch (Exception e) {
			print(e);
		}

		print("Program complete - Check log.txt for diagnostics.");
	}
	
	private static void print(Object msg) {
		try {
			System.out.println(msg);
		} catch (Exception e) {
			print(e);
		}
	}
}