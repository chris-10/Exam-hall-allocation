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
public class DegreeBatchCollection {

    DegreeBatchCollection()
    {
        Sections = new Vector();
        
    }
    
    void AddSection( String name )
    {
        DegreeBatch temp  = new DegreeBatch();
        
        temp.Initialize( name );
        
        Sections.add(temp);
    }   

    void Sort()
    {   
        // sort Sections according to DegreeBatch name/number
        for( int m=0; m<GetCount(); m++ )
            for( int n=m+1; n<GetCount(); n++ )
            {
                DegreeBatch temp;

                if( ElementAt(m).Name.compareTo( ElementAt(n).Name ) > 0 )
                {
                   temp = (DegreeBatch)Sections.get(n);
                   Sections.setElementAt( Sections.get(m), n );
                   Sections.setElementAt( temp, m );
                }
            }
        //done sotring
    }

    void SortBySize()
    {   
        // sort Sections according to DegreeBatch size
        for( int m=0; m<GetCount(); m++ )
            for( int n=m+1; n<GetCount(); n++ )
            {
                DegreeBatch temp;

                if( ElementAt(m).Strength < ElementAt(n).Strength )
                {
                   temp = (DegreeBatch)Sections.get(n);
                   Sections.setElementAt( Sections.get(m), n );
                   Sections.setElementAt( temp, m );
                }
            }
        //done sotring
    }

    int FindSectionForFacultysCourse( String course, String facultyID, boolean fReturnOnlyVacantSection )
    {
        int l=0, k=0;

        for( k=0; k<GetCount(); k++ )
        {
            for( l=0; l<ElementAt(k).CourseInfoList.size(); l++ )
            {
                CourseInfo tempCourseInfo = (CourseInfo)ElementAt(k).CourseInfoList.elementAt(l);

                if( true == tempCourseInfo.ID.equals(course) 
                 && ( true == tempCourseInfo.FacultyInstructing.isEmpty()
                   || ( true == tempCourseInfo.FacultyInstructing.equals( facultyID )
                     && tempCourseInfo.nClassSlotsFilled + tempCourseInfo.nLabSlotsFilled < tempCourseInfo.nTotalContactHours 
                     && fReturnOnlyVacantSection == false ) ) )
                {
                    ElementAt(k).CourseInfoList.removeElementAt(l);
                    tempCourseInfo.FacultyInstructing = facultyID;
                    ElementAt(k).CourseInfoList.add(l, tempCourseInfo);
                    break;
                }
            } 
            if( l != ElementAt(k).CourseInfoList.size() )
                break;                            
        }

        if( k == GetCount() )
        {
            throw new RuntimeException("Error:  Could not find the course subscription in the Section List");
        }        
        
        return k;
    }
    
    void UpdateSectionForThisCourseAndFaculty( int sectionIndex, String courseID, int classSlotsFilled, int labSlotsFilled, String facultyID )
    {
        int l;
        
        for( l=0; l<ElementAt(sectionIndex).CourseInfoList.size(); l++ )
        {
            CourseInfo tempCourseInfo = (CourseInfo)ElementAt(sectionIndex).CourseInfoList.elementAt(l);

            if( true == tempCourseInfo.ID.equals(courseID) 
             && ( true == tempCourseInfo.FacultyInstructing.isEmpty()
               || ( true == tempCourseInfo.FacultyInstructing.equals( facultyID )
                 && tempCourseInfo.nClassSlotsFilled + tempCourseInfo.nLabSlotsFilled < tempCourseInfo.nTotalContactHours ) ) )  //TODO: possible bug here
            {
                ElementAt(sectionIndex).CourseInfoList.removeElementAt(l);
                tempCourseInfo.FacultyInstructing = facultyID;
                tempCourseInfo.nClassSlotsFilled  = classSlotsFilled;
                tempCourseInfo.nLabSlotsFilled    = labSlotsFilled;
                ElementAt(sectionIndex).CourseInfoList.addElement(tempCourseInfo);
                break;
            }
        } 
        if( l == ElementAt(sectionIndex).CourseInfoList.size() )
        {
            throw new RuntimeException("Could not find the course subscription in the Section List");
        }        
    }

    void AssignRemainingSessionToTheRemainingSection( int sectionIndex, ClassRoom selectedClassroom )
    {                        
        if( ElementAt(sectionIndex).nIsEveningSession == -1 )
        {
            for( int r=0; r<selectedClassroom.AssignedDegreeBatches.size(); r++ )
                if( false == selectedClassroom.AssignedDegreeBatches.elementAt(r).toString().equals(ElementAt(sectionIndex).Name) )
                    for( int s=0; s<GetCount(); s++ )
                        if( true == ElementAt(s).Name.equals(selectedClassroom.AssignedDegreeBatches.elementAt(r).toString()) )
                        {
                            if( 0 == ElementAt(s).nIsEveningSession )
                            {
                                ElementAt(sectionIndex).nIsEveningSession = 1;
                                ExamSchedulerView.PrintNewLineToConsole( "Section " + ElementAt(sectionIndex).Name + " assigned evening session" );
                            }
                            else if( 1 == ElementAt(s).nIsEveningSession )
                            {
                                ElementAt(sectionIndex).nIsEveningSession = 0;
                                ExamSchedulerView.PrintNewLineToConsole( "Section " + ElementAt(sectionIndex).Name + " assigned morning session" );
                            }   
                            break;
                        } 
        }
    }
    
    DegreeBatch ElementAt( int i )
    {
        return (DegreeBatch)Sections.elementAt(i);
    }
    
    int GetCount()
    {
        return Sections.size();
    }
/*    
    int GetSectionRoomIndex( String classroom )
    {
        for( int i=0; i<GetCount(); i++ )
            if( true == ElementAt(i).ClassroomName.equals(classroom) )
                return i;
    
        throw new RuntimeException("Error:  Could not find " + classroom + " in the Courses list" );
    }
*/
    // data
    
    Vector  Sections;
        
}
