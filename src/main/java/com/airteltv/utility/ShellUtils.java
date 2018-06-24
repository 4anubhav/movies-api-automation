package com.airteltv.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.airteltv.reports.Log;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.StreamGobbler;

public class ShellUtils {

	private static String SHELL_AUTH_ANALYTICS = "disha@10.1.2.249";

	private static BufferedReader getOutput(Process p) {
		return new BufferedReader(new InputStreamReader(p.getInputStream()));
	}

	private static BufferedReader getError(Process p) {
		return new BufferedReader(new InputStreamReader(p.getErrorStream()));
	}

	public static String getShellResponseLocal(String command) throws IOException {
		ProcessBuilder pb = new ProcessBuilder("/usr/bin/ssh", "-i", "/home/disha/.ssh/id_rsa", command);
		Process p = pb.start();
		BufferedReader output = getOutput(p);
		BufferedReader error = getError(p);
		StringBuilder ligne = new StringBuilder("");
		String temp;
		while ((temp = output.readLine()) != null) {
			ligne.append(temp);
		}
		output.close();
		if (ligne.length() > 0)
			return ligne.toString();

		while ((temp = error.readLine()) != null) {
			ligne.append(temp);
		}
		error.close();
		if (ligne.length() > 0)
			return ligne.toString();

		return null;
	}

	// Authentication keys placed at 10.1.2.142 the server where this code will be
	// deployed..
	// /home/pankajk/.ssh/id_rsa
	private static String getShellResponse(String command, String auth) throws IOException {
		ProcessBuilder pb = new ProcessBuilder("/usr/bin/ssh", "-i", "/home/disha/.ssh/id_rsa", auth, command);
		Log.info("Command - " + command);
		Process p = pb.start();
		BufferedReader output = getOutput(p);
		BufferedReader error = getError(p);
		StringBuilder ligne = new StringBuilder("");
		String temp;
		while ((temp = output.readLine()) != null) {
			ligne.append(temp + "\n");
		}
		output.close();
		if (ligne.length() > 0)
			return ligne.toString();

		while ((temp = error.readLine()) != null) {
			ligne.append(temp);
		}
		error.close();
		if (ligne.length() > 0)
			return ligne.toString();
		return null;
	}

	public static void executeShellFromLocal(String command) throws IOException, InterruptedException {

		System.out.println("command to execute " + command);
		Runtime run = Runtime.getRuntime();
		Process pr = run.exec(command);
		pr.waitFor();
		BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
		String line = "";
		while ((line = buf.readLine()) != null) {
			System.out.println(line);
		}
		System.out.println("Command executed successfully");

	}
	public static void makeConnection()
	{
		
	}
	public static String executeCommand(String server, String command) throws IOException {
		String username = "disha", password = "disha";
		Connection connection = new Connection(server);
		connection.connect();
		boolean isAuthenticated = connection.authenticateWithPassword(username, password);
		Log.info("connection status " + isAuthenticated);
		ch.ethz.ssh2.Session session = connection.openSession();
		InputStream stdout = new StreamGobbler(session.getStdout());
		BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdout));
		Log.info("connected");

		// Run command
		String tempCommand = command;

		Log.info("sending command: " + tempCommand);
		session.execCommand(tempCommand);// + " && sleep 5");

		// Get output
		StringBuffer sb = new StringBuffer();
		while (true) {
			String line = stdoutReader.readLine();
			if (line == null)
				break;
			sb.append(line + "\n");
		}
		String output = sb.toString();

		Log.info("got output: " + output);
		return output;

	}

	/*public static String getAnalyticalEvent(String msisdnOrUid, String event, int count) throws IOException {
		List<AnalyticalEvent> response = new ArrayList<AnalyticalEvent>();

		Date date = new Date(System.currentTimeMillis() - (60 * 60 * 1000));
		SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd-HH");
		System.out.println(dt1.format(date));
		String lastFile = "/mnt/share/analytics/analytics.log." + dt1.format(date) + ".6700 "
				+ "/mnt/share/analytics/analytics.log." + dt1.format(date) + ".6701";
		String currentFile = "/mnt/share/analytics/analytics.log.6700 /mnt/share/analytics/analytics.log.6701";

		Calendar rightNow = Calendar.getInstance();
		int minute = rightNow.get(Calendar.MINUTE);
		String q = Utils.getUID(msisdnOrUid).replaceAll("^[-]+", "");
		String command = "zgrep " + q + " " + currentFile + " " + (minute > 5 ? "" : lastFile) + " | grep -i " + event
				+ " | tail -" + count;
		Log.info("running following command\n" + command);

		String eventString = ShellUtils.getShellResponse(command, SHELL_AUTH_ANALYTICS);
		if (StringUtils.isNotBlank(eventString)) {
			String[] Logs = eventString.split("\n");
			for (String eventLoopItem : Logs) {
				AnalyticalEvent e = new AnalyticalEvent(eventLoopItem);
				if (StringUtils.isNotBlank(e.getEventName()))
					response.add(e);
			}
		} else {
			eventString = "No Logs found";
		}
		if (response.size() > 0)
			return Utils.toJsonString(response);
		else
			return eventString;
	}*/

	public static String getLogs(String ip, String file, String q, int count, int extra) throws IOException {
		String eventString = null;
		q = q.replace("^[-]+", "");
		String command = "zgrep -B" + extra + " -A" + extra + " " + q + " " + file + " | tail -" + count;
		Log.info("Running following command\n" + command);
		eventString = ShellUtils.getShellResponse(command, "pankajk@" + ip);
		if (eventString != null && eventString.length() > 0)
			return eventString;
		else
			return null;
	}

	public static String getLogs(String server, String uid, String timestamp, String eventType) throws IOException {
		Log.info("Getting Logs From Server " + server + "for uid :" + uid);
		if (server.equalsIgnoreCase("analytics")) {
			String command = "cat /mnt/share/analytics/analytics.log.670* | grep '" + uid + "'| grep '" + eventType
					+ "'" + " | grep '" + timestamp + "'";
			String command1 = "grep -E '" + uid
					+ "' /mnt/share/analytics/analytics.log.670* /mnt/share/analytics/analytics.log."
					+ Utils.getTodaysDate() + "-*" + " | grep  '" + eventType + "' |" + "grep '" + timestamp + "'";

			Log.info("analytics command executed " + command1);
			String serverIp = "10.1.2.249";
			Log.info("Command to be executed :" + command + "On server :" + serverIp);
			String logs = executeCommand(serverIp, command1);
			Log.info("Analytical Logs : " + logs);
			return logs;
		} else if (server.equalsIgnoreCase("portal")) {
			String command = "";
			if (timestamp.equalsIgnoreCase("")) {
				command = "cat /data/logs/portallog.log|grep '/music/v2/stats, message: ' | grep '" + uid + "'"
						+ "| grep '" + eventType + "'";

			} else {

				command = "cat /data/logs/portallog.log|grep '/music/v2/stats, message: ' | grep -E '" + uid
						+ "'| grep '" + timestamp + "'";
			}
			String serverIp = "10.1.2.248";
			Log.info("Command to be executed :" + command + "On server :" + serverIp);
			String logs = executeCommand(serverIp, command);
			Log.info("Portal Logs : " + logs);
			return logs;
		}
		return null;
	}

	/*public static void main(String[] args) throws IOException {
		Utils.printPrettyJson(getAnalyticalEvent("-IyqxNLPzpAUwRIZF0", "CLICK", 4));

	}*/
}