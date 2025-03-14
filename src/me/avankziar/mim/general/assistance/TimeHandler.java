package me.avankziar.mim.general.assistance;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class TimeHandler
{
	private final static long ss = 1000L;
	private final static long mm = 1000L*60;
	private final static long HH = 1000L*60*60;
	private final static long dd = 1000L*60*60*24;
	private final static long MM = 1000L*60*60*24*30;
	private final static long yyyy = 1000L*60*60*24*365;
	
	public static String getDateTime(long l)
	{
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(l), ZoneId.systemDefault())
				.format(DateTimeFormatter.ofPattern("dd.MM.yyyy-HH:mm:ss"));
	}
	
	public static long getDateTime(String l)
	{
		return LocalDateTime.parse(l, DateTimeFormatter.ofPattern("dd.MM.yyyy-HH:mm:ss"))
				.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}
	
	public static String getDate(long l)
	{
		Date date = new Date(l);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); 
		sdf.setTimeZone(TimeZone.getDefault()); 
		return sdf.format(date);
	}
	
	public static long getDate(String l)
	{
		Instant instant = Instant.now();
		ZoneId systemZone = ZoneId.systemDefault();
		ZoneOffset currentOffsetForMyZone = systemZone.getRules().getOffset(instant);
		
		return LocalDate.parse(l, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
				.atTime(LocalTime.MIDNIGHT).toEpochSecond(currentOffsetForMyZone)*1000;
	}
	
	public static String getTime(long l)
	{
		return new Time(l).toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
	}
	
	public static String getRepeatingTime(long l, String timeformat) // yyyy-dd-HH:mm
	{
		long ll = l;
		String year = "";
		long y = 0;
		while(ll >= yyyy)
		{
			ll = ll - yyyy;
			y++;
		}
		year = String.valueOf(y);
		String month = "";
		long M = 0;
		while(ll >= MM)
		{
			ll = ll - MM;
			M++;
		}
		month = String.valueOf(M);
		String day = "";
		long d = 0;
		while(ll >= dd)
		{
			ll = ll - dd;
			d++;
		}
		day = String.valueOf(d);
		long H = 0;
		String hour = "";
		while(ll >= HH)
		{
			ll = ll - HH;
			H++;
		}
		if(H < 10)
		{
			hour += String.valueOf(0);
		}
		hour += String.valueOf(H);
		long m = 0;
		String min = "";
		while(ll >= mm)
		{
			ll = ll - mm;
			m++;
		}
		if(m < 10)
		{
			min += String.valueOf(0);
		}
		min += String.valueOf(m);
		long s = 0;
		String sec = "";
		while(ll >= ss)
		{
			ll = ll - ss;
			s++;
		}
		if(s < 10)
		{
			sec += String.valueOf(0);
		}
		sec += String.valueOf(s);
		String time = timeformat.replace("yyyy", year)
								.replace("MM", month)
								.replace("dd", day)
								.replace("HH", hour)
								.replace("mm", min)
								.replace("ss", sec);
		return time;
	}
	
	public static long getRepeatingTime(String l) //yyyy-MM-dd-HH:mm
	{
		String[] a = l.split("-");
		if(!MatchApi.isInteger(a[0]))
		{
			return 0;
		}
		int y = Integer.parseInt(a[0]);
		int M = Integer.parseInt(a[1]);
		int d = Integer.parseInt(a[2]);
		String[] b = a[3].split(":");
		if(!MatchApi.isInteger(b[0]))
		{
			return 0;
		}
		if(!MatchApi.isInteger(b[1]))
		{
			return 0;
		}
		int H = Integer.parseInt(b[0]);
		int m = Integer.parseInt(b[1]);
		long time = y*yyyy+M*MM+d*dd + H*HH + m*mm;
		return time;
	}
}