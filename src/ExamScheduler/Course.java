/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ExamScheduler;

import java.util.Vector;

/**
 *
 * @author christendsouza
 */
public class Course {

    void Initialize( String id, String name, int classCreditHours, double labCreditHours, String relevantLabs )
    {
        ID               = id;
        Name             = name;
        ClassCreditHours = classCreditHours;
        LabCreditHours   = labCreditHours;
        RelevantLabs     = new Vector();
        StudentAttendees = new Vector();
        
        TotalCreditHours = ClassCreditHours + LabCreditHours;
        
        int iIndex;
                
        while( relevantLabs.length() != 0 )
        {
            iIndex = relevantLabs.indexOf(',');
            
            if( iIndex != -1 )
                RelevantLabs.addElement( relevantLabs.substring(0, iIndex ));
            else 
            {
                RelevantLabs.addElement( relevantLabs );
                break;
            }
            
            relevantLabs = relevantLabs.substring(iIndex+1);
            relevantLabs = relevantLabs.trim();
        }
    }

    void AddStudent( String studentName )
    {
        StudentAttendees.addElement( studentName );
    }
    
    String  ID;
    String  Name;
    Vector  RelevantLabs;
    double  LabCreditHours;
    int     ClassCreditHours;
    double  TotalCreditHours;
    Vector  StudentAttendees;
}
