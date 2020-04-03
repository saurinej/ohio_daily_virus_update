package com.joey.ohio_daily_virus_update;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class VirusUpdateDriver {
public static void main(String[] args) {
		
		TreeMap<Date, Integer> time = new TreeMap<>();
		
		
		//read in data from "tracker_data.txt" file
		try {
			BufferedReader input = new BufferedReader(new FileReader("tracker_data.txt"));
			String dateValuePair = input.readLine();
			while (dateValuePair != null) {
				String[] array = dateValuePair.split("~");
				DateFormat formatter = new SimpleDateFormat("ddMMMyyyy");;
				Date parsedDate = formatter.parse(array[0].trim());
				int cases = Integer.parseInt(array[1].trim());
				time.put(parsedDate, cases);
				dateValuePair = input.readLine();
			}
			System.out.println("Successfully parsed data.");
			input.close();
		} catch (FileNotFoundException e) {
			System.err.println("\"tracker_data.txt\" file not found. Will not send data for previous days before this date.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		//task to run once a day at 2:05 pm after website updates
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		Runnable task = new Runnable() {
			@Override
			public void run() {
				//create date and get case number for this specific day
				DateFormat dateFormat = new SimpleDateFormat("ddMMMyyyy HH:mm:ss");
				Date date = new Date();
				int cases = getCases();
				time.put(date, cases);
				String subject = "Ohio Virus Update: " + dateFormat.format(date);
				
				//build body of email with data
				StringBuilder body = new StringBuilder();
				body.append("Date | Confirmed Cases in Ohio");
				for (Map.Entry<Date, Integer> entry: time.entrySet()) {
					body.append("\n" + dateFormat.format(entry.getKey()) + " | " + entry.getValue());
				}
				//initialize email parameters and send email
				String emailFrom = "XXXXXXXX@gmail.com";
				String password = "YYYYYYYYYY";
				String emailTo = "ZZZZZZZZZ@gmail.com";
				sendEmail(subject, body.toString(), emailFrom, password, emailTo);
				
				System.out.println("Email sent successfully on " + dateFormat.format(date) + ". Enter \"1\" to terminate.");
			}
		};
		
		//sets delay and starts task to send email every day at 2:05pm according to local time
		long delay = ChronoUnit.SECONDS.between(LocalTime.now(), LocalTime.of(15, 28, 00));
		scheduler.scheduleAtFixedRate(task, delay, 86400, TimeUnit.SECONDS);
		//user may terminate program by entering 1
		Scanner in = new Scanner(System.in);
		if (in.nextInt() == 1) {
			List<Runnable> list = scheduler.shutdownNow();
			System.out.println(list.toString());
			in.close();
			//writes new data to file
			exit(time);
			System.exit(0);
		}
	}
	
	//returns daily case data as integer from coronavirus.ohio.gov
	private static int getCases() {
		URL url = null;
		try {
			url = new URL("https://coronavirus.ohio.gov/wps/portal/gov/covid-19/");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		//Get the input stream through URL Connection
        URLConnection connection;
        InputStream inputURL = null;
		try {
			connection = url.openConnection();
			inputURL = connection.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
		BufferedReader br = new BufferedReader(new InputStreamReader(inputURL));
		
		//store number and relative title data as displayed in the HTML file 
		Queue<String> numbers = new LinkedList<>();
		Queue<String> titles = new LinkedList<>();
		
			
		try {
			String line;
			while ((line = br.readLine()) != null) {
				//"<div class=\"odh-ads__item-title\">" line comes before data in the HTML document
				if (line.contains("<div class=\"odh-ads__item-title\">")) { 
					numbers.offer(br.readLine().trim());
					continue;
				}
				//"<div class=\"odh-ads__item-summary\">" line comes before title relative to stored data in the HTML document
				if (line.contains("<div class=\"odh-ads__item-summary\">")) {
					titles.offer(br.readLine().trim());
					continue;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		int cases = -1;
		int size = numbers.size();
		for (int i = 0; i < size; i++) {
			String title = titles.remove();
			String number = numbers.remove();
			number = number.replace(",", "");
			//title and number lists will contain other data but only confirmed case number is returned in this implementation
			if (title.contains("Confirmed Cases")) { 
				cases = Integer.parseInt(number);
				return cases;
			}
		}
		return cases;
	}
	
	//sends email according to entered parameters
	private static void sendEmail(String subject, String body, String emailFrom, String password, String emailTo) {
		//set properties 
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		props.put("mail.debug.auth", true);
		props.put("mail.debug", true);
		
		//create session
		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(emailFrom, password);
			}
		});
		
		//create message
		Message message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(emailFrom));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(emailTo));
			message.setSubject(subject);
			message.setText(body);
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
		//message transport
		try {
			Transport.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
	}
	
	//writes data to "tracker_data.txt" file according to format "ddMMMyyy~#"
	private static void exit(TreeMap<Date, Integer> map) {
		
		try {
			DateFormat dateFormat = new SimpleDateFormat("ddMMMyyyy HH:mm:ss");
			BufferedWriter output = new BufferedWriter(new FileWriter("tracker_data.txt", false));
			StringBuilder dataOut = new StringBuilder();
			for (Map.Entry<Date, Integer> entry: map.entrySet()) {
				dataOut.append(dateFormat.format(entry.getKey()) + "~" + entry.getValue() + "\n");
			}
			output.write(dataOut.toString());
			System.out.println("Successfully wrote data to \"tracker_data.txt\"");
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
