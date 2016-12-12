/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ExamScheduler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;

/**
 *
 * @author christendsouza
 */
public class Login extends javax.swing.JFrame {

    /**
     * Creates new form Login
     */
    public Login() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        Loginlabel = new javax.swing.JLabel();
        user_label = new javax.swing.JLabel();
        pass_label = new javax.swing.JLabel();
        user_text = new javax.swing.JTextField();
        Login = new javax.swing.JButton();
        pass_test = new javax.swing.JPasswordField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        jPanel1.setName("jPanel1"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ExamScheduler.ExamSchedulerApp.class).getContext().getResourceMap(Login.class);
        setBackground(resourceMap.getColor("Form.background")); // NOI18N
        setName("Form"); // NOI18N

        Loginlabel.setFont(resourceMap.getFont("Loginlabel.font")); // NOI18N
        Loginlabel.setForeground(resourceMap.getColor("Loginlabel.foreground")); // NOI18N
        Loginlabel.setText(resourceMap.getString("Loginlabel.text")); // NOI18N
        Loginlabel.setToolTipText(resourceMap.getString("Loginlabel.toolTipText")); // NOI18N
        Loginlabel.setName("Loginlabel"); // NOI18N

        user_label.setText(resourceMap.getString("user_label.text")); // NOI18N
        user_label.setName("user_label"); // NOI18N

        pass_label.setText(resourceMap.getString("pass_label.text")); // NOI18N
        pass_label.setName("pass_label"); // NOI18N

        user_text.setBackground(resourceMap.getColor("user_text.background")); // NOI18N
        user_text.setText(resourceMap.getString("user_text.text")); // NOI18N
        user_text.setName("user_text"); // NOI18N

        Login.setBackground(resourceMap.getColor("Login.background")); // NOI18N
        Login.setText(resourceMap.getString("Login.text")); // NOI18N
        Login.setName("Login"); // NOI18N
        Login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoginActionPerformed(evt);
            }
        });

        pass_test.setBackground(resourceMap.getColor("pass_test.background")); // NOI18N
        pass_test.setText(resourceMap.getString("pass_test.text")); // NOI18N
        pass_test.setName("pass_test"); // NOI18N

        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setIcon(resourceMap.getIcon("jLabel3.icon")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(137, 137, 137)
                .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 368, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(240, 240, 240)
                        .add(Loginlabel))
                    .add(layout.createSequentialGroup()
                        .add(194, 194, 194)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(Login)
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(pass_label)
                                    .add(user_label))
                                .add(51, 51, 51)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(user_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 117, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(pass_test, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 117, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(0, 504, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(jLabel1)
                        .add(340, 340, 340))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(jLabel2)
                        .add(470, 470, 470))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(64, 64, 64)
                .add(jLabel1)
                .add(37, 37, 37)
                .add(jLabel2)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(36, 36, 36)
                        .add(Loginlabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 133, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(24, 24, 24)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(user_label)
                            .add(user_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(26, 26, 26)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(pass_label)
                            .add(pass_test, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(49, 49, 49)
                        .add(Login))
                    .add(layout.createSequentialGroup()
                        .add(83, 83, 83)
                        .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 397, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(747, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void LoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoginActionPerformed
        // TODO add your handling code here:
        int flag=0;
        if(evt.getSource()==Login){
					String s_name=user_text.getText();
					char[] input=pass_test.getPassword();
					String pwd=null;
                                        pwd=String.copyValueOf(input);
                                        try
					{
						Class.forName("com.mysql.jdbc.Driver");
						DriverManager.registerDriver(new com.mysql.jdbc.Driver());
					
						String url="jdbc:mysql://localhost:3306/ExamDB";
						
						Connection cn=DriverManager.getConnection(url,"root","210296book");
						Statement st=cn.createStatement();
	                    					
						String qry="select * from login where username='"+s_name+"'";
						ResultSet rs=st.executeQuery(qry);
						
						
						//check if username exists or not in the table through while loop
						while(rs.next())
						{
							
							String s=rs.getString(2);
							//System.out.println("hello");
							//compare if the password is correct or not for the corresponding user name
							if(pwd.compareTo(s)==0)
							{
								//setVisible(false);
								//Scanner in = new Scanner(System.in);
								//while(in.nextInt() != 1);
								//System.out.println("HELLO!");
								//setVisible(true);
                                                                flag=1;
								this.setVisible(false);
								JOptionPane.showMessageDialog(null, "Successfully Logged In !!!");
								ExamSchedulerApp a=new ExamSchedulerApp();
                                                                a.main_Exam();
								
							}
							
						
						}
						if(flag==0)
						{
							JOptionPane.showMessageDialog(null, "Incorrect username or password !!!");
						}
						//cn.close();
					}
                                        catch(Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                        
        }
                
        
        //demo a =new demo();
        //a.main_demo();
        
    }//GEN-LAST:event_LoginActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
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
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Login;
    private javax.swing.JLabel Loginlabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel pass_label;
    private javax.swing.JPasswordField pass_test;
    private javax.swing.JLabel user_label;
    private javax.swing.JTextField user_text;
    // End of variables declaration//GEN-END:variables
}
