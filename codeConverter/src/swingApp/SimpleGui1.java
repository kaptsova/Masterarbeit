package swingApp;

import javax.swing.*;
import java.awt.event.*;

public class SimpleGui1 implements ActionListener{
	
	JButton button;
	boolean textChangedEven = true;
	
	public static void main(String[] args)
	{
		SimpleGui1 simpleGui = new SimpleGui1();
		simpleGui.go();
	}
	
	public void go()
	{
		JFrame frame = new JFrame();
		button = new JButton("Click me");
		
		button.addActionListener(this);
		
		frame.getContentPane().add(button);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);			
		frame.setSize(1000, 800);
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stub
		if (textChangedEven == true)
			button.setText("Text changed even times");	
		else
			button.setText("Text changen uneven times");
		
		textChangedEven = !textChangedEven;
		
	}

}
