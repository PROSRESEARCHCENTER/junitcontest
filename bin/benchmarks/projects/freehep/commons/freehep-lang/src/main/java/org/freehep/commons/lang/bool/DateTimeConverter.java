
package org.freehep.commons.lang.bool;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author bvan
 */
public class DateTimeConverter {
    
    static Pattern timePattern = Pattern.compile("([0-9:\\.]*)");
    
    public static Timestamp getTime(String dateString){
        try{
            Calendar cal = DatatypeConverter.parseDateTime( dateString );
            return new Timestamp(cal.getTimeInMillis() );
        } catch (IllegalArgumentException ex){ }
        
        try{
            Calendar cal = DatatypeConverter.parseDate( dateString );
            return new Timestamp(cal.getTimeInMillis() );
        } catch (IllegalArgumentException ex){ }
        
        if(dateString.length() < 10){
            String dateZeroPadding = "1970-01-01";
            String date = dateString + dateZeroPadding.substring( dateString.length() );
            return new Timestamp( DatatypeConverter.parseDate( date ).getTimeInMillis() );
        }
        
        if(dateString.indexOf( 'T' ) != 10){
            if(dateString.indexOf( "Z" ) == 10){
                return new Timestamp( DatatypeConverter.parseDate( dateString ).getTimeInMillis() );
            }
            if(dateString.indexOf( "-" ) == 10 || dateString.indexOf( "+" ) == 10){
                String timeZone = dateString.substring( 11 );
                return new Timestamp( DatatypeConverter.parseDate( dateString ).getTimeInMillis() );
            }            
            
        }

        
        if(dateString.indexOf( 'T' ) == 10){
            StringBuilder dateTime = new StringBuilder();

            String date = dateString.substring( 0, 10 );
            String timeAndTimezone = dateString.substring( 11 );
            Matcher timeMatcher = timePattern.matcher( timeAndTimezone );
            if(!timeMatcher.find()){
                throw new RuntimeException("unable to parse time");
            }
            String time = timeMatcher.group();
            String timeZone = timeAndTimezone.substring( time.length() );
            
            dateTime.append( date ).append( 'T' );
            String timeExtend = "00:00:00";
            dateTime.append( time.length() < 8 ? time + timeExtend.substring( time.length()) : time );
            dateString = dateTime.append( timeZone ).toString();
            return new Timestamp( DatatypeConverter.parseDate( dateString ).getTimeInMillis() );
        }
        
        throw new RuntimeException("Unable to parse date");
    }
    
    public static void main(String[] argc){
        DateTimeConverter dtc = new DateTimeConverter();
        System.out.println( dtc.getTime( "2012-01").toString() );
        System.out.println( dtc.getTime( "2012-01-02").toString() );
        System.out.println( dtc.getTime( "2012-01-02Z").toString() );
        System.out.println( dtc.getTime( "2012-01-02T12").toString() );
        System.out.println( dtc.getTime( "2012-01-02T12:04").toString() );
        System.out.println( dtc.getTime( "2012-01-02T12:04:20").toString() );
        System.out.println( dtc.getTime( "2012-01-02T12:04:20.001").toString() );
        System.out.println( dtc.getTime( "2012-01-02T12:04-08:00").toString() );
        System.out.println( dtc.getTime( "2012-01-02T12:04:20-08:00").toString() );
        System.out.println( dtc.getTime( "2012-01-02T12:04:20.001-08:00").toString() );
        System.out.println( dtc.getTime( "2012-01-02T12:04Z").toString() );
        System.out.println( dtc.getTime( "2012-01-02T12:04:20Z").toString() );
        System.out.println( dtc.getTime( "2012-01-02T12:04:20.001Z").toString() );
        System.out.println( dtc.getTime( "2012-01-02T12:04+08:00").toString() );
        System.out.println( dtc.getTime( "2012-01-02T12:04:20+08:00").toString() );
        System.out.println( dtc.getTime( "2012-01-02T12:04:20.001+08:00").toString() );
    }
}
