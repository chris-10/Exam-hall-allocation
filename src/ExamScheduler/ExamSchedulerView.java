/*
 * ExamSchedulerView.java
 */
package ExamScheduler;

//import java.awt.Cursor;
import java.awt.Cursor;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;

import java.sql.*;
import java.lang.Integer;
import java.util.Vector;
import java.util.Date;

import java.awt.event.*;
import javax.swing.*;

import java.awt.Graphics2D;
import java.awt.print.*;
import java.io.BufferedWriter;
import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.*;
import javax.print.*;

import javax.print.attribute.*;
import javax.print.attribute.standard.*;

import java.util.Locale;
import javax.swing.table.TableModel;

/**
 * The application's main frame.
 */
public class ExamSchedulerView extends FrameView {

    public ExamSchedulerView(SingleFrameApplication app) {
        super(app);

        ClassRooms = new ClassRoomCollection();
        Courses = new CourseCollection();
        DegreeBatch = new DegreeBatchCollection();
        FacultyMembers = new FacultyCollection();
        Students = new StudentCollection();
        //Status = new StatusCollection();
        //Department = new DepartmentCollection();
        //this.setTitle("Exam Hall Allocation");

        initComponents();

        QueryAndLoadInitialDataFromDatabase();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });

        jTabPane.setSelectedIndex(0);

    }

    private void QueryAndLoadInitialDataFromDatabase() {
        try {
            String fullTableColumnHeaderValues[] = null;
            String sectionTableColumnHeaderValues[] = null;
            Vector tempVector = new Vector();
            int nSize = 0, nDays = 0;
            ResultSet rs;
            Statement s;

            StatusBarText = "";
            fToggleDetailedView = !fToggleDetailedView;

            //String dataSourceName = "Exam Scheduler Database";
            //String dbURL = "jdbc:odbc:" + dataSourceName;

            //Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            //Connection con = DriverManager.getConnection(dbURL, "shahid", "shahid");
            //s = con.createStatement();
            Class.forName("com.mysql.jdbc.Driver");
	    DriverManager.registerDriver(new com.mysql.jdbc.Driver());
					
	    String url="jdbc:mysql://localhost:3306/ExamDB";//?autoReconnect=true&relaxAutoCommit=true";
						
	    Connection cn=null;
            cn=DriverManager.getConnection(url,"root","210296book");
	    Statement st=cn.createStatement();   
                                                
            String sqlQuery = "select * from Class";

            rs=st.executeQuery(sqlQuery);
            if (rs != null) {
                // step through the output data row-by-row
                for (int i = 0; rs.next(); i++) {
                    String classroomName = rs.getString(1);
                    String capacity = rs.getString(5);
                    String alloted = rs.getString(4);
                    String rows = rs.getString(3);
                    String columns = rs.getString(2);
                    boolean occ = alloted.equalsIgnoreCase("Yes");
                    Integer nCapacity = Integer.valueOf(capacity);
                    Integer nRows = Integer.valueOf(rows);
                    Integer nColumns = Integer.valueOf(columns);

                    ClassRooms.AddClassroom(classroomName, nCapacity, occ, nRows, nColumns);
                }
            }
            rs.close();

            //s.execute("select [Day], [StartTime], [EndTime] from Timeslots");
            String sqlQuery2 = "select Day,Session1 from Subject";

            rs=st.executeQuery(sqlQuery2);
            
            for (nSize = 0, nDays = 0; rs != null && rs.next(); nSize++) {
                String dayOfWeek = rs.getString(1);
                int j;
                for (j = 0; j < nDays; j++) {
                    if (tempVector.get(j).equals(dayOfWeek) == true) {
                        break;
                    }
                }

                if (j == nDays) {
                    // no entry exists for this day, create a new one 
                    tempVector.add(dayOfWeek);
                    nDays++;
                }
            }
            rs.close();

            nNumberOfTimeSlots = nSize;
            nNumberOfWorkingDaysPerWeek = nDays;

            TimeSlots = new TimeSlot[nNumberOfTimeSlots];
            Days = new Day[nNumberOfWorkingDaysPerWeek];
            //JOptionPane.showMessageDialog(null, nNumberOfTimeSlots);
            //JOptionPane.showMessageDialog(null, nNumberOfWorkingDaysPerWeek);

            rs=st.executeQuery("select Day,Session1 from Subject");
            if (rs != null) {
                fullTableColumnHeaderValues = new String[nNumberOfTimeSlots + 1];
                fullTableColumnHeaderValues[0] = new String("Classroom");

                sectionTableColumnHeaderValues = new String[nNumberOfWorkingDaysPerWeek + 1];
                sectionTableColumnHeaderValues[0] = new String("Time Slot");

                tempVector.removeAllElements();
                nDays = -1;
                int startTime,endTime;
                // step through the output data row-by-row
                for (int i = 0; rs.next(); i++) {
                    String day = rs.getString(1);
                    String session= rs.getString(2);
                    if(session.compareTo("0")==0){
                        startTime = 930;
                        endTime = 1130;
                    }
                    else{
                        startTime = 1430;
                        endTime = 1630;
                    }

                    TimeSlots[i] = new TimeSlot();
                    TimeSlots[i].Initialize(day, startTime, endTime);
                    
                   
                    // add the slot to the appropriate Day object
                    if (nDays >= 0 && Days[nDays].Name.equals(day) == true) {
                        Days[nDays].NumberOfSlots++;
                    } else {
                        nDays++;
                        Days[nDays] = new Day();
                        Days[nDays].Initialize(day);
                        Days[nDays].NumberOfSlots++;
                        
                        sectionTableColumnHeaderValues[nDays + 1] = new String(day + "\n ");
                    }
                    fullTableColumnHeaderValues[i + 1] = new String(day + "\n" + String.valueOf(startTime) + " - " + String.valueOf(endTime));
                    
                    if (nDays <= 0 && i == 0) {
                        tempVector.add("     " + String.valueOf(startTime) + " - " + String.valueOf(endTime));
                    } else if (nDays <= 0) {
                        tempVector.add("  " + String.valueOf(startTime) + " - " + String.valueOf(endTime));
                    }
                }
            }
            
            rs.close();
            st.close();
            cn.close();

            // Sort the classroom by name
            ClassRooms.Sort();

            // initialize the weekly schedule 2D array now that we know the number of classrooms
            FullSchedule = new ClassTime_Schedule[ClassRooms.GetCount()][nNumberOfTimeSlots + 1];
            DegreeBatchSchedule = new ClassTime_Schedule[nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek][nNumberOfWorkingDaysPerWeek + 1];
            FacultySchedule = new ClassTime_Schedule[nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek][nNumberOfWorkingDaysPerWeek + 1];
            ClassroomSchedule = new ClassTime_Schedule[nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek][nNumberOfWorkingDaysPerWeek + 1];
            CourseSchedule = new ClassTime_Schedule[nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek][nNumberOfWorkingDaysPerWeek + 1];

            for (int j = 0; j < ClassRooms.GetCount(); j++) {
                for (int i = 0; i < nNumberOfTimeSlots; i++) {
                    FullSchedule[j][i] = new ClassTime_Schedule();
                    FullSchedule[j][i].Initialize();
                }
            }

            for (int j = 0; j < nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek; j++) {
                for (int i = 0; i < nNumberOfWorkingDaysPerWeek; i++) {
                    DegreeBatchSchedule[j][i] = new ClassTime_Schedule();
                    DegreeBatchSchedule[j][i].Initialize();
                }
            }

            for (int j = 0; j < nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek; j++) {
                for (int i = 0; i < nNumberOfWorkingDaysPerWeek; i++) {
                    FacultySchedule[j][i] = new ClassTime_Schedule();
                    FacultySchedule[j][i].Initialize();
                }
            }

            for (int j = 0; j < nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek; j++) {
                for (int i = 0; i < nNumberOfWorkingDaysPerWeek; i++) {
                    ClassroomSchedule[j][i] = new ClassTime_Schedule();
                    ClassroomSchedule[j][i].Initialize();
                }
            }

            for (int j = 0; j < nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek; j++) {
                for (int i = 0; i < nNumberOfWorkingDaysPerWeek; i++) {
                    CourseSchedule[j][i] = new ClassTime_Schedule();
                    CourseSchedule[j][i].Initialize();
                }
            }

            jFullScheduleTable.setModel(new javax.swing.table.DefaultTableModel(FullSchedule, fullTableColumnHeaderValues));
            jDegreeBatchScheduleTable.setModel(new javax.swing.table.DefaultTableModel(DegreeBatchSchedule, sectionTableColumnHeaderValues));
            jFacultyScheduleTable.setModel(new javax.swing.table.DefaultTableModel(FacultySchedule, sectionTableColumnHeaderValues));
            jClassroomScheduleTable.setModel(new javax.swing.table.DefaultTableModel(ClassroomSchedule, sectionTableColumnHeaderValues));
            jCourseScheduleTable.setModel(new javax.swing.table.DefaultTableModel(CourseSchedule, sectionTableColumnHeaderValues));

            // Full Schedule: set the names to the rows (this will go in the very first column)
            jFullScheduleTable.getColumnModel().getColumn(0).setPreferredWidth(100);
            for (int i = 0; i < jFullScheduleTable.getRowCount(); i++) {
                jFullScheduleTable.setValueAt(ClassRooms.ElementAt(i).ClassroomName, i, 0);
            }

            // Section, Faculty, Classroom and Course Schedule: set the names to the rows (this will go in the very first column)
            for (int i = 0; i < jDegreeBatchScheduleTable.getRowCount(); i++) {
                jDegreeBatchScheduleTable.setValueAt(tempVector.elementAt(i), i, 0);
                jFacultyScheduleTable.setValueAt(tempVector.elementAt(i), i, 0);
                jClassroomScheduleTable.setValueAt(tempVector.elementAt(i), i, 0);
                jCourseScheduleTable.setValueAt(tempVector.elementAt(i), i, 0);
            }

            // reset the schedules (2D array of objects)and the table model 
            for (int j = 0; j < ClassRooms.GetCount(); j++) {
                for (int i = 0; i < nNumberOfTimeSlots; i++) {
                    FullSchedule[j][i].Reset();
                    jFullScheduleTable.setValueAt("", j, i + 1);
                }
            }

            for (int j = 0; j < nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek; j++) {
                //jSectionScheduleTable.getColumnModel().getColumn(j+1).setPreferredWidth( 150 );
                //jFacultyScheduleTable.getColumnModel().getColumn(j+1).setPreferredWidth( 150 );

                for (int i = 0; i < nNumberOfWorkingDaysPerWeek; i++) {
                    DegreeBatchSchedule[j][i].Reset();
                    jDegreeBatchScheduleTable.setValueAt("", j, i + 1);

                    FacultySchedule[j][i].Reset();
                    jFacultyScheduleTable.setValueAt("", j, i + 1);

                    ClassroomSchedule[j][i].Reset();
                    jClassroomScheduleTable.setValueAt("", j, i + 1);

                    CourseSchedule[j][i].Reset();
                    jCourseScheduleTable.setValueAt("", j, i + 1);
                }
            }
            // done initializing


            // do the initial display of (empty) schedule table
            DisplayEmptySchedule();

            // set the status bar text
            

            StatusBarText = "      To Start:  Click on 'Run Schedule' Button";
            statusMessageLabel.setText(StatusBarText);

        } catch (Exception e) {
            statusMessageLabel.setText("            Error6: " + e);
            JOptionPane.showMessageDialog(null, "Error: "+e);
        }
    }

    private void QuearyAndLoadFinalDataFromDatabase() {
        try {
            int nSize = 0;
            Statement st;
            ResultSet rs;
            int l;

            //String dataSourceName = "Exam Scheduler Database";
            //String dbURL = "jdbc:odbc:" + dataSourceName;

            if (fScheduleCreated == true) {
                QueryAndLoadInitialDataFromDatabase();
            } else {
                fScheduleCreated = true;
            }

            //Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            //Connection con = DriverManager.getConnection(dbURL, "shahid", "shahid");
            Class.forName("com.mysql.jdbc.Driver");
	    DriverManager.registerDriver(new com.mysql.jdbc.Driver());
					
	    String url="jdbc:mysql://localhost:3306/ExamDB";//?autoReconnect=true&relaxAutoCommit=true";
						
	    Connection cn=null;
            cn=DriverManager.getConnection(url,"root","210296book");
	    st=cn.createStatement();   

            rs=st.executeQuery("select SCode,SName from Subject");

            // query Courses table

            if (rs != null) {
                // step through our data row-by-row
                for (int i = 1; rs.next(); i++) {
                    String courseID = rs.getString(1),
                            courseName = rs.getString(2);

                    Courses.AddCourse(courseID, courseName, 0, 0, "");
                }
            }
            st.close();
            rs.close();
            //done querying Courses table

            // query Faculty table
            st = cn.createStatement();
            //s.execute("select [Faculty id], [Faculty Name], [Department], [Gender] from Faculty");
            rs=st.executeQuery("select FID,FName,Dept,Gender from Faculty");
            if (rs != null) {
                // step through the output data row-by-row
                for (int i = 1; rs.next(); i++) {
                    String facultyID = rs.getString(1),
                            facultyName = rs.getString(2),
                            facultyDepartment = rs.getString(3),
                            facultyGender = rs.getString(4);

                    FacultyMembers.AddFaculty(facultyID, facultyName, facultyDepartment, facultyGender, ClassRooms.GetCount(), nNumberOfTimeSlots, "", 0, 0, 0, nNumberOfTimeSlots);
                }
            }
            
            rs.close();
            st.close();


            // query Students table
            st = cn.createStatement();
            //s.execute("select [Student ID], [Name], [Degree], [Batch], [Section] from Students");
            
            rs=st.executeQuery("select USN,Name,Branch,Sem,Section from Student");
            // select the data from the table

            if (rs != null) {
                // step through our data row-by-row
                for (int i = 1; rs.next(); i++) {
                    String studentID = rs.getString(1);
                    String studentName = rs.getString(2);
                    String degree = rs.getString(3);
                    String batch = rs.getString(4);
                    String section = rs.getString(5);
                    String courseList  = "";
                    
                    Students.AddStudent(studentID, studentName ,courseList, degree, batch, section);
                    // add the student ID to the courses enrolled
                    for (int j = 0; j < Students.ElementAt(i - 1).NumberOfCoursesEnrolled; j++){
                        String course = Students.ElementAt(i - 1).CoursesEnrolled.elementAt(j).toString();
                        for (int k = 0; k < Courses.GetCount(); k++) {
                            if (Courses.ElementAt(k).ID.equals(course) == true) {
                                Courses.ElementAt(k).AddStudent(studentID);
                                break;
                            }
                        }
                    }
                    // add the student ID to the appropriate section
                    //section = degree.trim() +  batch.trim() + section.trim();
                    section = degree.trim() + "-" + batch.trim();

                    for (l = 0; l < DegreeBatch.GetCount(); l++) {
                        if (DegreeBatch.ElementAt(l).Name.equals(section) == true) {
                            break;
                        }
                    }

                    if (l == DegreeBatch.GetCount()) {
                        DegreeBatch.AddSection(section);
                    }
                    
                    for (int j = 0; j < DegreeBatch.GetCount(); j++) {
                        if (DegreeBatch.ElementAt(j).Name.equals(section) == true) {
                            DegreeBatch.ElementAt(j).AddStudent(studentID, Students.ElementAt(i - 1).CoursesEnrolled, Courses);
                            break;
                        }
                    }
                }
            }
            st.close();
            cn.close();
        } catch (Exception e) {
            statusMessageLabel.setText("            Error:     QuearyAndLoadFinalDataFromDatabase():  " + e);
            JOptionPane.showMessageDialog(null, "Error: "+e);
        }

    }

    @Action
    public void RunScheduler() {
        try {
            Vector facultyNames = new Vector();
            Vector degreeBatchNames = new Vector();
            Vector classroomNames = new Vector();
            Vector courseNames = new Vector();

            Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
            Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);

            ExamSchedulerApp.getApplication().getMainFrame().setCursor(hourglassCursor);

            // initial data was already loaded, now load the rest of it from the database
            QuearyAndLoadFinalDataFromDatabase();

            // sort the classroom names & populate the classroom name vector
            ClassRooms.Sort();
            classroomNames.add("<Select One>");
            classroomNames.add("(All)");
            for (int i = 0; i < ClassRooms.GetCount(); i++) {
                classroomNames.add(ClassRooms.ElementAt(i).ClassroomName);
            }

            // sort the section names & populate the section name vector
            DegreeBatch.Sort();
            degreeBatchNames.add("<Select One>");
            degreeBatchNames.add("(All)");
            for (int i = 0; i < DegreeBatch.GetCount(); i++) {
                degreeBatchNames.add(DegreeBatch.ElementAt(i).Name);
            }

            // sort the faculty names vector & populate the faculty name vector
            FacultyMembers.Sort();
            facultyNames.add("<Select One>");
            facultyNames.add("(All)");
            for (int i = 0; i < FacultyMembers.GetCount(); i++) {
                facultyNames.add(FacultyMembers.ElementAt(i).Name);
            }

            // sort the faculty names vector & populate the faculty name vector
            Courses.Sort();
            courseNames.add("<Select One>");
            courseNames.add("(All)");
            for (int i = 0; i < Courses.GetCount(); i++) {
                courseNames.add(Courses.ElementAt(i).Name);
            }


            // populate the Combo boxes
            jDegreeBatchComboBox.setModel(new javax.swing.DefaultComboBoxModel(degreeBatchNames));
            jDegreeBatchComboBox.setSelectedIndex(0);
            jDegreeBatchComboBox.setName("jDegreeBatchComboBox");

            jFacultyComboBox.setModel(new javax.swing.DefaultComboBoxModel(facultyNames));
            jFacultyComboBox.setSelectedIndex(0);
            jFacultyComboBox.setName("jFacultyComboBox");

            jClassroomComboBox.setModel(new javax.swing.DefaultComboBoxModel(classroomNames));
            jClassroomComboBox.setSelectedIndex(0);
            jClassroomComboBox.setName("jClassroomComboBox");

            jCourseComboBox.setModel(new javax.swing.DefaultComboBoxModel(courseNames));
            jCourseComboBox.setSelectedIndex(0);
            jCourseComboBox.setName("jCourseComboBox");

            jCourseComboBox.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    ResetCourseSchedule();
                    fToggleDetailedView = !fToggleDetailedView;
                    DisplayCourseSchedule();
                    fToggleDetailedView = !fToggleDetailedView;
                    jTabPane.setSelectedIndex(1);
                }
            });

            jFacultyComboBox.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    ResetFacultySchedule();
                    fToggleDetailedView = !fToggleDetailedView;
                    DisplayFacultySchedule();
                    fToggleDetailedView = !fToggleDetailedView;
                    jTabPane.setSelectedIndex(2);
                }
            });

            jDegreeBatchComboBox.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    ResetDegreeBatchSchedule();
                    fToggleDetailedView = !fToggleDetailedView;
                    DisplayDegreeBatchSchedule();
                    fToggleDetailedView = !fToggleDetailedView;
                    jTabPane.setSelectedIndex(3);
                }
            });

            jClassroomComboBox.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    ResetClassroomSchedule();
                    fToggleDetailedView = !fToggleDetailedView;
                    DisplayClassroomSchedule();
                    fToggleDetailedView = !fToggleDetailedView;
                    jTabPane.setSelectedIndex(4);
                }
            });

            // update the status bar

            jDegreeBatchCountLabel.setText("" + DegreeBatch.GetCount());
            jFacultyCountLabel.setText("" + FacultyMembers.GetCount());
            jClassroomCountLabel.setText("" + ClassRooms.GetCount());
            jStudentCountLabel.setText("" + Students.GetCount());
            jCourseCountLabel.setText("" + Courses.GetCount());

            StatusBarText = "";
            statusMessageLabel.setText(StatusBarText);

            //CreateDateSheet();

            //SaveDateSheet();

            ReadDateSheet();

            AssignCoursesAndDegreeBatchesToClassrooms();

            AssignFacultyToClassrooms();

            // do the scheduling
            //ScheduleFacultyAndCoursesInClassrooms( );

            jTabPane.setSelectedIndex(0);

            ExamSchedulerApp.getApplication().getMainFrame().setCursor(normalCursor);

        } catch (Exception e) {
            statusMessageLabel.setText("            Error1:    RunScheduler():   " + e);
            JOptionPane.showMessageDialog(null, "Error: "+e);
        }
    }

    private int ReadDateSheet() {
        try {
            ResultSet rs;
            Statement s;

            StatusBarText = "";
            fToggleDetailedView = !fToggleDetailedView;

            Class.forName("com.mysql.jdbc.Driver");
	    DriverManager.registerDriver(new com.mysql.jdbc.Driver());
					
	    String url="jdbc:mysql://localhost:3306/ExamDB";//?autoReconnect=true&relaxAutoCommit=true";
						
	    Connection cn=null;
            cn=DriverManager.getConnection(url,"root","210296book");
	    Statement st=cn.createStatement();   
                                                
            String sqlQuery = "select Day,SName,Session1,Branch,Sem from Subject";

            rs=st.executeQuery(sqlQuery);

            //s.execute("select [Day], [Course Name], [Timeslot], [Degree_Batch] from Datesheet");
            if (rs != null) {
                // step through the output data row-by-row
                for (int i = 0; rs.next(); i++) {
                    Day courseDay = new Day();
                    TimeSlot timeSlot = new TimeSlot();
                    String courseName = rs.getString(2),
                            degreeBatch = rs.getString(4)+'-'+rs.getString(5);
                    courseDay.Initialize(rs.getString(1));
                    if(rs.getString(3).compareTo("0")==0){
                        timeSlot.Initialize(courseDay.Name, "930-1130");
                    }
                    else{
                        timeSlot.Initialize(courseDay.Name, "1430-1630");
                    }
                    //JOptionPane.showMessageDialog(null,i);
                    CourseSchedule[timeSlot.StartTimeRowOffset][courseDay.DaysOffset].SetAssignedDegreeBatch(degreeBatch);
                    CourseSchedule[timeSlot.StartTimeRowOffset][courseDay.DaysOffset].SetAssignedCourseName(courseName);
                }
            }
            rs.close();

            st.close();
            cn.close();
        } catch (Exception e) {
            statusMessageLabel.setText("            Error2:       " + e);
            JOptionPane.showMessageDialog(null, "Error: "+e);
        }

        return 0;
    }

    private int AssignCoursesAndDegreeBatchesToClassrooms() {
        try {
            int nUnassignedStudents, previousUnassignedStudents, studentsFromDegreeBatchAssignedToClassroom;
            String DegreeBatchSections;

            // sort relevant data first
            ClassRooms.Sort();
            DegreeBatch.SortBySize();

            // first sort students by RegNo, then Year and finally by Section
            Students.SortByRegNo();
            Students.SortByYear();
            Students.SortBySection();
            //done sorting

            //assign batches to classrooms
            for (int i = 0; i < nNumberOfWorkingDaysPerWeek; i++) {
                for (int j = 0; j < nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek; j++) {
                    for (int l = 0; l < DegreeBatch.GetCount(); l++) {
                        for (int m = 0,  k,  p; m < CourseSchedule[j][i].GetAssignedCourseName().size(); m++) {
                            if (DegreeBatch.ElementAt(l).Name.equals(CourseSchedule[j][i].GetAssignedDegreeBatch().elementAt(m).toString()) == true) {
                                StudentSerialNo = 0;
                                for (k = 0; k < ClassRooms.GetCount(); k++) {
                                    if (ClassRooms.ElementAt(k).Capacity <= 0) {
                                        continue;
                                    }

                                    for (p = 0; p < FullSchedule[k][i * (nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek) + j].GetAssignedCourseName().size(); p++) {
                                        
                                        String tempCourse1 = FullSchedule[k][i * (nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek) + j].GetAssignedCourseName().elementAt(p).toString();
                                        String tempCourse2 = CourseSchedule[j][i].GetAssignedCourseName().elementAt(m).toString();

/*
                                        if( tempCourse1.contains("Linear") == true
                                         || tempCourse2.contains("Linear") == true  )
                                            tempCourse1 = tempCourse1;
*/
                                        if (true == FullSchedule[k][i * (nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek) + j].GetAssignedCourseName().elementAt(p).toString().trim().equals(CourseSchedule[j][i].GetAssignedCourseName().elementAt(m).toString().trim())) {
                                            break;
                                        }
                                    }
                                    //JOptionPane.showMessageDialog(null, k);


                                    if (p == FullSchedule[k][i * (nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek) + j].GetAssignedCourseName().size()) {
                                        previousUnassignedStudents = nUnassignedStudents = DegreeBatch.ElementAt(l).GetUnassignedStrength();
                                        nUnassignedStudents = ClassRooms.ElementAt(k).AssignDegreeBatch(DegreeBatch.ElementAt(l));

                                        if (nUnassignedStudents != previousUnassignedStudents) {
                                            studentsFromDegreeBatchAssignedToClassroom = previousUnassignedStudents - nUnassignedStudents;

                                            // if only one student will be left after this, then just assign the extra kid to this classroom
                                            if (nUnassignedStudents == 1) {
                                                studentsFromDegreeBatchAssignedToClassroom++;
                                                nUnassignedStudents--;
                                            }

                                            FullSchedule[k][i * (nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek) + j].SetAssignedDegreeBatch(CourseSchedule[j][i].GetAssignedDegreeBatch().elementAt(m).toString());
                                            FullSchedule[k][i * (nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek) + j].SetAssignedCourseName(CourseSchedule[j][i].GetAssignedCourseName().elementAt(m).toString());
                                            FullSchedule[k][i * (nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek) + j].SetNumberOfAssignedStudents(studentsFromDegreeBatchAssignedToClassroom);
                                            
                                            RegNoRange = "";
                                            DegreeBatchSections = "";
                                            
                                            DegreeBatchSections = GetStudentsRegistrationNumberRange(studentsFromDegreeBatchAssignedToClassroom, l, DegreeBatchSections);
                                            
                                            FullSchedule[k][i * (nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek) + j].SetAssignedStudentRegNos(RegNoRange);
                                            FullSchedule[k][i * (nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek) + j].SetAssignedDegreeBatchSections(DegreeBatchSections);
                                            if (nUnassignedStudents == 0) {
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (k == ClassRooms.GetCount()) {
                                    DisplayFilledSchedule();
                                    throw new RuntimeException("Error:  Could not assign a classroom to " + DegreeBatch.ElementAt(l).Name);
                                }
                            }
                        }
                    }
                    for (int n = 0; n < ClassRooms.GetCount(); n++) {
                        ClassRooms.ElementAt(n).Reset();
                    }

                    for (int l = 0; l < DegreeBatch.GetCount(); l++) {
                        DegreeBatch.ElementAt(l).UnassignedStrength = DegreeBatch.ElementAt(l).Strength;
                    }
                }
            }

            DisplayFullSchedule();
        } catch (Exception e) {
            statusMessageLabel.setText("            Error3:       " + e);
            JOptionPane.showMessageDialog(null, "Error: "+e);
        }
        return 0;
    }

    private String GetStudentsRegistrationNumberRange(int studentsFromDegreeBatchAssignedToClassroom, int nDegreeBatch, String DegreeBatchSections) {
        String PreviousStudentsSection = "", CurrentStudentsSection = "";
        String PreviousRegNo = "", LastAddedRegNo = "";
        String PreviousStudentYearValue = "";

        for (int q = 0; q < studentsFromDegreeBatchAssignedToClassroom; q++) {
            int nIndex = -1, previousValue = 0;
            String StudentYear;
            String StudentID;
            String RegNo;

            // find next student of the degree batch
            while (Students.ElementAt(StudentSerialNo).DegreeBatch.equals(DegreeBatch.ElementAt(nDegreeBatch).Name) == false) {
                StudentSerialNo++;
            }

            StudentID = Students.ElementAt(StudentSerialNo).ID;
            CurrentStudentsSection = Students.ElementAt(StudentSerialNo).Section;


            if (DegreeBatchSections.contains(Students.ElementAt(StudentSerialNo).Section) == false) {
                DegreeBatchSections += Students.ElementAt(StudentSerialNo).Section;
            }

            /*do {
                previousValue = nIndex;
                nIndex = StudentID.indexOf('-', nIndex + 1);
            } while (nIndex != -1);*/
            previousValue=2;
            StudentYear = "16";
            RegNo = StudentID;


            if (PreviousStudentYearValue.equals(StudentYear) == false || PreviousStudentsSection.equals(CurrentStudentsSection) == false) {
                if (PreviousRegNo.equals(LastAddedRegNo) == false) // check for avoiding situation like " ... & 26 - 26 & ... "
                {
                    if (PreviousStudentYearValue.equals("") == false) {
                        RegNoRange += " - ";
                    }
                    RegNoRange += PreviousRegNo;
                    LastAddedRegNo = PreviousRegNo;
                }
                if (PreviousStudentYearValue.equals("") == false) {
                    RegNoRange += " & ";
                }
                RegNoRange += RegNo;
                LastAddedRegNo = RegNo;
            }

            PreviousStudentsSection = CurrentStudentsSection;
            PreviousStudentYearValue = StudentYear;
            PreviousRegNo = RegNo;

            StudentSerialNo++;
        }

        if (PreviousRegNo.equals(LastAddedRegNo) == false) // check for avoiding situation like " ... & 26 - 26 & ... "
        {
            RegNoRange += " - ";
            RegNoRange += PreviousRegNo;
        }

        return DegreeBatchSections;
    }

    private int AssignFacultyToClassrooms() {
        try {
            int nNumberOfFacultyRequiredInClassroom, nIndexInFacultyMembersList;
            int nAssignedStudentsInClassroom, nUnassignedInvigilationSlots = 0;
            Vector PrefferedFaculty = new Vector();
            Vector FreeFaculty = new Vector();
            int nCountOfClassroomSessions = 0;
            boolean fCanAssignFacultyToSlot;

            //Date   currentTime = new Date();
            //FacultyRandomizingSeed = currentTime.getTime();

            // randomize the order of Faculty Members
            FacultyMembers.Randomize(FacultyRandomizingSeed);

            for (int i = 0; i < nNumberOfTimeSlots; i++) {
                for (int j = 0; j < ClassRooms.GetCount(); j++) {
                    nAssignedStudentsInClassroom = 0;
                    for (int m = 0; m < FullSchedule[j][i].GetNumberOfAssignedStudents().size(); m++) {
                        nAssignedStudentsInClassroom += Integer.valueOf(FullSchedule[j][i].GetNumberOfAssignedStudents().elementAt(m).toString());
                    }

                    if (nAssignedStudentsInClassroom > 0) {
                        nCountOfClassroomSessions++;
                    }

                    nNumberOfFacultyRequiredInClassroom = (int) Math.ceil(((double) nAssignedStudentsInClassroom) / STUDENTS_INVIGILATED_BY_ONE_FACULTY);

                    for (int k = 0,  nIndexOfFacultyToAssign; k < nNumberOfFacultyRequiredInClassroom; k++) {
                        if (FreeFaculty.size() == 0) {
                            for (int h = 0; h < FacultyMembers.GetCount(); h++) {
                                FreeFaculty.add(FacultyMembers.ElementAt(h).Name);
                            }
                        }

                        fCanAssignFacultyToSlot = false;
                        nIndexOfFacultyToAssign = 0;

                        for (int nAttempts = 0; nAttempts < 4 && fCanAssignFacultyToSlot == false; nAttempts++) {
                            switch (nAttempts) {
                                case 0:
                                case 1:
                                    PrefferedFaculty = GetFacultyAssignedToClassSameTimeYesterday(j, i);

                                    if (nAttempts < PrefferedFaculty.size()) {
                                        for (nIndexOfFacultyToAssign = 0; nIndexOfFacultyToAssign < FreeFaculty.size(); nIndexOfFacultyToAssign++) {
                                            if (FreeFaculty.elementAt(nIndexOfFacultyToAssign).toString().equals(PrefferedFaculty.elementAt(nAttempts).toString()) == true) {
                                                break;
                                            }
                                        }

                                        if (nIndexOfFacultyToAssign < FreeFaculty.size() && CanAssignFacultyToSlot(PrefferedFaculty.elementAt(nAttempts).toString(), j, i) == true) {
                                            fCanAssignFacultyToSlot = true;
                                        }
                                    }
                                    break;

                                case 2:
                                    // more classroom with more than one facluty member required, one faculty should be a female
                                    if (nNumberOfFacultyRequiredInClassroom > 1 && k == 0) {
                                        nIndexOfFacultyToAssign = GetFirstFemaleFaculty(FreeFaculty);

                                        if (nIndexOfFacultyToAssign != -1 && CanAssignFacultyToSlot(FreeFaculty.elementAt(nIndexOfFacultyToAssign).toString(), j, i) == true) {
                                            fCanAssignFacultyToSlot = true;
                                            break;
                                        }
                                    }
                                    break;
                                case 3:
                                    for (nIndexOfFacultyToAssign = 0; nIndexOfFacultyToAssign < FreeFaculty.size(); nIndexOfFacultyToAssign++) {
                                        nIndexInFacultyMembersList = FacultyMembers.GetFacultyIndexUsingName(FreeFaculty.elementAt(nIndexOfFacultyToAssign).toString());

                                        // we should only be assigning male faculty members here ... females are all handled above
                                        if (FacultyMembers.ElementAt(nIndexInFacultyMembersList).GetGender().equalsIgnoreCase("H") == false && CanAssignFacultyToSlot(FreeFaculty.elementAt(nIndexOfFacultyToAssign).toString(), j, i) == true) {
                                            fCanAssignFacultyToSlot = true;
                                            break;
                                        }
                                    }
                                    break;
                            }
                        }

                        //if (fCanAssignFacultyToSlot == true) {
                            FullSchedule[j][i].SetAssignedFacultyName(FreeFaculty.remove(nIndexOfFacultyToAssign).toString());
                        /*} else {
                            FullSchedule[j][i].SetAssignedFacultyName("<NO FACULTY ASSIGNABLE>");
                            nUnassignedInvigilationSlots++;
                        }*/
                    }
                }
            }

            if (nUnassignedInvigilationSlots > 0) {
                statusMessageLabel.setText("ERROR:  No faculty assigned to " + nUnassignedInvigilationSlots + " invigilation slots!!!!");
            }
        } catch (Exception e) {
            statusMessageLabel.setText("            Error4:       " + e);
            PrintNewLineToConsole("Error: " + e);
        }

        return 0;
    }

    private Vector GetFacultyAssignedToClassSameTimeYesterday(int row, int column) {
        int ColumnsInADay = nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek;

        if (column - ColumnsInADay >= 0) {
            return FullSchedule[row][column - ColumnsInADay].GetAssignedFacultyName();
        } else {
            return new Vector();
        }
    }

    private int GetFirstFemaleFaculty(Vector FacultyList) {
        int nIndex = -1, nIndexOfFaculty;

        for (int i = 0; i < FacultyList.size(); i++) {
            nIndexOfFaculty = FacultyMembers.GetFacultyIndexUsingName(FacultyList.elementAt(i).toString());

            if (FacultyMembers.ElementAt(nIndexOfFaculty).GetGender().equalsIgnoreCase("F") == true) {
                nIndex = i;
                break;
            }
        }

        return nIndex;
    }

    private boolean CanAssignFacultyToSlot(String FacultyToCheck, int row, int column) {
        boolean fFacultyToCheckGenderMale = true, fOtherFacultyAssignedToClassroomGenderMale;
        int NumberOfSlotsPerDay = nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek;
        int nIndexOfFacultyToCheck, nIndexOtherFacultyAssignedToClassroom;
        int k;

        try {
            nIndexOfFacultyToCheck = FacultyMembers.GetFacultyIndexUsingName(FacultyToCheck);

            if (FacultyMembers.ElementAt(nIndexOfFacultyToCheck).GetGender().equalsIgnoreCase("F") == true) {
                fFacultyToCheckGenderMale = false;
            }

            // make sure same faculty is not already assigned a classrom in the [row,column] slot
            for (int j = 0; j < ClassRooms.GetCount() && j <= row; j++) {
                for (k = 0; k < FullSchedule[j][column].GetAssignedFacultyName().size(); k++) {
                    if (FullSchedule[j][column].GetAssignedFacultyName().elementAt(k).toString().equals(FacultyToCheck) == true) {
                        return false;
                    }
                }
            }

            for (k = 0; k < FullSchedule[row][column].GetAssignedFacultyName().size(); k++) {
                if (FullSchedule[row][column].GetAssignedFacultyName().elementAt(k).toString().equals(FacultyToCheck) == true) {
                    break;
                }

                nIndexOtherFacultyAssignedToClassroom = FacultyMembers.GetFacultyIndexUsingName(FullSchedule[row][column].GetAssignedFacultyName().elementAt(k).toString());

                if (FacultyMembers.ElementAt(nIndexOtherFacultyAssignedToClassroom).GetGender().equalsIgnoreCase("F") == true) {
                    fOtherFacultyAssignedToClassroomGenderMale = false;
                } else {
                    fOtherFacultyAssignedToClassroomGenderMale = true;
                }

                if (fFacultyToCheckGenderMale == false && fOtherFacultyAssignedToClassroomGenderMale == false) {
                    break;
                }
            }

            if (k != FullSchedule[row][column].GetAssignedFacultyName().size()) {
                return false;
            }
        } catch (Exception e) {
            statusMessageLabel.setText("            Error5:       " + e);
            PrintNewLineToConsole("Error: " + e);
        }

        return true;
    }

    public void DisplayEmptySchedule() {
        for (int i = 0; i < nNumberOfTimeSlots; i++) {
            jFullScheduleTable.getColumnModel().getColumn(i + 1).setCellRenderer(new FullTableTextAreaRenderer(i));
            jFullScheduleTable.getColumnModel().getColumn(i + 1).setHeaderRenderer(new TableHeaderRenderer());
        }

        for (int i = 0; i < nNumberOfWorkingDaysPerWeek; i++) {
            jDegreeBatchScheduleTable.getColumnModel().getColumn(i + 1).setCellRenderer(new MiniTableTextAreaRenderer(i));
            jDegreeBatchScheduleTable.getColumnModel().getColumn(i + 1).setHeaderRenderer(new TableHeaderRenderer());
        }

        for (int i = 0; i < nNumberOfWorkingDaysPerWeek; i++) {
            jFacultyScheduleTable.getColumnModel().getColumn(i + 1).setCellRenderer(new MiniTableTextAreaRenderer(i));
            jFacultyScheduleTable.getColumnModel().getColumn(i + 1).setHeaderRenderer(new TableHeaderRenderer());
        }

        for (int i = 0; i < nNumberOfWorkingDaysPerWeek; i++) {
            jClassroomScheduleTable.getColumnModel().getColumn(i + 1).setCellRenderer(new MiniTableTextAreaRenderer(i));
            jClassroomScheduleTable.getColumnModel().getColumn(i + 1).setHeaderRenderer(new TableHeaderRenderer());
        }

        for (int i = 0; i < nNumberOfWorkingDaysPerWeek; i++) {
            jCourseScheduleTable.getColumnModel().getColumn(i + 1).setCellRenderer(new MiniTableTextAreaRenderer(i));
            jCourseScheduleTable.getColumnModel().getColumn(i + 1).setHeaderRenderer(new TableHeaderRenderer());
        }
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = ExamSchedulerApp.getApplication().getMainFrame();
            aboutBox = new ExamSchedulerAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        ExamSchedulerApp.getApplication().show(aboutBox);
    }

    @Action
    public void ToggleExpandedAndContractedViews() {
        if (fToggleExpandedView) {
            jFullScheduleTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        } else {
            jFullScheduleTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        }

        fToggleExpandedView = !fToggleExpandedView;
    }

    @Action
    public void DisplayFilledSchedule() {
        DisplayFullSchedule();

        DisplayDegreeBatchSchedule();

        DisplayFacultySchedule();

        DisplayClassroomSchedule();

        DisplayCourseSchedule();

        fToggleDetailedView = !fToggleDetailedView;
    }

    public void DisplayFullSchedule() {
        int columnWidth = 200;

        if (fToggleDetailedView == true) {
            columnWidth = (int) (columnWidth * 1.3);
        }

        // Display Full Schedule
        for (int i = 0; i < nNumberOfTimeSlots; i++) {
            jFullScheduleTable.getColumnModel().getColumn(i + 1).setCellRenderer(new FullTableTextAreaRenderer(i));
            jFullScheduleTable.getColumnModel().getColumn(i + 1).setHeaderRenderer(new TableHeaderRenderer());

            if (fToggleExpandedView == false) {
                jFullScheduleTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                jFullScheduleTable.getColumnModel().getColumn(i + 1).setPreferredWidth(columnWidth);
            }

            for (int j = 0; j < ClassRooms.GetCount(); j++) {
                String DegreeBatch_Course;

                DegreeBatch_Course = "";
                for (int k = 0; k < FullSchedule[j][i].GetAssignedCourseName().size(); k++) {
                    DegreeBatch_Course += FullSchedule[j][i].GetAssignedDegreeBatch().elementAt(k).toString() + " -- " + FullSchedule[j][i].GetAssignedCourseName().elementAt(k).toString() + "(" + FullSchedule[j][i].GetNumberOfAssignedStudents().elementAt(k).toString() + ")\n";
                }

                if (fToggleDetailedView == true) {
                    jFullScheduleTable.getModel().setValueAt(DegreeBatch_Course, j, i + 1);
                } else {
                    jFullScheduleTable.getModel().setValueAt(DegreeBatch_Course, j, i + 1);
                }
            }
        }
    }

    public void ResetDegreeBatchSchedule() {
        for (int j = 0; j < nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek; j++) {
            //jSectionScheduleTable.getColumnModel().getColumn(j+1).setPreferredWidth( 150 );

            for (int i = 0; i < nNumberOfWorkingDaysPerWeek; i++) {
                //DegreeBatchSchedule[j][i].Reset();
                jDegreeBatchScheduleTable.setValueAt("", j, i + 1);
            }
        }
    }

    public void DisplayDegreeBatchSchedule() {
        String selectedDegreeBatch = jDegreeBatchComboBox.getSelectedItem().toString();
        String DegreeBatch_Course;
        int columnWidth = 180;

        if (fToggleDetailedView == true) {
            columnWidth = (int) (columnWidth * 1.3);
        }

        jDegreeBatchScheduleTable.setRowHeight(200);

        for (int i = 0; i < nNumberOfWorkingDaysPerWeek; i++) {
            jDegreeBatchScheduleTable.getColumnModel().getColumn(i + 1).setCellRenderer(new MiniTableTextAreaRenderer(i));
            jDegreeBatchScheduleTable.getColumnModel().getColumn(i + 1).setHeaderRenderer(new TableHeaderRenderer());

            if (fToggleExpandedView == false) {
                jDegreeBatchScheduleTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                jDegreeBatchScheduleTable.getColumnModel().getColumn(i + 1).setPreferredWidth(columnWidth);
            }
        }

        if (selectedDegreeBatch.equals("(All)")) {
            for (int j = 0; j < nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek; j++) {
                for (int i = 0; i < nNumberOfWorkingDaysPerWeek; i++) {
                    DegreeBatch_Course = "";
                    for (int k = 0; k < CourseSchedule[j][i].GetAssignedCourseName().size(); k++) {
                        DegreeBatch_Course += CourseSchedule[j][i].GetAssignedDegreeBatch().elementAt(k).toString() + " -- " + CourseSchedule[j][i].GetAssignedCourseName().elementAt(k).toString() + "\n";
                    }
                    if (fToggleDetailedView == true) {
                        jDegreeBatchScheduleTable.getModel().setValueAt(DegreeBatch_Course, j, i + 1);
                    } else {
                        jDegreeBatchScheduleTable.getModel().setValueAt(DegreeBatch_Course, j, i + 1);
                    }
                }
            }
        } else {
            for (int i = 0; i < nNumberOfTimeSlots; i++) {
                DegreeBatch_Course = "";

                for (int j = 0; j < ClassRooms.GetCount(); j++) {
                    for (int k = 0; k < FullSchedule[j][i].GetAssignedDegreeBatch().size(); k++) {
                        if (FullSchedule[j][i].GetAssignedDegreeBatch().elementAt(k).toString().equals(selectedDegreeBatch) == true) {
                            String RegistrationNosRange = "";
                            String Location = "";

                            Location = jFullScheduleTable.getValueAt(j, 0).toString();
                            RegistrationNosRange = FullSchedule[j][i].GetAssignedStudentRegNos().size() > k ? FullSchedule[j][i].GetAssignedStudentRegNos().elementAt(k).toString() : "";

                            DegreeBatch_Course += Location + " : " + RegistrationNosRange + "\n";
                        }
                    }
                }

                jDegreeBatchScheduleTable.getModel().setValueAt(DegreeBatch_Course, i % (nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek), i / (nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek) + 1);
            }
        }
    }

    public void ResetClassroomSchedule() {
        for (int j = 0; j < nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek; j++) {
            //jClassroomScheduleTable.getColumnModel().getColumn(j+1).setPreferredWidth( 150 );

            for (int i = 0; i < nNumberOfWorkingDaysPerWeek; i++) {
                //ClasroomSchedule[j][i].Reset();
                jClassroomScheduleTable.setValueAt("", j, i + 1);
            }
        }
    }

    public void DisplayClassroomSchedule() {
        String selectedClassroom = jClassroomComboBox.getSelectedItem().toString();
        String DegreeBatch_Course;
        int columnWidth = 180;

        if (fToggleDetailedView == true) {
            columnWidth = (int) (columnWidth * 1.3);
        }

        jDegreeBatchScheduleTable.setRowHeight(150);

        for (int i = 0; i < nNumberOfWorkingDaysPerWeek; i++) {
            jClassroomScheduleTable.getColumnModel().getColumn(i + 1).setCellRenderer(new MiniTableTextAreaRenderer(i));
            jClassroomScheduleTable.getColumnModel().getColumn(i + 1).setHeaderRenderer(new TableHeaderRenderer());

            if (fToggleExpandedView == false) {
                jClassroomScheduleTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                jClassroomScheduleTable.getColumnModel().getColumn(i + 1).setPreferredWidth(columnWidth);
            }
        }

        if (selectedClassroom.equals("(All)")) {
            for (int i = 0; i < nNumberOfWorkingDaysPerWeek; i++) {
                for (int j = 0; j < nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek; j++) {
                    DegreeBatch_Course = "";
                    for (int l = 0; l < ClassRooms.GetCount(); l++) {
                        for (int k = 0; k < FullSchedule[l][i * (nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek) + j].GetAssignedCourseName().size(); k++) {
                            DegreeBatch_Course += FullSchedule[l][i * (nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek) + j].GetAssignedDegreeBatch().elementAt(k).toString() + " -- " + FullSchedule[l][i * (nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek) + j].GetAssignedCourseName().elementAt(k).toString() + "(" + FullSchedule[l][i * (nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek) + j].GetNumberOfAssignedStudents().elementAt(k).toString() + ")\n";
                        }
                    }

                    if (fToggleDetailedView == true) {
                        jClassroomScheduleTable.getModel().setValueAt(DegreeBatch_Course, j, i + 1);
                    } else {
                        jClassroomScheduleTable.getModel().setValueAt(DegreeBatch_Course, j, i + 1);
                    }
                }
            }
        } else if( selectedClassroom.equals("<Select One>")) {
            // ignore
        }else {
            int j = ClassRooms.GetClassRoomIndex(selectedClassroom);

            for (int i = 0; i < nNumberOfTimeSlots; i++) {
                DegreeBatch_Course = "";

                for (int k = 0; k < FullSchedule[j][i].GetAssignedDegreeBatch().size(); k++) {
                    String RegistrationNosRange = "";

                    RegistrationNosRange = FullSchedule[j][i].GetAssignedStudentRegNos().size() > k ? FullSchedule[j][i].GetAssignedStudentRegNos().elementAt(k).toString() : "";

                    DegreeBatch_Course += FullSchedule[j][i].GetAssignedDegreeBatch().elementAt(k).toString() + " -- " + FullSchedule[j][i].GetAssignedCourseName().elementAt(k).toString() + "\n";

                    DegreeBatch_Course += "  Reg No: " + RegistrationNosRange + "\n";

                    if (k == 0) {
                        DegreeBatch_Course += "\n";
                    }
                }

                jClassroomScheduleTable.getModel().setValueAt(DegreeBatch_Course, i % (nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek), i / (nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek) + 1);
            }
        }

    }

    public void ResetFacultySchedule() {
        for (int j = 0; j < nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek; j++) {
            //jFacultyScheduleTable.getColumnModel().getColumn(j+1).setPreferredWidth( 150 );

            for (int i = 0; i < nNumberOfWorkingDaysPerWeek; i++) {
                //FacultySchedule[j][i].Reset();
                jFacultyScheduleTable.setValueAt("", j, i + 1);
            }
        }
    }

    public void DisplayFacultySchedule() {
        String selectedFaculty = jFacultyComboBox.getSelectedItem().toString();
        int columnWidth = 150;

        if (fToggleDetailedView == true) {
            columnWidth = (int) (columnWidth * 1.3);
        }


        for (int i = 0; i < nNumberOfWorkingDaysPerWeek; i++) {
            jFacultyScheduleTable.getColumnModel().getColumn(i + 1).setCellRenderer(new MiniTableTextAreaRenderer(i));
            jFacultyScheduleTable.getColumnModel().getColumn(i + 1).setHeaderRenderer(new TableHeaderRenderer());

            if (fToggleExpandedView == false) {
                jFacultyScheduleTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                jFacultyScheduleTable.getColumnModel().getColumn(i + 1).setPreferredWidth(columnWidth);
            }
        }

        for (int i = 0; i < nNumberOfWorkingDaysPerWeek; i++) {
            for (int j = 0; j < nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek; j++) {
                String AssignedFaculty;

                AssignedFaculty = "";
                for (int l = 0; l < ClassRooms.GetCount(); l++) {
                    for (int k = 0; k < FullSchedule[l][i * (nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek) + j].GetAssignedFacultyName().size(); k++) {
                        if (selectedFaculty.equals("(All)")) {
                            AssignedFaculty += FullSchedule[l][i * (nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek) + j].GetAssignedFacultyName().elementAt(k).toString() + " -- " + ClassRooms.ElementAt(l).ClassroomName + "\n";
                        } else if (FullSchedule[l][i * (nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek) + j].GetAssignedFacultyName().elementAt(k).toString().equals(selectedFaculty) == true) {
                            AssignedFaculty += ClassRooms.ElementAt(l).ClassroomName + "\n";
                        }
                    }
                }

                if (fToggleDetailedView == true) {
                    jFacultyScheduleTable.getModel().setValueAt(AssignedFaculty, j, i + 1);
                } else {
                    jFacultyScheduleTable.getModel().setValueAt(AssignedFaculty, j, i + 1);
                }
            }
        }
    }

    public void ResetCourseSchedule() {
        for (int j = 0; j < nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek; j++) {
            //jCourseScheduleTable.getColumnModel().getColumn(j+1).setPreferredWidth( 150 );

            for (int i = 0; i < nNumberOfWorkingDaysPerWeek; i++) {
                //CourseSchedule[j][i].Reset();
                jCourseScheduleTable.setValueAt("", j, i + 1);
            }
        }
    }

    public void DisplayCourseSchedule() {
        String selectedCourse = jCourseComboBox.getSelectedItem().toString();
        int columnWidth = 250;

        if (fToggleDetailedView == true) {
            columnWidth = (int) (columnWidth * 1.3);
        }


        for (int i = 0; i < nNumberOfWorkingDaysPerWeek; i++) {
            jCourseScheduleTable.getColumnModel().getColumn(i + 1).setCellRenderer(new MiniTableTextAreaRenderer(i));
            jCourseScheduleTable.getColumnModel().getColumn(i + 1).setHeaderRenderer(new TableHeaderRenderer());

            if (fToggleExpandedView == false) {
                jCourseScheduleTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                jCourseScheduleTable.getColumnModel().getColumn(i + 1).setPreferredWidth(columnWidth);
            }
        }

        for (int j = 0; j < nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek; j++) {
            for (int i = 0; i < nNumberOfWorkingDaysPerWeek; i++) {
                String DegreeBatch_Course;

                DegreeBatch_Course = "";
                for (int k = 0; k < CourseSchedule[j][i].GetAssignedCourseName().size(); k++) {
                    if (selectedCourse.equals("(All)") || CourseSchedule[j][i].GetAssignedCourseName().elementAt(k).toString().equals(selectedCourse) == true) {
                        DegreeBatch_Course += CourseSchedule[j][i].GetAssignedDegreeBatch().elementAt(k).toString() + " -- " + CourseSchedule[j][i].GetAssignedCourseName().elementAt(k).toString() + "\n";
                    }
                }

                if (fToggleDetailedView == true) {
                    jCourseScheduleTable.getModel().setValueAt(DegreeBatch_Course, j, i + 1);
                } else {
                    jCourseScheduleTable.getModel().setValueAt(DegreeBatch_Course, j, i + 1);
                }
            }
        }
    }

    public int Print_DegreeBatch_Classroom_Faculty_Schedule(Graphics2D g, int nPage, PrintService selectedPrinter) {
        try {
            HashPrintRequestAttributeSet printAttrib = new HashPrintRequestAttributeSet();
            String selectedFacultyMemberName = "";
            int selectedFacultyMemberIndex = 0;

            // print degree batch schedule
            if (jDegreeBatchComboBox.getSelectedItem().toString().equals("(All)") == true || jDegreeBatchComboBox.getSelectedItem().toString().equals("<Select One>") == true) {
                for (int i = 0; i < jDegreeBatchComboBox.getItemCount(); i++) {
                    jDegreeBatchComboBox.setSelectedIndex(i);

                    if (jDegreeBatchComboBox.getSelectedItem().toString().equals("<Select One>") == true || jDegreeBatchComboBox.getSelectedItem().toString().equals("(All)") == true) {
                        continue;
                    }

                    MessageFormat headerFormat = new MessageFormat("Seating Plan (OHT-2):   " + jDegreeBatchComboBox.getSelectedItem().toString());

                    printAttrib.add(OrientationRequested.LANDSCAPE);

                    printAttrib.add(new JobName("Seating Plan (OHT-2):   " + jDegreeBatchComboBox.getSelectedItem().toString(), Locale.getDefault()));
                    jDegreeBatchScheduleTable.print(JTable.PrintMode.FIT_WIDTH, headerFormat, null, false, printAttrib, false, selectedPrinter);
                }
            } else {
                MessageFormat headerFormat = new MessageFormat("Seating Plan (OHT-2):   " + jDegreeBatchComboBox.getSelectedItem().toString());

                printAttrib.add(OrientationRequested.LANDSCAPE);

                printAttrib.add(new JobName("Seating Plan (OHT-2):   " + jDegreeBatchComboBox.getSelectedItem().toString(), Locale.getDefault()));
                jDegreeBatchScheduleTable.print(JTable.PrintMode.FIT_WIDTH, headerFormat, null, false, printAttrib, false, selectedPrinter);
            }

            // print classroom schedule
            if (jClassroomComboBox.getSelectedItem().toString().equals("(All)") == true || jClassroomComboBox.getSelectedItem().toString().equals("<Select One>") == true) {
                for (int i = 0; i < jClassroomComboBox.getItemCount(); i++) {
                    jClassroomComboBox.setSelectedIndex(i);

                    if (jClassroomComboBox.getSelectedItem().toString().equals("<Select One>") == true || jClassroomComboBox.getSelectedItem().toString().equals("(All)") == true) {
                        continue;
                    }

                    MessageFormat headerFormat = new MessageFormat("Seating Plan (OHT-2):   " + jClassroomComboBox.getSelectedItem().toString());

                    printAttrib.add(OrientationRequested.LANDSCAPE);

                    printAttrib.add(new JobName("Seating Plan (OHT-2):   " + jClassroomComboBox.getSelectedItem().toString(), Locale.getDefault()));
                    jClassroomScheduleTable.print(JTable.PrintMode.FIT_WIDTH, headerFormat, null, false, printAttrib, false, selectedPrinter);
                }
            } else {
                MessageFormat headerFormat = new MessageFormat("Seating Plan (OHT-2):   " + jClassroomComboBox.getSelectedItem().toString());

                printAttrib.add(OrientationRequested.LANDSCAPE);

                printAttrib.add(new JobName("Seating Plan (OHT-2):   " + jClassroomComboBox.getSelectedItem().toString(), Locale.getDefault()));
                jClassroomScheduleTable.print(JTable.PrintMode.FIT_WIDTH, headerFormat, null, false, printAttrib, false, selectedPrinter);
            }

            // print faculty schedule
            if (jFacultyComboBox.getSelectedItem().toString().equals("(All)") == true || jFacultyComboBox.getSelectedItem().toString().equals("<Select One>") == true) {
                for (int i = 0; i < jFacultyComboBox.getItemCount(); i++) {
                    jFacultyComboBox.setSelectedIndex(i);

                    selectedFacultyMemberName = jFacultyComboBox.getSelectedItem().toString();

                    if (selectedFacultyMemberName.equals("<Select One>") == true || selectedFacultyMemberName.equals("(All)") == true) {
                        continue;
                    }

                    selectedFacultyMemberIndex = FacultyMembers.GetFacultyIndexUsingName(selectedFacultyMemberName);


                    MessageFormat headerFormat = new MessageFormat("Timetable (OHT-2):   " + selectedFacultyMemberName + " (" + FacultyMembers.ElementAt(selectedFacultyMemberIndex).GetFacultyDepartment() + ")");

                    printAttrib.add(OrientationRequested.LANDSCAPE);

                    printAttrib.add(new JobName("Timetable (OHT-2):   " + selectedFacultyMemberName + " (" + FacultyMembers.ElementAt(selectedFacultyMemberIndex).GetFacultyDepartment() + ")", Locale.getDefault()));
                    jFacultyScheduleTable.print(JTable.PrintMode.FIT_WIDTH, headerFormat, null, false, printAttrib, false, selectedPrinter);
                }
            } else {
                
                selectedFacultyMemberName = jFacultyComboBox.getSelectedItem().toString();
                 
                MessageFormat headerFormat = new MessageFormat("Timetable (OHT-2):   " + selectedFacultyMemberName + " (" + FacultyMembers.ElementAt(selectedFacultyMemberIndex).GetFacultyDepartment() + ")");

                printAttrib.add(OrientationRequested.LANDSCAPE);

                printAttrib.add(new JobName("Timetable (OHT-2):   " + selectedFacultyMemberName + " (" + FacultyMembers.ElementAt(selectedFacultyMemberIndex).GetFacultyDepartment() + ")", Locale.getDefault()));
                jFacultyScheduleTable.print(JTable.PrintMode.FIT_WIDTH, headerFormat, null, false, printAttrib, false, selectedPrinter);
            }              

        } catch (Exception e) {
            if (e.getClass() == java.lang.NullPointerException.class) {
                System.err.println("Error printing: NullPoinerException");
            } else if (e.getClass() == javax.print.attribute.UnmodifiableSetException.class) {
                System.err.println("Error printing: UnmodifiableSetException");
            } else {
                System.err.println("Error printing: " + e.getMessage());
            }
        }

        return Printable.NO_SUCH_PAGE;
    }

    void OutputSchedulingReport() {
        if (DEBUG == true) {
            PrintNewLineToConsole("\nFinal Scheduling Summary:\n ");
        } else {
            System.out.println("\nFinal Scheduling Summary:\n ");
        }

        for (int p = 0; p < FacultyMembers.GetCount(); p++) {
            for (int q = 0; q < FacultyMembers.ElementAt(p).CoursesInstructing.size(); q++) {
                CourseInfo courseInfo = (CourseInfo) FacultyMembers.ElementAt(p).CoursesInstructingInfo.elementAt(q);

                if (courseInfo.nClassSlotsFilled < courseInfo.nClassCreditHours) {
                    if (DEBUG == true) {
                        JOptionPane.showMessageDialog(null,"\nCould not Schedule " + (courseInfo.nClassCreditHours - courseInfo.nClassSlotsFilled) + " class slots for " + FacultyMembers.ElementAt(p).Name + " for " + courseInfo.Name);
                    } else {
                        System.out.println("\nCould not Schedule " + (courseInfo.nClassCreditHours - courseInfo.nClassSlotsFilled) + " class slots for " + FacultyMembers.ElementAt(p).Name + " for " + courseInfo.Name);
                    }
                }

                if (courseInfo.nLabSlotsFilled < courseInfo.nLabContactHours) {
                    if (DEBUG == true) {
                        JOptionPane.showMessageDialog(null,"\nCould not Schedule " + (courseInfo.nLabContactHours - courseInfo.nLabSlotsFilled) + " lab slots for " + FacultyMembers.ElementAt(p).Name + " for " + courseInfo.Name);
                    } else {
                        System.out.println("\nCould not Schedule " + (courseInfo.nLabContactHours - courseInfo.nLabSlotsFilled) + " lab slots for " + FacultyMembers.ElementAt(p).Name + " for " + courseInfo.Name);
                    }
                }
            }
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jTabPane = new javax.swing.JTabbedPane();
        jScrollPaneFullSchedule = new javax.swing.JScrollPane();
        jFullScheduleTable = new javax.swing.JTable();
        jScrollPaneCourseSchedule = new javax.swing.JScrollPane();
        jCourseScheduleTable = new javax.swing.JTable();
        jScrollPaneFacultySchedule = new javax.swing.JScrollPane();
        jFacultyScheduleTable = new javax.swing.JTable();
        jScrollPaneDegreeBatchSchedule = new javax.swing.JScrollPane();
        jDegreeBatchScheduleTable = new javax.swing.JTable();
        jScrollPaneClassroomSchedule = new javax.swing.JScrollPane();
        jClassroomScheduleTable = new javax.swing.JTable();
        jDegreeBatchLabel1 = new javax.swing.JLabel();
        jFacultyLabel1 = new javax.swing.JLabel();
        jClassroomLabel1 = new javax.swing.JLabel();
        jCourseLabel1 = new javax.swing.JLabel();
        jStudentLabel = new javax.swing.JLabel();
        jDegreeBatchCountLabel = new javax.swing.JLabel();
        jFacultyCountLabel = new javax.swing.JLabel();
        jClassroomCountLabel = new javax.swing.JLabel();
        jCourseCountLabel = new javax.swing.JLabel();
        jStudentCountLabel = new javax.swing.JLabel();
        jRunScheduleButton = new javax.swing.JButton();
        jSaveScheduleInDatabase = new javax.swing.JButton();
        jPrintSchedule = new javax.swing.JButton();
        jCourseLabel = new javax.swing.JLabel();
        jFacultyLabel = new javax.swing.JLabel();
        jDegreeBatchLabel = new javax.swing.JLabel();
        jClassroomLabel = new javax.swing.JLabel();
        jCourseComboBox = new javax.swing.JComboBox();
        jFacultyComboBox = new javax.swing.JComboBox();
        jDegreeBatchComboBox = new javax.swing.JComboBox();
        jClassroomComboBox = new javax.swing.JComboBox();
        jDatabaseEntry = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jSaveSchedule = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        jMenuItemRunScheduler = new javax.swing.JMenuItem();
        jMenuItemExpandView = new javax.swing.JMenuItem();
        jMenuItemDetailedView = new javax.swing.JMenuItem();
        jMenuItemPrintSchedule = new javax.swing.JMenuItem();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ExamScheduler.ExamSchedulerApp.class).getContext().getResourceMap(ExamSchedulerView.class);
        mainPanel.setBackground(resourceMap.getColor("mainPanel.background")); // NOI18N
        mainPanel.setMaximumSize(new java.awt.Dimension(2000, 2000));
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(2000, 2000));
        mainPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTabPane.setMaximumSize(new java.awt.Dimension(2000, 2000));
        jTabPane.setName("jTabPane"); // NOI18N
        jTabPane.setPreferredSize(new java.awt.Dimension(405, 300));

        jScrollPaneFullSchedule.setMaximumSize(new java.awt.Dimension(2000, 2000));
        jScrollPaneFullSchedule.setName("jScrollPaneFullSchedule"); // NOI18N
        jScrollPaneFullSchedule.setPreferredSize(new java.awt.Dimension(400, 300));

        jFullScheduleTable.setBackground(resourceMap.getColor("jFullScheduleTable.background")); // NOI18N
        jFullScheduleTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jFullScheduleTable.setFont(resourceMap.getFont("jFullScheduleTable.font")); // NOI18N
        jFullScheduleTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object[0][0] ,
            new String[0] ));
    jFullScheduleTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
    jFullScheduleTable.setCellSelectionEnabled(true);
    jFullScheduleTable.setDragEnabled(true);
    jFullScheduleTable.setName("jFullScheduleTable"); // NOI18N
    jFullScheduleTable.setRowHeight(46);
    jFullScheduleTable.setSelectionBackground(resourceMap.getColor("jFullScheduleTable.selectionBackground")); // NOI18N
    jFullScheduleTable.getTableHeader().setReorderingAllowed(false);
    jScrollPaneFullSchedule.setViewportView(jFullScheduleTable);

    jTabPane.addTab(resourceMap.getString("jScrollPaneFullSchedule.TabConstraints.tabTitle"), jScrollPaneFullSchedule); // NOI18N

    jScrollPaneCourseSchedule.setMaximumSize(new java.awt.Dimension(2000, 2000));
    jScrollPaneCourseSchedule.setName("jScrollPaneCourseSchedule"); // NOI18N
    jScrollPaneCourseSchedule.setPreferredSize(new java.awt.Dimension(400, 300));

    jCourseScheduleTable.setBackground(resourceMap.getColor("jCourseScheduleTable.background")); // NOI18N
    jCourseScheduleTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    jCourseScheduleTable.setModel(new javax.swing.table.DefaultTableModel(
        new Object[0][0] ,
        new String[0] ));
jCourseScheduleTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
jCourseScheduleTable.setCellSelectionEnabled(true);
jCourseScheduleTable.setDragEnabled(true);
jCourseScheduleTable.setName("jCourseScheduleTable"); // NOI18N
jCourseScheduleTable.setRequestFocusEnabled(false);
jCourseScheduleTable.setRowHeight(100);
jCourseScheduleTable.setSelectionBackground(resourceMap.getColor("jCourseScheduleTable.selectionBackground")); // NOI18N
jCourseScheduleTable.getTableHeader().setReorderingAllowed(false);
jScrollPaneCourseSchedule.setViewportView(jCourseScheduleTable);

jTabPane.addTab(resourceMap.getString("jScrollPaneCourseSchedule.TabConstraints.tabTitle"), jScrollPaneCourseSchedule); // NOI18N

jScrollPaneFacultySchedule.setMaximumSize(new java.awt.Dimension(2000, 2000));
jScrollPaneFacultySchedule.setName("jScrollPaneFacultySchedule"); // NOI18N
jScrollPaneFacultySchedule.setPreferredSize(new java.awt.Dimension(400, 300));

jFacultyScheduleTable.setBackground(resourceMap.getColor("jFacultyScheduleTable.background")); // NOI18N
jFacultyScheduleTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
jFacultyScheduleTable.setModel(new javax.swing.table.DefaultTableModel(
    new Object[0][0] ,
    new String[0] ));
    jFacultyScheduleTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
    jFacultyScheduleTable.setCellSelectionEnabled(true);
    jFacultyScheduleTable.setDragEnabled(true);
    jFacultyScheduleTable.setName("jFacultyScheduleTable"); // NOI18N
    jFacultyScheduleTable.setRequestFocusEnabled(false);
    jFacultyScheduleTable.setRowHeight(100);
    jFacultyScheduleTable.setSelectionBackground(resourceMap.getColor("jFacultyScheduleTable.selectionBackground")); // NOI18N
    jFacultyScheduleTable.getTableHeader().setReorderingAllowed(false);
    jScrollPaneFacultySchedule.setViewportView(jFacultyScheduleTable);

    jTabPane.addTab(resourceMap.getString("jScrollPaneFacultySchedule.TabConstraints.tabTitle"), jScrollPaneFacultySchedule); // NOI18N

    jScrollPaneDegreeBatchSchedule.setMaximumSize(new java.awt.Dimension(2000, 2000));
    jScrollPaneDegreeBatchSchedule.setName("jScrollPaneDegreeBatchSchedule"); // NOI18N
    jScrollPaneDegreeBatchSchedule.setPreferredSize(new java.awt.Dimension(400, 300));

    jDegreeBatchScheduleTable.setBackground(resourceMap.getColor("jDegreeBatchScheduleTable.background")); // NOI18N
    jDegreeBatchScheduleTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    jDegreeBatchScheduleTable.setModel(new javax.swing.table.DefaultTableModel(
        new Object[0][0] ,
        new String[0] ));
jDegreeBatchScheduleTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
jDegreeBatchScheduleTable.setCellSelectionEnabled(true);
jDegreeBatchScheduleTable.setDragEnabled(true);
jDegreeBatchScheduleTable.setName("jDegreeBatchScheduleTable"); // NOI18N
jDegreeBatchScheduleTable.setRequestFocusEnabled(false);
jDegreeBatchScheduleTable.setRowHeight(100);
jDegreeBatchScheduleTable.setSelectionBackground(resourceMap.getColor("jDegreeBatchScheduleTable.selectionBackground")); // NOI18N
jDegreeBatchScheduleTable.getTableHeader().setReorderingAllowed(false);
jScrollPaneDegreeBatchSchedule.setViewportView(jDegreeBatchScheduleTable);

jTabPane.addTab(resourceMap.getString("jScrollPaneDegreeBatchSchedule.TabConstraints.tabTitle"), jScrollPaneDegreeBatchSchedule); // NOI18N

jScrollPaneClassroomSchedule.setMaximumSize(new java.awt.Dimension(2000, 2000));
jScrollPaneClassroomSchedule.setName("jScrollPaneClassroomSchedule"); // NOI18N
jScrollPaneClassroomSchedule.setPreferredSize(new java.awt.Dimension(400, 300));

jClassroomScheduleTable.setBackground(resourceMap.getColor("jClassroomScheduleTable.background")); // NOI18N
jClassroomScheduleTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
jClassroomScheduleTable.setModel(new javax.swing.table.DefaultTableModel(
    new Object[0][0] ,
    new String[0] ));
    jClassroomScheduleTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
    jClassroomScheduleTable.setCellSelectionEnabled(true);
    jClassroomScheduleTable.setDragEnabled(true);
    jClassroomScheduleTable.setName("jClassroomScheduleTable"); // NOI18N
    jClassroomScheduleTable.setRequestFocusEnabled(false);
    jClassroomScheduleTable.setRowHeight(100);
    jClassroomScheduleTable.setSelectionBackground(resourceMap.getColor("jClassroomScheduleTable.selectionBackground")); // NOI18N
    jClassroomScheduleTable.getTableHeader().setReorderingAllowed(false);
    jScrollPaneClassroomSchedule.setViewportView(jClassroomScheduleTable);

    jTabPane.addTab(resourceMap.getString("jScrollPaneClassroomSchedule.TabConstraints.tabTitle"), jScrollPaneClassroomSchedule); // NOI18N

    mainPanel.add(jTabPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 150, 1270, 550));

    jDegreeBatchLabel1.setText(resourceMap.getString("jDegreeBatchLabel1.text")); // NOI18N
    jDegreeBatchLabel1.setName("jDegreeBatchLabel1"); // NOI18N
    mainPanel.add(jDegreeBatchLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 6, -1, -1));
    jDegreeBatchLabel1.getAccessibleContext().setAccessibleName(resourceMap.getString("jDegreeBatchLabel1.AccessibleContext.accessibleName")); // NOI18N

    jFacultyLabel1.setText(resourceMap.getString("jFacultyLabel1.text")); // NOI18N
    jFacultyLabel1.setName("jFacultyLabel1"); // NOI18N
    mainPanel.add(jFacultyLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 29, 72, 15));

    jClassroomLabel1.setText(resourceMap.getString("jClassroomLabel1.text")); // NOI18N
    jClassroomLabel1.setName("jClassroomLabel1"); // NOI18N
    mainPanel.add(jClassroomLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 50, 72, 17));

    jCourseLabel1.setText(resourceMap.getString("jCourseLabel1.text")); // NOI18N
    jCourseLabel1.setName("jCourseLabel1"); // NOI18N
    mainPanel.add(jCourseLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 74, 72, -1));

    jStudentLabel.setText(resourceMap.getString("jStudentLabel.text")); // NOI18N
    jStudentLabel.setName("jStudentLabel"); // NOI18N
    mainPanel.add(jStudentLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 97, 72, 14));

    jDegreeBatchCountLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jDegreeBatchCountLabel.setText(resourceMap.getString("jDegreeBatchCountLabel.text")); // NOI18N
    jDegreeBatchCountLabel.setName("jDegreeBatchCountLabel"); // NOI18N
    mainPanel.add(jDegreeBatchCountLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(101, 7, 57, 13));

    jFacultyCountLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jFacultyCountLabel.setText(resourceMap.getString("jFacultyCountLabel.text")); // NOI18N
    jFacultyCountLabel.setName("jFacultyCountLabel"); // NOI18N
    mainPanel.add(jFacultyCountLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(101, 29, 57, 15));

    jClassroomCountLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jClassroomCountLabel.setText(resourceMap.getString("jClassroomCountLabel.text")); // NOI18N
    jClassroomCountLabel.setName("jClassroomCountLabel"); // NOI18N
    mainPanel.add(jClassroomCountLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(101, 51, 57, 15));

    jCourseCountLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jCourseCountLabel.setText(resourceMap.getString("jCourseCountLabel.text")); // NOI18N
    jCourseCountLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
    jCourseCountLabel.setName("jCourseCountLabel"); // NOI18N
    mainPanel.add(jCourseCountLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(101, 73, 57, 18));

    jStudentCountLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jStudentCountLabel.setText(resourceMap.getString("jStudentCountLabel.text")); // NOI18N
    jStudentCountLabel.setName("jStudentCountLabel"); // NOI18N
    mainPanel.add(jStudentCountLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(101, 97, 57, 14));

    javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(ExamScheduler.ExamSchedulerApp.class).getContext().getActionMap(ExamSchedulerView.class, this);
    jRunScheduleButton.setAction(actionMap.get("RunScheduler")); // NOI18N
    jRunScheduleButton.setText(resourceMap.getString("jRunSchedulerButton.text")); // NOI18N
    jRunScheduleButton.setName("jRunSchedulerButton"); // NOI18N
    mainPanel.add(jRunScheduleButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(223, 6, 290, 48));

    jSaveScheduleInDatabase.setAction(actionMap.get("SaveScheduleInDatabase")); // NOI18N
    jSaveScheduleInDatabase.setText(resourceMap.getString("jSaveScheduleInDatabase.text")); // NOI18N
    jSaveScheduleInDatabase.setName("jSaveScheduleInDatabase"); // NOI18N
    jSaveScheduleInDatabase.setPreferredSize(new java.awt.Dimension(150, 150));
    mainPanel.add(jSaveScheduleInDatabase, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 60, 150, 22));
    jSaveScheduleInDatabase.getAccessibleContext().setAccessibleName(resourceMap.getString("jSaveScheduleInDatabase.AccessibleContext.accessibleName")); // NOI18N

    jPrintSchedule.setText(resourceMap.getString("jPrintSchedule.text")); // NOI18N
    jPrintSchedule.setName("jPrintSchedule"); // NOI18N
    mainPanel.add(jPrintSchedule, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 60, 130, 22));
    jPrintSchedule.addActionListener(new SchedulePrinter());

    jCourseLabel.setText(resourceMap.getString("jCourseLabel.text")); // NOI18N
    jCourseLabel.setAlignmentY(0.0F);
    jCourseLabel.setName("jCourseLabel"); // NOI18N
    mainPanel.add(jCourseLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(662, 10, -1, -1));

    jFacultyLabel.setText(resourceMap.getString("jFacultyLabel.text")); // NOI18N
    jFacultyLabel.setName("jFacultyLabel"); // NOI18N
    mainPanel.add(jFacultyLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(662, 40, -1, -1));

    jDegreeBatchLabel.setText(resourceMap.getString("jDegreeBatchLabel.text")); // NOI18N
    jDegreeBatchLabel.setName("jDegreeBatchLabel"); // NOI18N
    mainPanel.add(jDegreeBatchLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(662, 73, -1, -1));

    jClassroomLabel.setText(resourceMap.getString("jClassroomLabel.text")); // NOI18N
    jClassroomLabel.setName("jClassroomLabel"); // NOI18N
    mainPanel.add(jClassroomLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(662, 106, -1, -1));

    jCourseComboBox.setSelectedItem("Section");
    jCourseComboBox.setAlignmentX(0.0F);
    jCourseComboBox.setAlignmentY(0.0F);
    jCourseComboBox.setName("jCourseComboBox"); // NOI18N
    mainPanel.add(jCourseComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(751, 6, 186, -1));

    jFacultyComboBox.setSelectedItem("Faculty");
    jFacultyComboBox.setName("jFacultyComboBox"); // NOI18N
    mainPanel.add(jFacultyComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(751, 36, 186, -1));

    jDegreeBatchComboBox.setSelectedItem("Section");
    jDegreeBatchComboBox.setName("jDegreeBatchComboBox"); // NOI18N
    mainPanel.add(jDegreeBatchComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(751, 69, 186, -1));

    jClassroomComboBox.setSelectedItem("Classroom");
    jClassroomComboBox.setName("jClassroomComboBox"); // NOI18N
    mainPanel.add(jClassroomComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(751, 102, 186, -1));

    jDatabaseEntry.setText(resourceMap.getString("jDatabaseEntry.text")); // NOI18N
    jDatabaseEntry.setName("jDatabaseEntry"); // NOI18N
    jDatabaseEntry.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jDatabaseEntryActionPerformed(evt);
        }
    });
    mainPanel.add(jDatabaseEntry, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 90, 130, 40));

    jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
    jButton1.setName("jButton1"); // NOI18N
    jButton1.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton1ActionPerformed(evt);
        }
    });
    mainPanel.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, 10, -1, -1));

    jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
    jButton2.setName("jButton2"); // NOI18N
    jButton2.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton2ActionPerformed(evt);
        }
    });
    mainPanel.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, 50, -1, -1));

    jSaveSchedule.setAction(actionMap.get("SaveSchedule")); // NOI18N
    jSaveSchedule.setText(resourceMap.getString("jSaveSchedule.text")); // NOI18N
    jSaveSchedule.setName("jSaveSchedule"); // NOI18N
    jSaveSchedule.setPreferredSize(new java.awt.Dimension(150, 150));
    mainPanel.add(jSaveSchedule, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 90, 150, 40));

    menuBar.setBackground(resourceMap.getColor("menuBar.background")); // NOI18N
    menuBar.setMaximumSize(new java.awt.Dimension(60, 2000));
    menuBar.setName("menuBar"); // NOI18N

    fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
    fileMenu.setMaximumSize(new java.awt.Dimension(40, 2000));
    fileMenu.setName("fileMenu"); // NOI18N
    fileMenu.setPreferredSize(new java.awt.Dimension(45, 21));

    jMenuItemRunScheduler.setAction(actionMap.get("RunScheduler")); // NOI18N
    jMenuItemRunScheduler.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
    jMenuItemRunScheduler.setText(resourceMap.getString("jMenuItemRunScheduler.text")); // NOI18N
    jMenuItemRunScheduler.setName("jMenuItemRunScheduler"); // NOI18N
    jMenuItemRunScheduler.addMenuKeyListener(new javax.swing.event.MenuKeyListener() {
        public void menuKeyPressed(javax.swing.event.MenuKeyEvent evt) {
        }
        public void menuKeyReleased(javax.swing.event.MenuKeyEvent evt) {
        }
        public void menuKeyTyped(javax.swing.event.MenuKeyEvent evt) {
            jMenuItemRunSchedulerOnMenuPressed(evt);
        }
    });
    jMenuItemRunScheduler.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemRunSchedulerActionPerformed(evt);
        }
    });
    fileMenu.add(jMenuItemRunScheduler);

    jMenuItemExpandView.setAction(actionMap.get("ToggleExpandedAndContractedViews")); // NOI18N
    jMenuItemExpandView.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
    jMenuItemExpandView.setText(resourceMap.getString("jMenuItemExpandView.text")); // NOI18N
    jMenuItemExpandView.setName("jMenuItemExpandView"); // NOI18N
    fileMenu.add(jMenuItemExpandView);

    jMenuItemDetailedView.setAction(actionMap.get("DisplayFilledSchedule")); // NOI18N
    jMenuItemDetailedView.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
    jMenuItemDetailedView.setText(resourceMap.getString("jMenuItemDetailedView.text")); // NOI18N
    jMenuItemDetailedView.setName("jMenuItemDetailedView"); // NOI18N
    fileMenu.add(jMenuItemDetailedView);

    jMenuItemPrintSchedule.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
    jMenuItemPrintSchedule.setText(resourceMap.getString("jMenuItemPrintSchedule.text")); // NOI18N
    jMenuItemPrintSchedule.setName("jMenuItemPrintSchedule"); // NOI18N
    jMenuItemPrintSchedule.addActionListener(new SchedulePrinter());
    fileMenu.add(jMenuItemPrintSchedule);

    exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
    exitMenuItem.setName("exitMenuItem"); // NOI18N
    fileMenu.add(exitMenuItem);

    menuBar.add(fileMenu);

    helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
    helpMenu.setMaximumSize(new java.awt.Dimension(40, 2000));
    helpMenu.setName("helpMenu"); // NOI18N
    helpMenu.setPreferredSize(new java.awt.Dimension(60, 21));

    aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
    aboutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
    aboutMenuItem.setName("aboutMenuItem"); // NOI18N
    helpMenu.add(aboutMenuItem);

    menuBar.add(helpMenu);

    statusPanel.setBackground(resourceMap.getColor("statusPanel.background")); // NOI18N
    statusPanel.setMaximumSize(new java.awt.Dimension(2000, 2000));
    statusPanel.setName("statusPanel"); // NOI18N
    statusPanel.setPreferredSize(new java.awt.Dimension(728, 25));

    statusMessageLabel.setText(resourceMap.getString("statusMessageLabel.text")); // NOI18N
    statusMessageLabel.setMaximumSize(new java.awt.Dimension(100, 20));
    statusMessageLabel.setMinimumSize(new java.awt.Dimension(108, 20));
    statusMessageLabel.setName("statusMessageLabel"); // NOI18N

    statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    statusAnimationLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
    statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

    progressBar.setMaximumSize(new java.awt.Dimension(100, 14));
    progressBar.setName("progressBar"); // NOI18N

    javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
    statusPanel.setLayout(statusPanelLayout);
    statusPanelLayout.setHorizontalGroup(
        statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(statusPanelLayout.createSequentialGroup()
            .addContainerGap()
            .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(statusPanelLayout.createSequentialGroup()
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(1271, 1271, 1271))
                .addComponent(statusMessageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 1425, Short.MAX_VALUE))
            .addGap(31336, 31336, 31336))
        .addGroup(statusPanelLayout.createSequentialGroup()
            .addGap(1449, 1449, 1449)
            .addComponent(statusAnimationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    statusPanelLayout.setVerticalGroup(
        statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(statusPanelLayout.createSequentialGroup()
            .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(statusPanelLayout.createSequentialGroup()
                    .addComponent(statusMessageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusPanelLayout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(statusAnimationLabel)))
            .addContainerGap())
    );

    setComponent(mainPanel);
    setMenuBar(menuBar);
    setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents
    private void OnMenuSelected(javax.swing.event.MenuKeyEvent evt) {

    }

    
    private void jDatabaseEntryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDatabaseEntryActionPerformed
        // TODO add your handling code here:]
        DataEntry a = new DataEntry();
        a.main_entry();
    }//GEN-LAST:event_jDatabaseEntryActionPerformed

    private void jMenuItemRunSchedulerOnMenuPressed(javax.swing.event.MenuKeyEvent evt) {//GEN-FIRST:event_jMenuItemRunSchedulerOnMenuPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItemRunSchedulerOnMenuPressed

    private void jMenuItemRunSchedulerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRunSchedulerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItemRunSchedulerActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        mainPanel.setVisible(false);
        String b[]=new String[2];
        Login a=new Login();
        a.main(b);

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        ClassRooms = new ClassRoomCollection();
        Courses = new CourseCollection();
        DegreeBatch = new DegreeBatchCollection();
        FacultyMembers = new FacultyCollection();
        Students = new StudentCollection();
        initComponents();

        QueryAndLoadInitialDataFromDatabase();
    }//GEN-LAST:event_jButton2ActionPerformed
    
    
    @Action
    public void SaveSchedule() {

        try {
            JFileChooser chooser = new JFileChooser();

            /*    ExampleFileFilter filter = new ExampleFileFilter();
            filter.addExtension("jpg");
            filter.addExtension("gif");
            filter.setDescription("JPG & GIF Images");
            chooser.setFileFilter(filter); 
             */

            int returnVal = chooser.showSaveDialog(jSaveSchedule);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                /*FileOutputStream fStream = new FileOutputStream(chooser.getSelectedFile());
                ObjectOutput stream = new ObjectOutputStream(fStream);

                stream.writeObject(jFullScheduleTable.getModel());
                stream.flush();
                stream.close();
                fStream.close();*/
            File file = new File("/Users/christendsouza/Desktop/output.txt");
               //if the file not exist create one
               if(!file.exists()){
                   file.createNewFile();
               }
               
               FileWriter fw = new FileWriter(file.getAbsoluteFile());
               BufferedWriter bw = new BufferedWriter(fw);
               
               //loop for jtable rows
               for(int i = 0; i < jFullScheduleTable.getRowCount(); i++){
                   //loop for jtable column
                   for(int j = 0; j < jFullScheduleTable.getColumnCount(); j++){
                       bw.write(jFullScheduleTable.getModel().getValueAt(i, j)+" ");
                   }
                   //break line at the begin 
                   //break line at the end 
                   bw.write("\n_________\n");
               }
               //close BufferedWriter
               bw.close();
               //close FileWriter 
               fw.close();
               JOptionPane.showMessageDialog(null, "Data Exported");

                //JOptionPane.showMessageDialog(null,"You chose to save to this file: " + chooser.getSelectedFile().getName());

                //JOptionPane.showMessageDialog(null, "Schedule Saved to the following file: \n\n" + chooser.getSelectedFile().getPath(), "File Saved", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        catch (IOException e) {
            JOptionPane.showMessageDialog(null,"A table saving error has occurred");
        }

    }

    @Action
    /*public void LoadSchedule() throws ClassNotFoundException {

        try {
            JFileChooser chooser = new JFileChooser();

            /*    ExampleFileFilter filter = new ExampleFileFilter();
            filter.addExtension("jpg");
            filter.addExtension("gif");
            filter.setDescription("JPG & GIF Images");
            chooser.setFileFilter(filter); 
             */
/*
            int returnVal = chooser.showOpenDialog(jEnterDB);
            int columnWidth = 150;

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                FileInputStream fStream = new FileInputStream(chooser.getSelectedFile());
                ObjectInput stream = new ObjectInputStream(fStream);

                Object obj = stream.readObject();
                jFullScheduleTable.setModel((TableModel) obj);

                fStream.close();

                PrintNewLineToConsole("You chose to open this file: " + chooser.getSelectedFile().getName());

                for (int i = 0; i < nNumberOfTimeSlots; i++) {
                    jFullScheduleTable.getColumnModel().getColumn(i + 1).setCellRenderer(new FullTableTextAreaRenderer(i));
                    jFullScheduleTable.getColumnModel().getColumn(i + 1).setHeaderRenderer(new TableHeaderRenderer());

                    if (fToggleExpandedView == false) {
                        jFullScheduleTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                        jFullScheduleTable.getColumnModel().getColumn(i + 1).setPreferredWidth(columnWidth);
                    }
                }
            }

            jTabPane.setSelectedIndex(0);
        } catch (IOException e) {
            PrintToConsole("A table loading error has occurred");
        }

    }*/

    static void PrintNewLineToConsole(String string) {
        if (DEBUG == true) {
            System.out.println(string);
        }
    }

    void PrintToConsole(String string) {
        if (DEBUG == true) {
            System.out.print(string);
        }
    }

    @Action
    public void SaveScheduleInDatabase() {

        Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
        Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);

        try {
            Integer nTotalStudentsAssignedToClassroom = 0;
            Date now = new Date();
            Statement s;

            ExamSchedulerApp.getApplication().getMainFrame().setCursor(hourglassCursor);

            String dateString = now.toString();
            SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            Date parsed = format.parse(dateString);
            String formattedTimeString = parsed.toString();
            String sqlQuery, tableName;

            formattedTimeString = formattedTimeString.replace(' ', '_');
            formattedTimeString = formattedTimeString.replace(':', '_');

            StatusBarText = "";
            fToggleDetailedView = !fToggleDetailedView;

            Class.forName("com.mysql.jdbc.Driver");
	    DriverManager.registerDriver(new com.mysql.jdbc.Driver());
					
	    String url="jdbc:mysql://localhost:3306/ExamDB";//?autoReconnect=true&relaxAutoCommit=true";
						
	    Connection cn=null;
            cn=DriverManager.getConnection(url,"root","210296book");
	    Statement st=cn.createStatement();  


            tableName = "Schedule_" + formattedTimeString;

            // Create a table for Schedule
            sqlQuery = "CREATE TABLE " + tableName + " ( ExamDay varchar(50), ExamTime varchar(50), Class varchar(50), RegnNos varchar(75), Str varchar(50), Location varchar(100), Invigilator varchar(100) )";
            st.executeUpdate(sqlQuery);

            for (int i = 0; i < nNumberOfTimeSlots; i++) {
                for (int j = 0; j < ClassRooms.GetCount(); j++) {
                    nTotalStudentsAssignedToClassroom = 0;

                    for (int k = 0; k < FullSchedule[j][i].GetAssignedCourseName().size(); k++) {
                        // Insert data into the Schedule table
                        String Day = jFacultyScheduleTable.getColumnName(1 + (i / (nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek)));
                        String Time = jFacultyScheduleTable.getValueAt(i % (nNumberOfTimeSlots / nNumberOfWorkingDaysPerWeek), 0).toString();
                        String Degree_Batch = FullSchedule[j][i].GetAssignedDegreeBatch().size() > k ? FullSchedule[j][i].GetAssignedDegreeBatch().elementAt(k).toString() : "";
                        Degree_Batch += FullSchedule[j][i].GetAssignedDegreeBatchSections().size() > k ? FullSchedule[j][i].GetAssignedDegreeBatchSections().elementAt(k).toString() : "";
                        String RegistrationNosRange = FullSchedule[j][i].GetAssignedStudentRegNos().size() > k ? FullSchedule[j][i].GetAssignedStudentRegNos().elementAt(k).toString() : "";
                        String StudentsAssigned = FullSchedule[j][i].GetNumberOfAssignedStudents().size() > k ? FullSchedule[j][i].GetNumberOfAssignedStudents().elementAt(k).toString() : "";
                        String Location = jFullScheduleTable.getValueAt(j, 0).toString();
                        String FacultyName = FullSchedule[j][i].GetAssignedFacultyName().size() > k ? FullSchedule[j][i].GetAssignedFacultyName().elementAt(k).toString() : "";

                        nTotalStudentsAssignedToClassroom += Integer.valueOf(StudentsAssigned);

                        Day = Day.trim();
                        Time = Time.trim();
                        Location = Location.trim();

                        if (k != 0) {
                            // ExamDay, ExamTime and Location should not repeat for the same classroom
                            // we override it to empty string here
                            Day = "";
                            Time = "";
                            Location = "";
                        }

                        sqlQuery = "INSERT INTO " + tableName + " values ( '" + Day +
                                "', '" + Time +
                                "', '" + Degree_Batch +
                                "', '" + RegistrationNosRange +
                                "', '" + StudentsAssigned +
                                "', '" + Location +
                                "', '" + FacultyName +
                                "');";
                        st.executeUpdate(sqlQuery);
                        //cn.close();
                    }

                    if (FullSchedule[j][i].GetAssignedCourseName().size() > 0) {
                        // store summary of Classroom
                        String Day = "";
                        String Time = "";
                        String Degree_Batch = "";
                        String RegistrationNosRange = "Total";
                        String StudentsAssigned = nTotalStudentsAssignedToClassroom.toString();
                        String Location = "";
                        String FacultyName = FullSchedule[j][i].GetAssignedFacultyName().size() > 2 ? FullSchedule[j][i].GetAssignedFacultyName().elementAt(2).toString() : "";

                        sqlQuery = "INSERT INTO " + tableName + " values ( '" + Day +
                                "', '" + Time +
                                "', '" + Degree_Batch +
                                "', '" + RegistrationNosRange +
                                "', '" + StudentsAssigned +
                                "', '" + Location +
                                "', '" + FacultyName +
                                "');";
                        st.executeUpdate(sqlQuery);
                        //cn.close();
                    }
                }
            }

            st.close();
            cn.close();

            ExamSchedulerApp.getApplication().getMainFrame().setCursor(normalCursor);

            JOptionPane.showMessageDialog(null, "Schedule Saved to Access Database with Table Name:\n     '" + tableName + "'     ", "Schedule Saved!", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            ExamSchedulerApp.getApplication().getMainFrame().setCursor(normalCursor);
            statusMessageLabel.setText("            Error: " + e);
            PrintNewLineToConsole("Error: " + e);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox jClassroomComboBox;
    private javax.swing.JLabel jClassroomCountLabel;
    private javax.swing.JLabel jClassroomLabel;
    private javax.swing.JLabel jClassroomLabel1;
    private javax.swing.JTable jClassroomScheduleTable;
    private javax.swing.JComboBox jCourseComboBox;
    private javax.swing.JLabel jCourseCountLabel;
    private javax.swing.JLabel jCourseLabel;
    private javax.swing.JLabel jCourseLabel1;
    private javax.swing.JTable jCourseScheduleTable;
    private javax.swing.JButton jDatabaseEntry;
    private javax.swing.JComboBox jDegreeBatchComboBox;
    private javax.swing.JLabel jDegreeBatchCountLabel;
    private javax.swing.JLabel jDegreeBatchLabel;
    private javax.swing.JLabel jDegreeBatchLabel1;
    private javax.swing.JTable jDegreeBatchScheduleTable;
    private javax.swing.JComboBox jFacultyComboBox;
    private javax.swing.JLabel jFacultyCountLabel;
    private javax.swing.JLabel jFacultyLabel;
    private javax.swing.JLabel jFacultyLabel1;
    private javax.swing.JTable jFacultyScheduleTable;
    private javax.swing.JTable jFullScheduleTable;
    private javax.swing.JMenuItem jMenuItemDetailedView;
    private javax.swing.JMenuItem jMenuItemExpandView;
    private javax.swing.JMenuItem jMenuItemPrintSchedule;
    private javax.swing.JMenuItem jMenuItemRunScheduler;
    private javax.swing.JButton jPrintSchedule;
    private javax.swing.JButton jRunScheduleButton;
    private javax.swing.JButton jSaveSchedule;
    private javax.swing.JButton jSaveScheduleInDatabase;
    private javax.swing.JScrollPane jScrollPaneClassroomSchedule;
    private javax.swing.JScrollPane jScrollPaneCourseSchedule;
    private javax.swing.JScrollPane jScrollPaneDegreeBatchSchedule;
    private javax.swing.JScrollPane jScrollPaneFacultySchedule;
    private javax.swing.JScrollPane jScrollPaneFullSchedule;
    private javax.swing.JLabel jStudentCountLabel;
    private javax.swing.JLabel jStudentLabel;
    private javax.swing.JTabbedPane jTabPane;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private static final boolean DEBUG = true;
    static final int nBusinessHoursStart = 900;
    static final int nBusinessHoursEnd = 1720;
    static final int nWorkingHoursPerDay = 8;
    static final int nMaxDaysAWeekOfClassesByFaculty = 3;
    static final int nMajorityPercentageInSection = 80;
    static final int nMaxClassesADayToASectionByFaculty = 2;
    static final int nDefaultMaxConsecutiveClasses = 2;
    static final int nDefaultMaxLecturesADay = 4;
    static final int nMinClassesADayForASection = 2;
    static final int STUDENTS_INVIGILATED_BY_ONE_FACULTY = 28;
    ClassTime_Schedule[][] FullSchedule;
    ClassTime_Schedule[][] DegreeBatchSchedule;
    ClassTime_Schedule[][] FacultySchedule;
    ClassTime_Schedule[][] ClassroomSchedule;
    ClassTime_Schedule[][] CourseSchedule;
    ClassRoomCollection ClassRooms;
    FacultyCollection FacultyMembers;
    StudentCollection Students;
    DegreeBatchCollection DegreeBatch;
    CourseCollection Courses;
    TimeSlot[] TimeSlots;
    Day[] Days;
    String RegNoRange;
    int StudentSerialNo = 0;
    static final long FacultyRandomizingSeed = 1234;
    String StatusBarText;
    int nSchedulingCost = 0;
    int nNumberOfTimeSlots = 0;
    int nNumberOfWorkingDaysPerWeek = 0;
    int nFacultySlotClashes = 0;
    boolean fToggleExpandedView = false;
    boolean fToggleDetailedView = true;
    boolean fScheduleCreated = false;
    private JDialog aboutBox;
    private JDialog showFacultyDialog;

    private void setTitle(String exam_Hall_Allocation) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
