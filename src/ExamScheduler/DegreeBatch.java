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
public class DegreeBatch {

    void Initialize( String name )
    {
        Name = name;
        
        StudentList        = new Vector();
        CourseInfoList     = new Vector();
        
        AssignedClassroom  = "";
        Strength           = 0;
        UnassignedStrength = 0;
        nIsEveningSession  = -1;
        
        fSkipAssignmentOfClassroom = false;
    }
    
    void AddStudent( String student, Vector coursesEnrolled, ExamScheduler.CourseCollection Courses  )
    {
        StudentList.addElement(student);
        Strength++;
        UnassignedStrength++;
        
        
        for( int i=0; i<coursesEnrolled.size(); i++ )
        {
            String       course = coursesEnrolled.elementAt(i).toString();
            CourseInfo   courseInfo = new CourseInfo();
            CourseInfo   tempCourseInfo;
            int          nClassCredits = 0;
            double       nLabCredits = 0;
            int j, k;
            
            for( j=0; j<CourseInfoList.size(); j++ )
            {
                tempCourseInfo = (CourseInfo) CourseInfoList.elementAt(j);
                if( course.equals( tempCourseInfo.ID ) == true )
                    break;
            }
            
            if( j == CourseInfoList.size() )
            {
                for( k=0; k<Courses.GetCount(); k++ )
                    if( Courses.ElementAt(k).ID.equals(course) == true )
                        break;
                
                if( k == Courses.GetCount() )
                {
                    throw new RuntimeException("Error: Course name not matched in Class Section");
                }
                    
                nClassCredits = Courses.ElementAt(k).ClassCreditHours;
                nLabCredits   = Courses.ElementAt(k).LabCreditHours;
                courseInfo.Initialization( Courses.ElementAt(k).ID, Courses.ElementAt(k).Name, nClassCredits, nLabCredits, Courses.ElementAt(k).RelevantLabs );
                
                CourseInfoList.add(j, courseInfo);
            }
            
            courseInfo = (CourseInfo) CourseInfoList.remove(j);
            courseInfo.nStudentsEnrolled = courseInfo.nStudentsEnrolled + 1;
            
            CourseInfoList.add(j, courseInfo);
        }
        
    }
    
    int GetClassCreditHoursTakenByPercentOfStudentsInSection( int nPercentage )
    {
        int nCreditHours = 0;
        
        for( int i=0; i<CourseInfoList.size(); i++ )
        {
            CourseInfo   courseInfo = (CourseInfo) CourseInfoList.elementAt(i);
            if( (( courseInfo.nStudentsEnrolled * 100 )/Strength ) >= nPercentage )
                nCreditHours = nCreditHours + courseInfo.nClassCreditHours;
        }
        
        return nCreditHours;
    }
    
    int GetLabContactHoursTakenByPercentOfStudentsInSection( int nPercentage )
    {
        int nContactHours = 0;
        
        for( int i=0; i<CourseInfoList.size(); i++ )
        {
            CourseInfo   courseInfo = (CourseInfo) CourseInfoList.elementAt(i);
            if( (( courseInfo.nStudentsEnrolled * 100 )/Strength ) >= nPercentage )
                nContactHours = nContactHours + courseInfo.nLabContactHours;
        }
        
        return nContactHours;
    }

    void SetAssignedClassroom( String classroom )
    {
        AssignedClassroom = classroom;
    }

    String GetAssignedClassroom()
    {
        return AssignedClassroom;
    }

    int GetStrength()
    {
        return Strength;
    }
    
    int DecrementUnassignedStrength( int nQuantityToDecreaseBy )
    {
        UnassignedStrength -= nQuantityToDecreaseBy;
        
        return UnassignedStrength;
    }

    int GetUnassignedStrength()
    {
        return UnassignedStrength;
    }
    
    String    Name;
    String    AssignedClassroom;
    Vector    StudentList;
    Vector    CourseInfoList;
    int       Strength;
    int       UnassignedStrength;
    int       nIsEveningSession;
    boolean   fSkipAssignmentOfClassroom;
}
