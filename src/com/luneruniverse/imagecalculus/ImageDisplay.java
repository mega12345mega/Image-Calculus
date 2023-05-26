package com.luneruniverse.imagecalculus;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class ImageDisplay extends JComponent implements MouseListener {
	
	private final BufferedImage img;
	private final int width;
	private final int height;
	
	public ImageDisplay(BufferedImage img, int width, int height) {
		this.img = img;
		this.width = width;
		this.height = height;
		setSize(width, height);
		setPreferredSize(getSize());
		addMouseListener(this);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(img, 0, 0, width, height, null);
	}
	
	@Override
	public void mouseClicked(MouseEvent event) {
		JFileChooser chooser = new JFileChooser();
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			if (!file.exists() || JOptionPane.showConfirmDialog(this, file.getName() + " already exists! Overwrite?",
					"Warning", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION) {
				int lastDot = file.getName().lastIndexOf('.');
				try {
					BufferedImage noAlphaImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
					Graphics2D g = noAlphaImg.createGraphics();
					g.drawImage(img, 0, 0, null);
					g.dispose();
					ImageIO.write(noAlphaImg, lastDot == -1 ? "png" : file.getName().substring(lastDot + 1), file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {}
	
	@Override
	public void mouseReleased(MouseEvent e) {}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	
	@Override
	public void mouseExited(MouseEvent e) {}
	
}
