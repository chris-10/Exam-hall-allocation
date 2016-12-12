/*
 * ExamSchedulerApp.java
 */

package ExamScheduler;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class ExamSchedulerApp extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        show(new ExamSchedulerView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of ExamSchedulerApp
     */
    public static ExamSchedulerApp getApplication() {
        return Application.getInstance(ExamSchedulerApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main_Exam() {
        ExamSchedulerApp a=new ExamSchedulerApp();
        a.startup();
    }
}
