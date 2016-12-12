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
public class ClassRoom {

    int Initialize( String classroomName, int nCapacity, boolean fIsLab, int nRows, int nColumns )
    {
        ClassroomName     = classroomName;
        Capacity          = nCapacity;
        IsLab             = fIsLab;
        Rows              = nRows;
        Columns           = nColumns;
        
        RemainingUnfilledColumns = Columns;
        NumberOfDegreeBatchesInClassroom = 0;

        AssignedDegreeBatches = new Vector();
        
        fSkipAssignmentOfClassroom = false;
        
        return 0;
    }

    void Reset()
    {
        RemainingUnfilledColumns = Columns;
        NumberOfDegreeBatchesInClassroom = 0;

        AssignedDegreeBatches.removeAllElements();        
    }
    
    int AssignDegreeBatch( DegreeBatch degreeBatch )
    {
        int nRet = degreeBatch.GetUnassignedStrength();
        int nColumnsToAssign;
        
        if( NumberOfDegreeBatchesInClassroom < 2 )
        {
            AssignedDegreeBatches.addElement(degreeBatch);
        
            if( NumberOfDegreeBatchesInClassroom == 0 )
            {
                nColumnsToAssign = (int)Math.ceil(((double)Columns)/2);
                
                if( nColumnsToAssign*Rows >= degreeBatch.GetUnassignedStrength() )
                {
                    if( (nColumnsToAssign-1)*Rows >= degreeBatch.GetUnassignedStrength() )
                        nColumnsToAssign = nColumnsToAssign - 1;
                    
                    nRet = degreeBatch.DecrementUnassignedStrength(degreeBatch.GetUnassignedStrength());
                }
                else
                    nRet = degreeBatch.DecrementUnassignedStrength( nColumnsToAssign*Rows );
                
                RemainingUnfilledColumns -= nColumnsToAssign;
            }
            else if( NumberOfDegreeBatchesInClassroom == 1)
            {
                nColumnsToAssign = (int)Math.ceil(((double)Columns)/2);

                // for even number total columns, you cannot assign more than columns/2 to any degree batch
                // for odd number total columns, you cannot assign more than columns/2 + 1 to any degree batch
                if( RemainingUnfilledColumns > nColumnsToAssign )
                    RemainingUnfilledColumns = nColumnsToAssign;
                
                if( RemainingUnfilledColumns*Rows >= degreeBatch.GetUnassignedStrength() )
                    nRet = degreeBatch.DecrementUnassignedStrength(degreeBatch.GetUnassignedStrength());
                else
                    nRet = degreeBatch.DecrementUnassignedStrength( RemainingUnfilledColumns*Rows );

                RemainingUnfilledColumns = 0;
            }
            NumberOfDegreeBatchesInClassroom++;
        }
        
        return nRet;
    }
    
    Vector GetAssignedDegreeBatches()
    {
        return AssignedDegreeBatches;
    }
    
    boolean IsALab()
    {
        return IsLab;
    }

    String     ClassroomName;
    Vector     AssignedDegreeBatches;
    int        Capacity;
    int        Rows;
    int        Columns;
    int        RemainingUnfilledColumns;
    int        NumberOfDegreeBatchesInClassroom;
    boolean    IsLab;
    boolean    fSkipAssignmentOfClassroom;
}
