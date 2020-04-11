package com.joey.ohio_daily_virus_update;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

public class LogItem {
	
	private GregorianCalendar date;
	private String body;
	private Session session;
	private MimeMessage message;
	private boolean sendSuccessful;
	private boolean scheduled;
	
	public LogItem(GregorianCalendar date, String body) {
		setDate(date);
		setBody(body);
		setScheduled(true);
	}
	
	public LogItem(GregorianCalendar date) {
		setDate(date);
		setScheduled(false);
	}
	
	@Override
	public String toString() {
		DateFormat format = new SimpleDateFormat("ddMMMyyyy HH:mm:ss");
		if (scheduled) {
			try {
				if (sendSuccessful) {
					return format.format(this.date.getTime()) + " - Message sent? " + sendSuccessful + ", Scheduled update? " + scheduled + ", Message Name: " + message.getFileName();
				} else {
					return format.format(this.date.getTime()) + " - Message sent?: " + sendSuccessful + ", Scheduled update? " + scheduled;
				}
				
			} catch (MessagingException e) {
				return format.format(this.date.getTime()) + " - Message sent?: " + sendSuccessful + ", Scheduled update? " + scheduled;
			}
		} else {
			return format.format(this.date.getTime()) + " - Manual update? " + !scheduled;
		}
		
	}

	public GregorianCalendar getDate() {
		return date;
	}

	public void setDate(GregorianCalendar date) {
		this.date = date;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public MimeMessage getMessage() {
		return message;
	}

	public void setMessage(MimeMessage message) {
		this.message = message;
	}

	public boolean isSendSuccessful() {
		return sendSuccessful;
	}

	public void setSendSuccessful(boolean sendSuccessful) {
		this.sendSuccessful = sendSuccessful;
	}

	public boolean isScheduled() {
		return scheduled;
	}

	public void setScheduled(boolean scheduled) {
		this.scheduled = scheduled;
	}
	
	
}
