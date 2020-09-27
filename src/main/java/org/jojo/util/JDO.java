package org.jojo.util;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jojo.util.TextUtil;

@SuppressWarnings("serial")
public class JDO extends JDialog
{
 private JButton b1;
 private JButton b2;
 
 public JDO()
 {
	System.err.println("NO JDO SELECTED!");
 }

 public JDO(String title, String text)
 {
	 b1 = null;
	 b2 = null;
	 setAlwaysOnTop(true);
	 setTitle(title);
	 add(new JLabel(TextUtil.toHTML(text)));
	 Dimension fs = getSize();
     final Dimension d = getToolkit().getScreenSize();
     setLocation((int) ((d.getWidth() - getWidth()) / 2 - fs.width / 2), (int) ((d.getHeight() - getHeight()) / 2 - fs.height / 2));
 }
 
 public JDO(String title, String text, String button)
 {
	 b1 = new JButton(button);
	 b2 = null;
	 setAlwaysOnTop(true);
	 setTitle(title);
	 Container c = getContentPane();
	 
	 JLabel txt = new JLabel(TextUtil.toHTML(text));
	 Dimension d = txt.getPreferredSize();
	 d.height = 25;
	 d.width = (int)d.getWidth();
		
	 if(b1!= null && b2==null)
		 b1.setMaximumSize(d);
	 if(b2!=null)
	 {
		d.width = (int)d.getWidth()/2;
		b1.setMaximumSize(d);
		b2.setMaximumSize(d);
	 }
	 
	 c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
	 c.add(txt);
	 c.add(b1);
 }
 
 public JDO(String title, String text, String button1, String button2)
 {
	 b1 = new JButton(button1);
	 b2 = new JButton(button2);
	 setAlwaysOnTop(true);
	 setTitle(title);
	 Container c = getContentPane();
	 
	 JLabel txt = new JLabel(TextUtil.toHTML(text));
	 Dimension d = txt.getPreferredSize();
	 d.height = 25;
	 d.width = (int)d.getWidth();
		
	 if(b1!= null && b2==null)
		 b1.setMaximumSize(d);
	 if(b2!=null)
	 {
		d.width = (int)d.getWidth()/2;
		b1.setMaximumSize(d);
		b2.setMaximumSize(d);
	 }
	 
	 c.add(txt,"North");
	  JPanel buttons = new JPanel();
	  buttons.setLayout(new BoxLayout(buttons,BoxLayout.X_AXIS));
	  buttons.add(b1);
	  buttons.add(b2);
	 c.add(buttons,"Center");
 }
 
 public void open()
 {
	pack();
	setVisible(true);
 }
 
 public JButton getB1()
 {
	 return b1;
 }
 
 public JButton getB2()
 {
	 return b2;
 }

 public void close(JButton in) 
 {
  if(in.equals(b1))
  {
   b1.addActionListener(new ActionListener(){

	public void actionPerformed(ActionEvent arg0) {
		dispose();
	}});
  }
  else if(in.equals(b2))
  {
	b2.addActionListener(new ActionListener(){

	public void actionPerformed(ActionEvent arg0) {
	    dispose();
	}});
  } 
  else
	  System.err.println("NO SUCH BUTTON!");
 }
 
 public void hardExit(JButton in) 
 {
  if(in.equals(b1))
  {
   b1.addActionListener(new ActionListener(){

	public void actionPerformed(ActionEvent arg0) {
		System.exit(0);
	}});
  }
  else if(in.equals(b2))
  {
	b2.addActionListener(new ActionListener(){

	public void actionPerformed(ActionEvent arg0) {
		System.exit(0);
	}});
  } 
  else
	  System.err.println("NO SUCH BUTTON!");
 }
}
