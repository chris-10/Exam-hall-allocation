/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ExamScheduler;

import java.util.Vector;

//class ClassTime_Schedule;

/**
 *
 * @author christendsouza
 */

public class Faculty {
    
    void Faculty()
    {
        ID   = "";
        Name = "";

        nClassrooms                = 0;
        nSlotsPerWeek              = 0;
        nCreditHoursTeaching       = 0;
        NumberOfCoursesInstructing = 0;
        
        TotalTimeSlots          = 1;
        TotalTimeSlotsAvailable = 1;
        
        nSlotsFilled            = 0;
        
        ConstraintPriority      = TotalTimeSlots/TotalTimeSlotsAvailable;
        TypeMaxConstraintFactor = 1;
        FacultyPriority         = 1;
        
        OverallPriority    = nCreditHoursTeaching * TypeMaxConstraintFactor * ConstraintPriority * FacultyPriority;
    }
    
    int Initialize( String facultyID, String facultyName, String facultyDepartment, String facultyGender, int Classrooms, int SlotsPerWeek, String coursesInstructing, int CreditHoursTeaching, int maxConsecutiveClasses, int maxLecturesADay, int nNumberOfTimeSlots )
    {
        CourseInfo  courseInfo = new CourseInfo();
        int iIndex;
       
        ID         = facultyID;
        Name       = facultyName;
        Department = facultyDepartment;
        Gender     = facultyGender;
        
        nClassrooms          = Classrooms;
        nSlotsPerWeek        = SlotsPerWeek;        
        nCreditHoursTeaching = CreditHoursTeaching;
        
        nMaxConsecutiveClasses = maxConsecutiveClasses;
        nMaxLecturesADay       = maxLecturesADay;

        facultySlotInfo        = new SlotInfo[nNumberOfTimeSlots];
        CoursesInstructing     = new Vector();
        CoursesInstructingInfo = new Vector();
        
        TotalTimeSlots          = nClassrooms*nSlotsPerWeek;
        TotalTimeSlotsAvailable = nClassrooms*nSlotsPerWeek;

        ConstraintPriority = TotalTimeSlots/TotalTimeSlotsAvailable;

        while( coursesInstructing.length() != 0 )
        {
            iIndex = coursesInstructing.indexOf(',');
            
            NumberOfCoursesInstructing++;
            
            if( iIndex != -1 )
            {
                courseInfo.ID = coursesInstructing.substring(0, iIndex );
                
                CoursesInstructing.addElement( coursesInstructing.substring(0, iIndex ) );
            }
            else 
            {
                CoursesInstructing.addElement( coursesInstructing );
                break;
            }
            
            coursesInstructing = coursesInstructing.substring(iIndex+1);
            coursesInstructing = coursesInstructing.trim();
        }
        
        return 0;
    }
    
    void GetCourseInfoOfCoursesInstruction( ExamScheduler.CourseCollection Courses )
    {
        for( int i=0; i<CoursesInstructing.size(); i++ )
        {
            String       course = CoursesInstructing.elementAt(i).toString();
            int          nClassCredits = 0;
            double       nLabCredits = 0;
            CourseInfo   courseInfo = new CourseInfo();
            int k;
            
            for( k=0; k<Courses.GetCount(); k++ )
                if( Courses.ElementAt(k).ID.equals(course) == true )
                    break;

            if( k == Courses.GetCount() )
            {
                throw new RuntimeException("Error: Course ID not matched in Class Section");
            }

            nClassCredits = Courses.ElementAt(k).ClassCreditHours;
            nLabCredits   = Courses.ElementAt(k).LabCreditHours;
            courseInfo.Initialization( course, Courses.ElementAt(k).Name, nClassCredits, nLabCredits, Courses.ElementAt(k).RelevantLabs );
            courseInfo.nStudentsEnrolled = -1;

            CoursesInstructingInfo.add(courseInfo);
        }
    }
    
    float ConstrainSlots( int nSlots)
    {
        TotalTimeSlotsAvailable = TotalTimeSlotsAvailable - nSlots;
        
        return SetConstraintPriority( TotalTimeSlots/TotalTimeSlotsAvailable );
    }
    //setters
    int SetFacultyPriority(int Priority, int defaultMaxConsecutiveClasses, int defaultMaxLecturesADay )
    {
        
        TypeMaxConstraintFactor = defaultMaxLecturesADay/nMaxLecturesADay;
                
        if( nMaxConsecutiveClasses < nMaxLecturesADay )
            TypeMaxConstraintFactor = TypeMaxConstraintFactor *  defaultMaxConsecutiveClasses/nMaxConsecutiveClasses;
        
        FacultyPriority = Priority;
        OverallPriority = nCreditHoursTeaching * TypeMaxConstraintFactor * ConstraintPriority * FacultyPriority;
        
        return FacultyPriority;
    }
    float SetConstraintPriority( float Priority)
    {
        ConstraintPriority = Priority;
        OverallPriority = nCreditHoursTeaching * TypeMaxConstraintFactor * ConstraintPriority * FacultyPriority;        
        
        return ConstraintPriority;
    }

    //getters
    int GetFacultyPriority()
    {
        return FacultyPriority;
    }
    float GetConstraintPriority()
    {
        return ConstraintPriority;
    }
    float GetOverallPriority()
    {
        return OverallPriority;
    }
    String GetGender()
    {
        return Gender;
    }
    String GetFacultyID()
    {
        return ID;
    }
    String GetFacultyName()
    {
        return Name;
    }
    String GetFacultyDepartment()
    {
        return Department;
    }


    String     ID;
    String     Name;
    String     Gender;
    String     Department;
    Vector     CoursesInstructing;
    Vector     CoursesInstructingInfo;
    float      ConstraintPriority         = 1;
    float      TypeMaxConstraintFactor    = 1;
    int        FacultyPriority            = 1;
    float      OverallPriority            = 1;
    int        nClassrooms                = 0;
    int        nSlotsPerWeek              = 0;
    int        nCreditHoursTeaching       = 0;
    float      TotalTimeSlots             = 1;
    float      TotalTimeSlotsAvailable    = 1;
    int        NumberOfCoursesInstructing = 0;
    int        nSlotsFilled               = 0;
    int        nMaxConsecutiveClasses     = 0;
    int        nMaxLecturesADay           = 0;
    SlotInfo[] facultySlotInfo;
    
}
