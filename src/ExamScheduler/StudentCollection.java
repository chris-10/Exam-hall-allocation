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
public class StudentCollection {


    StudentCollection()
    {
        Students = new Vector();
        
    }
    
    void AddStudent( String studentID, String name, String courseList, String degree, String batch, String section )
    {
        Student temp  = new Student();
        
        temp.Initialize( studentID, name, courseList, degree, batch, section );
        
        Students.add(temp);
    }   

    void SortByID()
    {    
        // sort Students according to Student name/number
        for( int m=0; m<GetCount(); m++ )
            for( int n=m+1; n<GetCount(); n++ )
            {
                Student temp;

                if( ElementAt(m).ID.compareToIgnoreCase( ElementAt(n).ID ) > 0 )
                {
                   temp = (Student)Students.get(n);
                   Students.setElementAt( Students.get(m), n );
                   Students.setElementAt( temp, m );
                }
            }
        //done sorting
    }
    
    void SortBySection()
    {    
        boolean fSwap = true;
        Student temp;

        // sort Students according to Student Section
        for( int m=GetCount()-1, n; m>0 && fSwap==true; m-- )
        {
            n=0;
            fSwap = false;
            
            do
            {
                if( ElementAt(n).Section.compareToIgnoreCase(ElementAt(n+1).Section) > 0 )
                {
                   temp = (Student)Students.get(n);
                   Students.setElementAt( Students.get(n+1), n );
                   Students.setElementAt( temp, n+1 );
                   fSwap = true;
                }
                n++;
                
            }while(n<m);
        }
        //done sorting        
    }

    void SortByRegNo()
    {
        boolean fSwap = true;
        Student temp;

        // sort Students according to Student RegNo
        for( int m=GetCount()-1, n; fSwap==true; m-- )
        {
            n=0;
            fSwap = false;

            do
            {
                if( ElementAt(n).RegNo > ElementAt(n+1).RegNo )
                {
                   temp = (Student)Students.get(n);
                   Students.setElementAt( Students.get(n+1), n );
                   Students.setElementAt( temp, n+1 );
                   fSwap = true;
                }
                n++;
            }while(n<m);
        }
        //done sorting        
    }
    
    void SortByYear()
    {
        boolean fSwap = true;
        Student temp;

        // sort Students according to Student Year
        for( int m=GetCount()-1, n; fSwap==true; m-- )
        {
            n=0;
            fSwap = false;
            
            do
            {
                if( ElementAt(n).Year > ElementAt(n+1).Year )
                {
                   temp = (Student)Students.get(n);
                   Students.setElementAt( Students.get(n+1), n );
                   Students.setElementAt( temp, n+1 );
                   fSwap = true;
                }
                n++;
            }while(n<m);
        }
        //done sorting
    }
    
    Student ElementAt( int i )
    {
        return (Student)Students.elementAt(i);
    }
    
    int GetCount()
    {
        return Students.size();
    }
/*    
    int GetStudentRoomIndex( String classroom )
    {
        for( int i=0; i<GetCount(); i++ )
            if( true == ElementAt(i).ClassroomName.equals(classroom) )
                return i;
    
        throw new RuntimeException("Error:  Could not find " + classroom + " in the Courses list" );
    }
*/
    // data
    
    Vector  Students;
        
    
}
