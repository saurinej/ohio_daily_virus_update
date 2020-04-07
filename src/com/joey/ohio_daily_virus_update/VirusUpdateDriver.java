package com.joey.ohio_daily_virus_update;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
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
	
	private static TreeMap<GregorianCalendar, TreeMap<String, County>> dataByDay = new TreeMap<>(new CustomGregorianCalendarComparator());
	private static ArrayList<SingleDayCount> previousVersionData = new ArrayList<>();
	
	public static void main(String[] args) {
		
		
		TreeMap<GregorianCalendar, TreeMap<String, County>> dataByDaySerialized = null;
		ArrayList<SingleDayCount>previousDataSerialized = null;
		
		//creates input stream for County_Data_Over_Time.dat file
		try{
			//read in new version data
			ObjectInputStream in = new ObjectInputStream(new FileInputStream("County_Data_Over_Time.dat"));
			dataByDaySerialized = (TreeMap<GregorianCalendar, TreeMap<String, County>>)(in.readObject());
			previousDataSerialized = (ArrayList<SingleDayCount>)(in.readObject());
			in.close();
		} catch (FileNotFoundException e) {
			System.err.println("Could not open the file \"County_Data_Over_Time.dat\"");
		} catch (IOException e) {
			System.err.println("Could not de-serialize the object");
		} catch (ClassNotFoundException e) {
			System.err.println("Could not cast the de-serialized object");
		}
		
		if(dataByDaySerialized != null) {
			dataByDay = dataByDaySerialized;
		}
		
		if(previousDataSerialized != null) {
			previousVersionData = previousDataSerialized;
		}
		
		//task to run once a day at 2:05 pm after website updates
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		Runnable task = new Runnable() {
			@Override
			public void run() {
				//create date and get case number for this specific day
				DateFormat dateFormat = new SimpleDateFormat("ddMMMyyyy");
				GregorianCalendar date = new GregorianCalendar();
				
				TreeMap<String, County> currentData = getDataFromCSV();
				dataByDay.put(date, currentData);
				
				
				String subject = "Ohio Virus Update: " + dateFormat.format(date.getTime());
				
				//build body of email with data
				StringBuilder body = new StringBuilder();
				int previousDayCount = 0;
				for (SingleDayCount d: previousVersionData) {
					body.append(dateFormat.format(d.getDate().getTime()) + ":\n");
					int currentDayCount = d.getCaseCount();
					if (previousDayCount == 0) {
						body.append("\tTotal Count: " + currentDayCount + "\n");
					} else {
						int newCases = currentDayCount - previousDayCount;
						body.append("\tTotal Count: " + currentDayCount + " (" + newCases + ")" + "\n");
					}
					previousDayCount = currentDayCount;
				}
				for (Map.Entry<GregorianCalendar, TreeMap<String, County>> entry: dataByDay.entrySet()) {
					body.append(dateFormat.format(entry.getKey().getTime()) + ":\n");
					
					//get specific county count
					int countyCount = entry.getValue().get("Montgomery").getCount();
					body.append("\tMontgomery Count: " + countyCount + "\n");
					
					//get total case count for a single reporting day
					int totalCount = 0;
					for (Map.Entry<String, County> subentry: entry.getValue().entrySet()) {
						totalCount += subentry.getValue().getCount();
					}
					if (previousDayCount == 0) {
						body.append("\tTotal Count: " + totalCount + "\n");
					} else {
						int newCases = totalCount - previousDayCount;
						body.append("\tTotal Count: " + totalCount + " (" + newCases + ")" + "\n");
					}
					
					previousDayCount = totalCount;
				}
				//initialize email parameters and send email
				String emailFrom = "XXXXXXXX@gmail.com";
				String password = "YYYYYYYYYY";
				String emailTo = "ZZZZZZZZZ@gmail.com";
				//sendEmail(subject, body.toString(), emailFrom, password, emailTo);
				System.out.println(body.toString());
				System.out.println("Email sent successfully on " + dateFormat.format(date.getTime()) + ". Enter \"1\" to terminate.");
			}
		};
		
		//sets delay and starts task to send email every day at 2:05pm according to local time
		long delay = ChronoUnit.SECONDS.between(LocalTime.now(), LocalTime.of(14, 05, 00));
		scheduler.scheduleAtFixedRate(task, delay, 86400, TimeUnit.SECONDS);
		//user may terminate program by entering 1
		Scanner in = new Scanner(System.in);
		if (in.nextInt() == 1) {
			scheduler.shutdownNow();
			in.close();
			//writes new data to file
			try {
				ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("County_Data_Over_Time.dat"));
				out.writeObject(dataByDay);
				out.writeObject(previousVersionData);
				out.close();
			} catch (FileNotFoundException e) {
				System.err.println("Could not create the file \"County_Data_Over_Time.dat\"");
			} catch (IOException e) {
				System.err.println("Could not serialize the object");
			}
			System.out.println("Goodbye!");
			System.exit(0);
		}
	}
	
	//returns collection of data stored by county
	private static TreeMap<String, County> getDataFromCSV() {
		
		TreeMap<String, County> currentDayData = new TreeMap<>();
		
		try {
			//open URL connection and create BufferedReader to read the input stream for the CSV file
			URL urlCSV = new URL("https://coronavirus.ohio.gov/static/COVIDSummaryData.csv");
			URLConnection connection = urlCSV.openConnection();
			BufferedReader inputCSV = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			/*
			 * CSV file formatted as follows:
			 * first row: column titles
			 * data: county, sex, age range, onset date, death date, case count, death count, hospitalized count
			 * last row: totals
			 * so we must ignore the first and last lines during data collection from the CSV file
			 */
			
			//first line read first so the title row is not stored
			String line = inputCSV.readLine();
			while((line = inputCSV.readLine()) != null) {
				String[] s = line.split(",");
				String countyName = s[0];
				//following if statement skips the last row for the totals
				if (countyName.contains("Grand Total")) {
					break;
				}
				String sex = s[1];
				String ageRange = s[2];
				String onsetDate = s[3];
				String deathDate = s[4];
				int count = Integer.parseInt(s[5]);
				int deathCount = Integer.parseInt(s[6]);
				int hospitalizedCount = Integer.parseInt(s[7]);
				CaseInstance newCase = new CaseInstance(sex, ageRange, onsetDate, deathDate, count, deathCount, hospitalizedCount);
				if (currentDayData.containsKey(countyName)) {
					currentDayData.get(countyName).addCaseInstance(newCase);
				} else {
					County newCounty = new County(countyName);
					newCounty.addCaseInstance(newCase);
					currentDayData.put(countyName, newCounty);
				}
			}
			
			inputCSV.close();
			
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return currentDayData;
		
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
	
	@Deprecated
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
	
	@Deprecated
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