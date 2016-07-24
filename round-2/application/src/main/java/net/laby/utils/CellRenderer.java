package net.laby.utils;

import java.awt.Component;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import net.laby.application.MainConnectionsFrame;

@SuppressWarnings("rawtypes")
public class CellRenderer implements ListCellRenderer {

	protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
	
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		try {
			renderer.setIcon(new ImageIcon(ImageIO.read(MainConnectionsFrame.class.getResource("/assets/connectionIcon.png"))));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return renderer;
	}

}
