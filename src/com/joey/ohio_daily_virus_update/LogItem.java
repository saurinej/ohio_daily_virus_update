package com.joey.ohio_daily_virus_update;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

public class LogItem {
	
	private GregorianCalendar date;
	private String body;
	private Session session;
	private MimeMessage message;
	private boolean sendSuccessful;
	
	public LogItem(GregorianCalendar date, String body) {
		setDate(date);
		setBody(body);
	}
	
	@Override
	public String toString() {
		DateFormat format = new SimpleDateFormat("ddMMMyyyy");
		return format.format(this.date.getTime()) + " - send successful: " + sendSuccessful;
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
	
	
	
	
}
