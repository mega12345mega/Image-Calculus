package com.luneruniverse.imagecalculus;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileFilter;

public class ImageCalculus {
	
	public static void main(String[] args) {
		new ImageCalculus();
	}
	
	private final JFrame window;
	private final JTabbedPane tabs;
	private final JMenuItem fileMenuCloseImage;
	private Color.Type type;
	private boolean integral;
	private boolean derivative2;
	private boolean slopeField;
	private boolean slopeFieldMagnitude;
	private boolean rescale;
	
	@SuppressWarnings("serial")
	public ImageCalculus() {
		window = new JFrame();
		window.setTitle("Image Calculus");
		window.setSize(1000, 750);
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		tabs = new JTabbedPane();
		window.add(tabs);
		
		// --- Menu Bar ---
		JMenuBar bar = new JMenuBar();
		window.setJMenuBar(bar);
		
		JMenu fileMenu = new JMenu("File");
		JMenuItem fileMenuLoadImage = new JMenuItem("Load Image");
		fileMenuCloseImage = new JMenuItem("Close Image");
		bar.add(fileMenu);
		
		JMenu controlsMenu = new JMenu("Controls");
		JMenuItem controlsMenuChangeType = new JMenuItem("Switch to HSV");
		JMenuItem controlsMenuIntegral = new JMenuItem("Switch to Integral");
		JMenuItem integralMenuRescale = new JCheckBoxMenuItem("Rescale Output");
		bar.add(controlsMenu);
		
		JMenu derivativeMenu = new JMenu("Derivative");
		JMenuItem derivativeMenuType2 = new JCheckBoxMenuItem("Type 2");
		JMenuItem derivativeMenuSlopeField = new JCheckBoxMenuItem("Slope Field");
		JMenuItem derivativeMenuSlopeFieldMagnitude = new JCheckBoxMenuItem("Slope Field Magnitude Colors");
		bar.add(derivativeMenu);
		
		JMenu integralMenu = new JMenu("Integral");
		bar.add(integralMenu);
		
		JMenu helpMenu = new JMenu("Help");
		bar.add(helpMenu);
		
		// File Menu
		fileMenuLoadImage.addActionListener(e -> onLoadImageAction());
		fileMenu.add(fileMenuLoadImage);
		
		fileMenuCloseImage.addActionListener(e -> {
			tabs.remove(tabs.getSelectedIndex());
			if (tabs.getSelectedIndex() == -1)
				fileMenuCloseImage.setEnabled(false);
		});
		fileMenuCloseImage.setEnabled(false);
		fileMenu.add(fileMenuCloseImage);
		
		// Controls Menu
		type = Color.Type.RGB;
		controlsMenuChangeType.addActionListener(e -> {
			if (type == Color.Type.RGB) {
				type = Color.Type.HSV;
				controlsMenuChangeType.setText("Switch to RGB");
			} else {
				type = Color.Type.RGB;
				controlsMenuChangeType.setText("Switch to HSV");
			}
		});
		controlsMenu.add(controlsMenuChangeType);
		
		integral = false;
		controlsMenuIntegral.addActionListener(e -> {
			if (integral) {
				integral = false;
				controlsMenuIntegral.setText("Switch to Integral");
			} else {
				integral = true;
				controlsMenuIntegral.setText("Switch to Derivative");
			}
		});
		controlsMenu.add(controlsMenuIntegral);
		
		// Derivative Menu
		derivative2 = false;
		derivativeMenuType2.addActionListener(e -> {
			derivative2 = !derivative2;
			derivativeMenuSlopeField.setEnabled(!derivative2);
			derivativeMenuSlopeFieldMagnitude.setEnabled(!derivative2 && slopeField);
		});
		derivativeMenu.add(derivativeMenuType2);
		
		slopeField = false;
		derivativeMenuSlopeField.addActionListener(e -> {
			slopeField = !slopeField;
			derivativeMenuSlopeFieldMagnitude.setEnabled(slopeField);
		});
		derivativeMenu.add(derivativeMenuSlopeField);
		
		slopeFieldMagnitude = false;
		derivativeMenuSlopeFieldMagnitude.addActionListener(e -> slopeFieldMagnitude = !slopeFieldMagnitude);
		derivativeMenuSlopeFieldMagnitude.setEnabled(false);
		derivativeMenu.add(derivativeMenuSlopeFieldMagnitude);
		
		// Integral Menu
		rescale = false;
		integralMenuRescale.addActionListener(e -> rescale = !rescale);
		integralMenu.add(integralMenuRescale);
		
		// Help Menu
		helpMenu.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JLabel text = new JLabel("""
						<html><pre>Process an image via "File &gt; Load Image" or dragging the file onto the window.
						Use "Controls" for RGB &lt;-&gt; HSV and Derivative &lt;-&gt; Integral switching.
						"Process Input" will re-process the input image using new settings, while \
						"Process Output" will re-process the output image.
						"Add Images" will add the input and output image, then process the result. \
						This assumes that the output uses the signed encoding.
						Click on an image to save it to a file.
						
						Created by mega12345mega (github.com/mega12345mega)</pre></html>""");
				text.setVerticalAlignment(JLabel.TOP);
				text.setBackground(java.awt.Color.WHITE);
				text.setOpaque(true);
				addTab("Help", text);
			}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
		});
		
		// --- /Menu Bar ---
		
		window.setDropTarget(new DropTarget() {
			public synchronized void drop(DropTargetDropEvent event) {
				if (event.getCurrentDataFlavorsAsList().contains(DataFlavor.javaFileListFlavor)) {
					event.acceptDrop(DnDConstants.ACTION_COPY);
					try {
						@SuppressWarnings("unchecked")
						List<File> files = (List<File>) event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
						for (File file : files)
							loadImage(file);
					} catch (IOException | UnsupportedFlavorException | ClassCastException e) {
						e.printStackTrace();
					} finally {
						event.dropComplete(true);
					}
				}
			}
		});
		
		window.setVisible(true);
	}
	
	private void onLoadImageAction() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return "Images";
			}
			@Override
			public boolean accept(File file) {
				return true;
			}
		});
		chooser.setVisible(true);
		if (chooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
			try {
				loadImage(chooser.getSelectedFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public ImageSettings getSettings() {
		return new ImageSettings(type, integral, derivative2, slopeField, slopeFieldMagnitude, rescale);
	}
	
	public void loadImage(File file) throws IOException {
		loadImage(file.getName(), ImageIO.read(file));
	}
	public void loadImage(String name, BufferedImage img) {
		addTab(name, new ImagePanel(this, name, img, getSettings()));
	}
	
	private void addTab(String name, Component content) {
		tabs.addTab(name, content);
		JPanel tabName = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		tabName.setOpaque(false);
		tabName.add(new JLabel(name + " "));
		JLabel closeBtn = new JLabel("X");
		closeBtn.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				tabs.remove(tabs.indexOfTabComponent(tabName));
				if (tabs.getSelectedIndex() == -1)
					fileMenuCloseImage.setEnabled(false);
			}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {
				closeBtn.setForeground(java.awt.Color.RED);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				closeBtn.setForeground(java.awt.Color.BLACK);
			}
		});
		tabName.add(closeBtn);
		tabs.setTabComponentAt(tabs.getTabCount() - 1, tabName);
		fileMenuCloseImage.setEnabled(true);
	}
	
}
