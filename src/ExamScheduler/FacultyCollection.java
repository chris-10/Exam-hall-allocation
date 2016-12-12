/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ExamScheduler;

import java.util.Date;
import java.util.Random;
import java.util.Vector;

/**
 *
 * @author christendsouza
 */
public class FacultyCollection {

    FacultyCollection()
    {
        FacultyMembers = new Vector();
        
    }
    
    void AddFaculty( String facultyID, String facultyName, String facultyDepartment, String facultyGender, int Classrooms, int SlotsPerWeek, String coursesInstructing, int CreditHoursTeaching, int maxConsecutiveClasses, int maxLecturesADay, int nNumberOfTimeSlots )
    {
        Faculty temp  = new Faculty();
        
        temp.Initialize( facultyID, facultyName, facultyDepartment, facultyGender, Classrooms, SlotsPerWeek, coursesInstructing, CreditHoursTeaching, maxConsecutiveClasses, maxLecturesADay, nNumberOfTimeSlots );
        
        FacultyMembers.add(temp);
    }   

    void Sort()
    {
        
        // sort FacultyMembers according to faculty name/number
        for( int m=0; m<FacultyMembers.size(); m++ )
            for( int n=m+1; n<FacultyMembers.size(); n++ )
            {
                Faculty temp;

                if( ElementAt(m).Name.compareTo( ElementAt(n).Name ) > 0 )
                {
                   temp = (Faculty)FacultyMembers.get(n);
                   FacultyMembers.setElementAt( FacultyMembers.get(m), n );
                   FacultyMembers.setElementAt( temp, m );
                }
            }
        //done sotring
    }

    void Randomize(long seed)
    {
        Random randomGenerator = new Random(seed);
        int randomInt, nFacultyMembers;
        Faculty temp;
        
        nFacultyMembers = FacultyMembers.size();
        
        // randomize FacultyMembers list
        for( int m=0; m<nFacultyMembers; m++ )
        {            
            randomInt = randomGenerator.nextInt(nFacultyMembers);
         
            // swap faculty at random
            temp = (Faculty)FacultyMembers.get(randomInt);
            FacultyMembers.setElementAt( FacultyMembers.get(m), randomInt );
            FacultyMembers.setElementAt( temp, m );
        }
        //done randomizing
    }

    void UpdateSlotInfoOfFaculty( int srcRow, int srcColumn, int destRow, int destColumn, String srcFaculty, ClassTime_Schedule[][] FullSchedule )
    {
        int iSrcFaculty = GetFacultyIndex( srcFaculty ); 
                            
        for( int l=0; l<ElementAt(iSrcFaculty).nSlotsFilled; l++) 
        {
            if( ElementAt(iSrcFaculty).facultySlotInfo[l].rowIndex    == srcRow
             && ElementAt(iSrcFaculty).facultySlotInfo[l].columnIndex == srcColumn )
            {
                ElementAt(iSrcFaculty).facultySlotInfo[l].rowIndex     = destRow;
                ElementAt(iSrcFaculty).facultySlotInfo[l].columnIndex  = destColumn;
                ElementAt(iSrcFaculty).facultySlotInfo[l].SlotPriority = FullSchedule[destRow][destColumn].GetAllocationPriority();
                break;
            }
        }
    }
    
    Faculty ElementAt( int i )
    {
        return (Faculty)FacultyMembers.elementAt(i);
    }
    
    int GetCount()
    {
        return FacultyMembers.size();
    }

    int GetFacultyIndex( String FacultyID )
    {
        for( int i=0; i<GetCount(); i++)
            if( ElementAt(i).ID.equals(FacultyID) == true )
                return i;
        
        return -1;
    }
    int GetFacultyIndexUsingName( String FacultyName )
    {
        for( int i=0; i<GetCount(); i++)
            if( ElementAt(i).Name.equals(FacultyName) == true )
                return i;
        
        return -1;
    }

    // data
    
    Vector  FacultyMembers;  
    
}
