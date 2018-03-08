/*
 * RmiRemoteUtils.java
 *
 * Created on October 26, 2003, 11:12 PM
 */

package hep.aida.ref.remote.rmi;

import java.text.DateFormat;
import java.util.Date;
import java.util.StringTokenizer;
/**
 *
 * @author  serbo
 */
public class RmiRemoteUtils {
    
    public static int port = 1099;
    
    public static String getCurrentDateString() {
        Date date = new Date(System.currentTimeMillis());
        return getCurrentDateString(date);
    }
    public static String getCurrentDateString(long timeMillis) {
        Date date = new Date(timeMillis);
        return getCurrentDateString(date);
    }
    public static String getCurrentDateString(Date date) {
        String tmpString = DateFormat.getDateTimeInstance().format(date);
        
        StringTokenizer st = new StringTokenizer(tmpString, ",\\ ");
        String dateString = "";
        while (st.hasMoreTokens()) {
            dateString += st.nextToken();
           if (st.hasMoreTokens()) { dateString += "_"; }
        }
        int index = dateString.indexOf(",");
        if (index > 0 && index < dateString.length()-1) {
           dateString = dateString.substring(0,index-1) + dateString.substring(index+1);
        }
        return dateString;
    }

    public static void main(String[] args) {
        
        System.out.println(getCurrentDateString());
    }
}
