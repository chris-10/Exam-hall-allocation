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
public class CourseInfo {

    CourseInfo()
    {
        ID                 = "";
        Name               = "";
        nClassCreditHours  = 0;
        nTotalContactHours = 0;
        nClassSlotsFilled  = 0;
        nStudentsEnrolled  = 0;
        FacultyInstructing = "";
        RelevantLabs       = new Vector();
        
    }
    void Initialization( String id, String name, int classHours, double labHours, Vector relevantLabs )
    {
        ID                 = id;
        Name               = name;
        nClassCreditHours  = classHours;
        nLabContactHours   = (int)(2*labHours);
        nTotalContactHours = nClassCreditHours + nLabContactHours;
        
        RelevantLabs = relevantLabs;
    }
            
    String  ID;
    String  Name;
    String  FacultyInstructing;
    Vector  RelevantLabs;
    int     nClassCreditHours;
    int     nLabContactHours;
    int     nTotalContactHours;
    int     nClassSlotsFilled;
    int     nLabSlotsFilled;
    int     nStudentsEnrolled;
}
