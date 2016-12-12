/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ExamScheduler;

import java.util.Vector;

/*
 *
 * @author christendsouza
 */
public class Student {

    void Initialize( String studentID, String name, String courseList, String degree, String batch, String section )
    {
        int iIndex;
        
        ID      = studentID.trim();
        Name    = name.trim();
        Degree  = degree.trim();
        Batch   = batch.trim();
        Section = section.trim();
        DegreeBatch = degree + "-" + batch;
        NumberOfCoursesEnrolled = 0;
        CoursesEnrolled = new Vector();
        
        int previousValue, nIndex=0;
        
        /*do{
            previousValue = nIndex;
            nIndex = ID.indexOf('-', nIndex+1 );    
            if( previousValue == 0
             && nIndex > 0 )
            {
                Year = Integer.valueOf(ID.substring(0, nIndex).trim());
            }
        }while( nIndex != -1 );*/
        Year = Integer.valueOf(Batch);
        String strRegNo = ID.substring(4);
        strRegNo = strRegNo.trim();
        
        RegNo = Integer.valueOf(strRegNo);
        
        /*while( courseList.length() != 0 )
        {
            iIndex = courseList.indexOf(',');
            
            NumberOfCoursesEnrolled++;
            
            if( iIndex != -1 )
                CoursesEnrolled.addElement( courseList.substring(0, iIndex ));
            else 
            {
                CoursesEnrolled.addElement( courseList );
                break;
            }
            
            courseList = courseList.substring(iIndex+1);
            courseList = courseList.trim();
        }*/
        
    }
    
    String ID;
    String Name;
    String Degree;
    String Batch;
    String DegreeBatch;
    String Section;
    Vector CoursesEnrolled;
    int    Year;
    int    RegNo;
    int    NumberOfCoursesEnrolled;
    
}
