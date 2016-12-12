/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ExamScheduler;

/**
 *
 * @author christendsouza
 */
public class TimeSlot {

    TimeSlot()
    {
        StartTime = 0000;
        EndTime   = 0000;
    }
    
    void Initialize( String day, int startTime, int endTime )
    {
        DayOfWeek = day;
        StartTime = startTime;
        EndTime   = endTime;
        
        StartTimeRowOffset = 0;
        EndTimeRowOffset = 0;
    }
 
    void Initialize( String day, String slotInfo )
    {
        int nIndex;
        
        DayOfWeek = day;

        nIndex = slotInfo.indexOf('-');
        
        StartTime = Integer.valueOf(slotInfo.substring(0, nIndex).trim());
        EndTime   = Integer.valueOf(slotInfo.substring(nIndex+1).trim());
        
        StartTimeRowOffset = StartTime>1000?1:0;
        EndTimeRowOffset   = EndTime>1000?1:0;
        
    }
    
    String      DayOfWeek;
    Integer     StartTime;
    Integer     EndTime;
    Integer     StartTimeRowOffset;
    Integer     EndTimeRowOffset;
    
}
