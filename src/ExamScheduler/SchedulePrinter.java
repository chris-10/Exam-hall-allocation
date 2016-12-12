/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ExamScheduler;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import javax.print.PrintService;
import javax.print.attribute.PrintServiceAttributeSet;

/**
 *
 * @author christendsouza
 */
public class SchedulePrinter implements Printable, ActionListener {

public int print(Graphics g, PageFormat pf, int page) throws
                                                        PrinterException {


        /* User (0,0) is typically outside the imageable area, so we must
         * translate by the X and Y values in the PageFormat to avoid clipping
         */
        Graphics2D g2d = (Graphics2D)g;
        g2d.translate(pf.getImageableX(), pf.getImageableY() );

        
        /* Now we perform our rendering */
        //g.drawString("Hello world!", 100, 100);

        
        ExamSchedulerApp myApp = ExamSchedulerApp.getApplication();
        ExamSchedulerView myView = (ExamSchedulerView) myApp.getMainView();

        return myView.Print_DegreeBatch_Classroom_Faculty_Schedule( g2d, page, selectedPrinter );
    }
    

   public void actionPerformed(ActionEvent e) {
         PrinterJob job = PrinterJob.getPrinterJob();
         job.setPrintable(this);

         job.setJobName("Examination Schedule Print");
        
         boolean ok = job.printDialog();
         
         selectedPrinter = job.getPrintService();
         
         if (ok) {
             try {
                  job.print();
                  
                  
             } catch (PrinterException ex) {
              /* The job did not successfully complete */
             }
         }
    }

    PrintService selectedPrinter;
}
