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
public class ClassRoomCollection {

    ClassRoomCollection()
    {
        Classrooms = new Vector();
        
    }
    
    void AddClassroom( String classroomName, int nCapacity, boolean isLab, int nRows, int nColumns )
    {
        ClassRoom temp  = new ClassRoom();
        
        temp.Initialize( classroomName, nCapacity, isLab, nRows, nColumns );
        
        Classrooms.add(temp);
    }   

    void Sort()
    {
        
        // sort classrooms according to classroom name/number
        for( int m=0; m<GetCount(); m++ )
            for( int n=m+1; n<GetCount(); n++ )
            {
                boolean   fSwap = false;
                
                if( ElementAt(m).IsLab == false
                 && ElementAt(n).IsLab == false )
                {
                    String    class1  = ((ClassRoom)Classrooms.get(m)).ClassroomName.replace( "Classroom ", "" ),
                              class2  = ((ClassRoom)Classrooms.get(n)).ClassroomName.replace( "Classroom ", "" );
                    Integer   nClass1 = Integer.valueOf(class1);
                    Integer   nClass2 = Integer.valueOf(class2);
                    
                    if( nClass1 > nClass2 )
                        fSwap = true;
                }
                else
                {
                    if( ElementAt(m).IsLab == true
                     && ElementAt(n).IsLab == false )
                        fSwap = true;
                    else if( ElementAt(m).IsLab == true
                          && ElementAt(n).IsLab == true )
                        if( ElementAt(m).ClassroomName.compareTo( ElementAt(n).ClassroomName ) > 0 )
                            fSwap = true;
                }
                
                ClassRoom temp;

                if( fSwap == true )
                {
                   temp = (ClassRoom)Classrooms.get(n);
                   Classrooms.setElementAt( Classrooms.get(m), n );
                   Classrooms.setElementAt( temp, m );
                }
            }
        //done sotring
    }

    void SortBySize()
    {
        
        // sort classrooms according to classroom size
        for( int m=0; m<GetCount(); m++ )
            for( int n=m+1; n<GetCount(); n++ )
            {
                boolean   fSwap = false;
                
                if( ElementAt(m).IsLab == false
                 && ElementAt(n).IsLab == false )
                {
                    if( ((ClassRoom)Classrooms.get(m)).Capacity < ((ClassRoom)Classrooms.get(n)).Capacity )
                        fSwap = true;
                }
                else
                {
                    if( ElementAt(m).IsLab == true )
                        fSwap = true;
                }
                
                ClassRoom temp;

                if( fSwap == true )
                {
                   temp = (ClassRoom)Classrooms.get(n);
                   Classrooms.setElementAt( Classrooms.get(m), n );
                   Classrooms.setElementAt( temp, m );
                }
            }
        //done sorting
    }

    ClassRoom ElementAt( int i )
    {
        return (ClassRoom)Classrooms.elementAt(i);
    }
    
    int GetCount()
    {
        return Classrooms.size();
    }
    
    int GetClassRoomIndex( String classroom )
    {
        for( int i=0; i<GetCount(); i++ )
            if( true == ElementAt(i).ClassroomName.equals(classroom) )
                return i;
    
        throw new RuntimeException("Error:  Could not find " + classroom + " in the ClassRooms list" );
    }

    int AssignDegreeBatchToClassrooms( DegreeBatch DegreeBatch, String ClassroomName )
    {
        for( int j=0; j<GetCount(); j++ )
            if( ElementAt(j).ClassroomName.equals( ClassroomName) )
            {
                return ElementAt(j).AssignDegreeBatch(DegreeBatch);
            }
        
        return 0;
    }
    
    int AssignSomeSectionsToClassroms( DegreeBatchCollection Sections, int nMajorityPercentageInSection, int nNumberOfTimeSlots )
    {
        int nOverallMaxClassroomCapacity = -2, nOverallMaxSectionStrength = -1;     // keep strength bigger than capacity by default
        int iOverallMaxClassroomCapacity = 0,  iOverallMaxSectionStrength = 0;
        int nAssignments = 0;
        
        for( int i=0; i<GetCount() && i<Sections.GetCount(); i++ )
        {
            int nMaxClassroomCapacity = -2, nMaxSectionStrength = -1;               // keep strength bigger than capacity by default
            int iMaxClassroomCapacity = 0,  iMaxSectionStrength = 0;
            int nSlotsOccupiedByClassroom = 0;
            int j;
            
            // find an unassigned class with the largest capacity
            for( j=0; j<GetCount(); j++ )
                if( ElementAt(j).fSkipAssignmentOfClassroom == false
                 && ElementAt(j).IsLab == false
                 && ElementAt(j).Capacity > nMaxClassroomCapacity )
                {
                    Vector  assignedSections = ElementAt(j).GetAssignedDegreeBatches();
                    int     nSlotsRequiredBySection = 0;
                    String  section;
                    int     l;
                    
                    nSlotsOccupiedByClassroom = 0;
                    
                    for( int k=0; k<assignedSections.size(); k++ )
                    {
                        section = assignedSections.elementAt(k).toString();
                        
                        for( l=0; l<Sections.GetCount(); l++ )
                            if( Sections.ElementAt(l).Name.equals(section) == true )
                                break;
                        
                        if( l == Sections.GetCount() )
                        {
                            throw new RuntimeException("Error: Section " + section + " not found in Section list");
                        }

                        nSlotsRequiredBySection = Sections.ElementAt(l).GetClassCreditHoursTakenByPercentOfStudentsInSection( nMajorityPercentageInSection );
                        nSlotsOccupiedByClassroom = nSlotsOccupiedByClassroom + nSlotsRequiredBySection;
                    }
                    
                    if( nNumberOfTimeSlots < nSlotsOccupiedByClassroom )
                    {
                        throw new RuntimeException("Error: Previously assigned more sections to " + ElementAt(j).ClassroomName + " than the timeslot capacity");
                    }
                        
                    nMaxClassroomCapacity = ElementAt(j).Capacity;
                    iMaxClassroomCapacity = j;
                }
                    
            if( nMaxClassroomCapacity == -2 )
            {
                // no more classrooms to assign
                ExamSchedulerView.PrintNewLineToConsole( "No classroom are free for Section assignment" );
                break;
            }         

            // find an unassigned section with the largest strength
            for( j=0; j<Sections.GetCount(); j++ )
                if( Sections.ElementAt(j).fSkipAssignmentOfClassroom == false
                 && Sections.ElementAt(j).AssignedClassroom.isEmpty() == true
                 && Sections.ElementAt(j).Strength > nMaxSectionStrength )
                {
                    int nSlotsRequiredForSection = Sections.ElementAt(j).GetClassCreditHoursTakenByPercentOfStudentsInSection( nMajorityPercentageInSection );
                    
                    if( nSlotsRequiredForSection >  nNumberOfTimeSlots - nSlotsOccupiedByClassroom )
                    {
                        ExamSchedulerView.PrintNewLineToConsole( "Section " + Sections.ElementAt(j).Name + " (Strength " + Sections.ElementAt(j).Strength + ") skipped as slots remaining are " + (nNumberOfTimeSlots - nSlotsOccupiedByClassroom) + " whereas slots required are " + nSlotsRequiredForSection );
                        continue;
                    }

                    nMaxSectionStrength = Sections.ElementAt(j).Strength;
                    iMaxSectionStrength = j; 
                }
   
            if( nMaxClassroomCapacity == -2
             && nMaxSectionStrength   == -1 )
            {
                throw new RuntimeException("Error:  No available Classroom and Section found" );
            }

            if( nMaxSectionStrength == -1 )
            {
                // no more sections to assign
                break;
            }         
            
            if( nMaxSectionStrength > nMaxClassroomCapacity )
            {
                ExamSchedulerView.PrintNewLineToConsole( "Section " + Sections.ElementAt(iMaxSectionStrength).Name + " (Strength " + Sections.ElementAt(iMaxSectionStrength).Strength + ") skipped as " + ElementAt(iMaxClassroomCapacity).ClassroomName + " (Capacity " + ElementAt(iMaxClassroomCapacity).Capacity + " ) isn't big enough." );
                Sections.ElementAt(iMaxSectionStrength).fSkipAssignmentOfClassroom = true;
                
                // retry the same classroom with a different section
                continue;
            }
            else
            {
                // do the section-classroom assignment
                ElementAt(iMaxClassroomCapacity).AssignDegreeBatch(Sections.ElementAt(iMaxSectionStrength));
                Sections.ElementAt(iMaxSectionStrength).SetAssignedClassroom(ElementAt(iMaxClassroomCapacity).ClassroomName);
                
                ElementAt(iMaxClassroomCapacity).fSkipAssignmentOfClassroom = true;
                Sections.ElementAt(iMaxSectionStrength).fSkipAssignmentOfClassroom     = true;
                
                nAssignments++;

                ExamSchedulerView.PrintNewLineToConsole( "Section " + Sections.ElementAt(iMaxSectionStrength).Name + " (Strength " + Sections.ElementAt(iMaxSectionStrength).Strength + ") assigned to " + ElementAt(iMaxClassroomCapacity).ClassroomName + " (Capacity " + ElementAt(iMaxClassroomCapacity).Capacity + " )" );
            }

            if( nMaxClassroomCapacity > nOverallMaxClassroomCapacity )
            {
                nOverallMaxClassroomCapacity = nMaxClassroomCapacity;
                iOverallMaxClassroomCapacity = iMaxClassroomCapacity;
            }
            if( nMaxSectionStrength > nOverallMaxSectionStrength )
            {
                nOverallMaxSectionStrength = nMaxSectionStrength;
                iOverallMaxSectionStrength = iMaxSectionStrength;
            }
        }
    
        if( nOverallMaxSectionStrength > nOverallMaxClassroomCapacity )
        {
            //throw new RuntimeException("Error:  Even the biggest classroom (" + ClassRooms[iOverallMaxClassroomCapacity].ClassroomName + ") cannot accomodate Section " + DegreeBatch[iOverallMaxSectionStrength].Name );
        }
        
        return nAssignments;
    }
    
    // data
    
    Vector  Classrooms;
    
}

