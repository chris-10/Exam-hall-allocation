/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ExamScheduler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import javax.swing.JOptionPane;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author christendsouza
 */
public class ViewDB extends javax.swing.JFrame {
    DefaultTableModel model;
    /**
     * Creates new form ViewDB
     */
    public ViewDB() {
        initComponents();
        model=(DefaultTableModel) jTable.getModel();
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();
        jButton = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ExamScheduler.ExamSchedulerApp.class).getContext().getResourceMap(ViewDB.class);
        jScrollPane1.setBackground(resourceMap.getColor("jScrollPane1.background")); // NOI18N
        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable.setBackground(resourceMap.getColor("jTable.background")); // NOI18N
        jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Student", "Faculty", "Dept", "Subject", "Class"
            }
        ));
        jTable.setColumnSelectionAllowed(true);
        jTable.setName("jTable"); // NOI18N
        jTable.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                jTableInputMethodTextChanged(evt);
            }
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
        });
        jScrollPane1.setViewportView(jTable);
        jTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("jTable.columnModel.title0")); // NOI18N
        jTable.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("jTable.columnModel.title1")); // NOI18N
        jTable.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("jTable.columnModel.title2")); // NOI18N
        jTable.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("jTable.columnModel.title3")); // NOI18N
        jTable.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("jTable.columnModel.title4")); // NOI18N

        jButton.setText(resourceMap.getString("jButton.text")); // NOI18N
        jButton.setName("jButton"); // NOI18N
        jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonActionPerformed(evt);
            }
        });

        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(41, 41, 41)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jButton)
                        .add(29, 29, 29)
                        .add(jButton3)
                        .add(26, 26, 26)
                        .add(jButton1))
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 1190, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(97, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton)
                    .add(jButton3)
                    .add(jButton1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 681, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(127, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTableInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_jTableInputMethodTextChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_jTableInputMethodTextChanged

    private void jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonActionPerformed
        // TODO add your handling code here:
        try
					{
                                             
						Class.forName("com.mysql.jdbc.Driver");
						DriverManager.registerDriver(new com.mysql.jdbc.Driver());
					
						String url="jdbc:mysql://localhost:3306/ExamDB";//?autoReconnect=true&relaxAutoCommit=true";
						
						Connection cn=null;
                                                cn=DriverManager.getConnection(url,"root","210296book");
						Statement st=cn.createStatement();
                                                
                                                String sqlQuery = "select * from Student";
                                                ResultSet rs=st.executeQuery(sqlQuery);
                                                
                                                
                                                int flag=0,i=1,j=0,k=0;
                                                String[][] s=new String[100][100];
						//check if username exists or not in the table through while loop
                                                
						while(rs.next())
						{
                                                    s[k][j]="";
							for(i=1;i<=7;i++){
                                                                
							        s[k][j]=s[k][j]+rs.getString(i)+"\n";
                                                        }
                                                        k++;
                                                }
                                                j++;k=0;
                                                String sqlQuery2 = "select * from Faculty";
                                                ResultSet rs2=st.executeQuery(sqlQuery2);
                                                while(rs2.next())
						{
                                                    s[k][j]="";
							for(i=1;i<=7;i++){
							        s[k][j]=s[k][j]+rs2.getString(i)+"\n";
                                                        }
                                                        k++;
                                                }
                                                j++;k=0;
                                                String sqlQuery3 = "select * from Department";
                                                ResultSet rs3=st.executeQuery(sqlQuery3);
                                                while(rs3.next())
						{
                                                    s[k][j]="";
							for(i=1;i<=2;i++){
							        s[k][j]=s[k][j]+rs3.getString(i)+"\n";
                                                        }
                                                        k++;
                                                }j++;k=0;
                                                String sqlQuery4 = "select * from Subject";
                                                ResultSet rs4=st.executeQuery(sqlQuery4);
                                                while(rs4.next())
						{
                                                    s[k][j]="";
							for(i=1;i<=7;i++){
							        s[k][j]=s[k][j]+rs4.getString(i)+"\n";
                                                        }
                                                        k++;
                                                }
                                                j++;k=0;
                                                String sqlQuery5 = "select * from Class";
                                                ResultSet rs5=st.executeQuery(sqlQuery5);
                                                while(rs5.next())
						{
                                                    s[k][j]="";
							for(i=1;i<=5;i++){
							        s[k][j]=s[k][j]+rs5.getString(i)+"\n";
                                                        }
                                                        k++;
                                                }
                                        		
								//this.setVisible(false);
							//JOptionPane.showMessageDialog(null,s);
                                                 i=0;       //model.setValueAt(s, j++, 0);
                                                 String r="a";
                                                while(r!="")
                                                {
                                                    
                                                    model.addRow(new Object[]{s[i][0],s[i][1],s[i][2],s[i][3],s[i][4]});
                                                    i++;
                                                    r=s[i][0]+s[i][1]+s[i][2]+s[i][3]+s[i][4];
                                                }
                                                
                                                        
                                                        
							
							//r=r+s+"\n";
                                                       
						
                                                //model.insertRow(1, new Object[]{r,r,r,r,r});
                                                //model.setValueAt(r, 0, flag);
                                                cn.close();
						
						
						//if(flag==0)
						//{
							
						//}
						//cn.close();
					}
                                        catch(Exception e)
                                        {
                                            //JOptionPane.showMessageDialog(null, "Connection unsuccessful!");
                                            //flag=1;
                                            e.printStackTrace();
                                        }
    }//GEN-LAST:event_jButtonActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        NSAR a = new NSAR();
        a.main_NSAR();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public void main_view() {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ViewDB.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ViewDB.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ViewDB.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ViewDB.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ViewDB().setVisible(true);
                
                
                                        
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable;
    // End of variables declaration//GEN-END:variables
}
