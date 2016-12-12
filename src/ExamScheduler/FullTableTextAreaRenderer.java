/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ExamScheduler;

/**
 *
 * @author christendsouza
 */

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

  public class FullTableTextAreaRenderer extends JTextArea
      implements TableCellRenderer {

    public FullTableTextAreaRenderer( int colIndex) {
        
      columnIndex = colIndex;
      
      setLineWrap(true);
      setWrapStyleWord(true);
      setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable jTable,
        Object obj, boolean isSelected, boolean hasFocus, int row,
        int column) {

        if (isSelected) {
          setForeground(jTable.getSelectionForeground());
          setBackground(jTable.getSelectionBackground());
        } else {
          java.awt.Color BgColor = jTable.getBackground();
          if((int)((columnIndex/3))%2 == 0 )
              BgColor = BgColor.brighter();
          
          setForeground(jTable.getForeground());
          setBackground(BgColor);
        }
        setFont(jTable.getFont());

        if (hasFocus) {
          setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
          if (jTable.isCellEditable(row, column)) {
            //setForeground(UIManager.getColor("Table.focusCellForeground"));
            //setBackground(UIManager.getColor("Table.focusCellBackground"));
          }
        } else {
          setBorder(new EmptyBorder(1, 2, 1, 2));
        }
        setText((obj == null) ? "" : obj.toString());
        //setToolTipText(obj.toString());

        return this;
    }
    
    int columnIndex = 0;
  }
