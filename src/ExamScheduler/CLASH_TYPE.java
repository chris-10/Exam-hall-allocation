/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ExamScheduler;

/**
 *
 * @author christendsouza
 */
/*
 * ExamSchedulerView.java
 */

public enum CLASH_TYPE
{
    CLASH_NONE,
    CLASH_OF_TIMESLOT_AND_SECTIONS_SESSION_PREFERENCE,
    CLASH_FACULTY_TAKING_ANOTHER_CLASS_AT_TIMESLOT,
    CLASH_TEST_FOR_MAX_CONSECUTIVE_CLASSES_IN_ROW_FAILED,
    CLASH_TEST_FOR_MAX_CLASSES_A_DAY_TO_A_SECTION_BY_FACULTY_FAILED,
    CLASH_TEST_FOR_MAX_CLASSES_A_DAY_BY_FACULTY_FAILED,
    CLASH_TEST_MAX_DAYS_A_WEEK_OF_CLASSES_BY_FACULTY
}
/*public class CLASH_TYPE {

}*/
