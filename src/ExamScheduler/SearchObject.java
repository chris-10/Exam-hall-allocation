/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ExamScheduler;

/**
 *
 * @author christendsouza
 */
public class SearchObject {

    SearchObject()
    {
        Reset();
    }
    
    void Reset()
    {
        maxConstraintPriority = Float.MIN_VALUE;
        columnStartIndex      = -1;
        columnEndIndex        = -1;
        rowIndex              = -1;
    }

    //data
    Float maxConstraintPriority;
    int   columnStartIndex;
    int   columnEndIndex;
    int   rowIndex;
    
}
