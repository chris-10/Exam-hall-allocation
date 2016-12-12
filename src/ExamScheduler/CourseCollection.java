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
public class CourseCollection {

    CourseCollection()
    {
        Courses = new Vector();
        
    }
    
    void AddCourse( String id, String name, int classCreditHours, double labCreditHours, String relevantLabs )
    {
        Course temp  = new Course();
        
        temp.Initialize( id, name, classCreditHours, labCreditHours, relevantLabs );
        
        Courses.add(temp);
    }   

    void Sort()
    {
        
        // sort Courses according to course name/number
        for( int m=0; m<Courses.size(); m++ )
            for( int n=m+1; n<Courses.size(); n++ )
            {
                Course temp;

                if( ElementAt(m).Name.compareTo( ElementAt(n).Name ) > 0 )
                {
                   temp = (Course)Courses.get(n);
                   Courses.setElementAt( Courses.get(m), n );
                   Courses.setElementAt( temp, m );
                }
            }
        //done sotring
    }

    int GetCreditHoursForCourseList( String courseList )
    {
        int nTotalCreditHours = 0;
        int iIndex = 0, i = 0;
        String course;
        
        while( courseList.length() != 0 )
        {
            iIndex = courseList.indexOf(',');
            
            if( iIndex != -1 )
            {
                course = courseList.substring(0, iIndex );
                courseList = courseList.substring(iIndex+1);
                courseList = courseList.trim();
            }
            else 
            {
                course = courseList;
                courseList = "";
            }
            
            course = course.trim();
            
            for( i=0; i<GetCount(); i++ )
                if( ElementAt(i).ID.equals(course) == true )
                {
                    // TODO: change this to 'TotalCreditHours' when labs start getting considered
                    nTotalCreditHours += ElementAt(i).ClassCreditHours;
                    break;
                }

            if( i == GetCount() )
            {
                throw new RuntimeException("Invalid Course Name:" + course );
            }
        }
        
        return nTotalCreditHours;
    }
    
    Course ElementAt( int i )
    {
        return (Course)Courses.elementAt(i);
    }
    
    int GetCount()
    {
        return Courses.size();
    }
/*    
    int GetClassRoomIndex( String classroom )
    {
        for( int i=0; i<GetCount(); i++ )
            if( true == ElementAt(i).ClassroomName.equals(classroom) )
                return i;
    
        throw new RuntimeException("Error:  Could not find " + classroom + " in the Courses list" );
    }
*/
    // data
    
    Vector  Courses;
    
}
