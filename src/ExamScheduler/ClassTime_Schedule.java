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
public class ClassTime_Schedule {

    ClassTime_Schedule(  )
    {
    }
    
    void Initialize()
    {
        Set();
    }
    
    void Set()
    {
        Classroom             = "";
        Time                  = "";
        AssignedCourseID      = "";
        AssignedClassroom     = "";
        
        if( SchedulingConstraints != null )
            SchedulingConstraints.removeAllElements();
        
        AssignedStudentRegNos       = new Vector();
        AssignedDegreeBatchSections = new Vector();
        NumberOfAssignedStudents    = new Vector();
        SchedulingConstraints       = new Vector();
        AssignedDegreeBatch         = new Vector();
        AssignedCourseName          = new Vector();
        AssignedFacultyID           = new Vector();
        AssignedFacultyName         = new Vector();
        
        AllocationPriority = 1;        
    }
    
    public void Reset()
    {
        Set();
    }
    boolean IsFacultyConstraintForSlot( String faculty )
    {
        for( int i=0; i<SchedulingConstraints.size(); i++)
            if( true == SchedulingConstraints.elementAt(i).toString().equals(faculty) )
                return true;
        
        return false;
    }
    
    String GetConstraintString()
    {
        String constraintString = "";
        
        for( int i=0; i<SchedulingConstraints.size(); i++ )
            constraintString = constraintString + SchedulingConstraints.elementAt(i).toString();
        
        return constraintString;
    }
    void SetAssignedFacultyID( String faclutyID )
    {
        AssignedFacultyID.add(faclutyID);
    }
    Vector GetAssignedFacultyID()
    {
        return AssignedFacultyID;
    }
    void SetAssignedFacultyName( String faclutyName )
    {
        AssignedFacultyName.add(faclutyName);
    }
    Vector GetAssignedFacultyName()
    {
        return AssignedFacultyName;
    }
    void SetConstraint( String schedulingConstraint )
    {
        for( int i=0; i<SchedulingConstraints.size(); i++ )
            if( true == SchedulingConstraints.elementAt(i).toString().equals(schedulingConstraint) )
                return;
        
        SchedulingConstraints.add(schedulingConstraint);
    }
    Vector GetConstraint()
    {
        return SchedulingConstraints;
    }
    String SetAssignedCourseID( String assignedCourseID )
    {
        AssignedCourseID = assignedCourseID;
        
        return AssignedCourseID;
    }
    String GetAssignedCourseID()
    {
        return AssignedCourseID;
    }    
    void SetAssignedCourseName( String assignedCourseName )
    {
        AssignedCourseName.add(assignedCourseName);
    }
    Vector GetAssignedCourseName()
    {
        return AssignedCourseName;
    }    
    void SetAssignedDegreeBatch( String assignedDegreeBatch )
    {
        AssignedDegreeBatch.add(assignedDegreeBatch);
    }
    Vector GetAssignedDegreeBatch()
    {
        return AssignedDegreeBatch;
    }
    String SetAssignedClassroom( String assignedClassroom )
    {
        AssignedClassroom = assignedClassroom;
        
        return AssignedClassroom;
    }
    String GetAssignedClassroom()
    {
        return AssignedClassroom;
    }
    float SetAllocationPriority( float allocationPriority )
    {
        AllocationPriority = allocationPriority;
        
        return AllocationPriority;
    }
    float GetAllocationPriority( )
    {
        return AllocationPriority;
    }
    void SetNumberOfAssignedStudents( int nStudents )
    {
        NumberOfAssignedStudents.add(nStudents);
    }
    Vector GetNumberOfAssignedStudents()
    {
        return NumberOfAssignedStudents;
    }
    void SetAssignedStudentRegNos( String RegNos )
    {
        AssignedStudentRegNos.add(RegNos);
    }
    Vector GetAssignedStudentRegNos()
    {
        return AssignedStudentRegNos;
    }
    void SetAssignedDegreeBatchSections( String DegreeBatchSections )
    {
        AssignedDegreeBatchSections.add(DegreeBatchSections);
    }
    Vector GetAssignedDegreeBatchSections()
    {
        return AssignedDegreeBatchSections;
    }
    
    private String Classroom;
    private String Time;
    private String AssignedCourseID;
    private String AssignedClassroom;

    private Vector AssignedStudentRegNos;
    private Vector AssignedDegreeBatchSections;
    private Vector NumberOfAssignedStudents;
    private Vector AssignedCourseName;
    private Vector AssignedDegreeBatch;
    private Vector AssignedFacultyID;
    private Vector AssignedFacultyName;
    private Vector SchedulingConstraints;
    private float AllocationPriority;
}
