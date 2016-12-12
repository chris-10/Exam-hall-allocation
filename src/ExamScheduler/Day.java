/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ExamScheduler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author christendsouza
 */
public class Day {

    Day()
    {
        Name = "";
        NumberOfSlots = 0;
    }
    
    void Initialize( String name )
    {
         Name = name;

/*
        // current time.
        Date now = new Date();
        Date before = new Date();
        
//        df
        //before.

        // Print the result of toString()
        String dateString = now.toString();
        System.out.println(" 1. " + dateString);

        SimpleDateFormat format =
            new SimpleDateFormat("EEEEEE MMM dd HH:mm:ss zzz yyyy");

        try {
            Date parsed = format.parse(dateString);
            System.out.println(" 2. " + parsed.toString());
        }
        catch(ParseException pe) {
            System.out.println("ERROR: Cannot parse \"" + dateString + "\"");
        }
            
  */          
        if( Name.equals("Monday") )
            DaysOffset = 0;
        else if( Name.equals("Tuesday") )
            DaysOffset = 1;
        else if( Name.equals("Wednesday") )
            DaysOffset = 2;
        else if( Name.equals("Thursday") )
            DaysOffset = 3;
        else if( Name.equals("Friday") )
            DaysOffset = 4;
        else  if( Name.equals("Saturday") )
            DaysOffset = 5;
        else  if( Name.equals("Sunday") )
            DaysOffset = 6;
        else                    //TODO: fix this to generic solution
        {
            if( Name.equals("20/01/2016") )
            {
                DaysOffset = 0;
                Name = "Monday";
            }
            else if( Name.equals("21/01/2016") )
            {
                DaysOffset = 1;
                Name = "Tuesday";
            }
            else if( Name.equals("22/01/2016") )
            {
                DaysOffset = 2;
                Name = "Wednesday";
            }
            else if( Name.equals("23/01/2016") )
            {
                DaysOffset = 3;
                Name = "Thursday";
            }
            else if( Name.equals("24/01/2016") )
            {
                DaysOffset = 4;
                Name = "Friday";
            }
            else
                 DaysOffset = -1;
        }
    }

    String  Name;
    int     DaysOffset;
    int     NumberOfSlots;

}
