package com.luneruniverse.imagecalculus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class ImagePanel extends JPanel implements ActionListener {
	
	private final ImageCalculus window;
	private final String name;
	private final BufferedImage img;
	private final BufferedImage processedImg;
	private final JButton processInput;
	private final JButton processOutput;
	private final JButton addImages;
	
	public ImagePanel(ImageCalculus window, String name, BufferedImage img, ImageSettings settings) {
		this.window = window;
		this.name = name;
		this.img = img;
		this.processedImg = Processor.process(img, settings);
		
		JPanel imgs = new JPanel();
		imgs.add(new ImageDisplay(img, 750, 750));
		imgs.add(new ImageDisplay(processedImg, 750, 750));
		JScrollPane imgsScroll = new JScrollPane(imgs);
		imgsScroll.getHorizontalScrollBar().setUnitIncrement(16);
		imgsScroll.getVerticalScrollBar().setUnitIncrement(16);
		add(imgsScroll);
		
		JPanel btns = new JPanel();
		processInput = new JButton("Process Input");
		processInput.addActionListener(this);
		btns.add(processInput);
		
		processOutput = new JButton("Process Output");
		processOutput.addActionListener(this);
		btns.add(processOutput);
		
		addImages = new JButton("Add Images");
		addImages.addActionListener(this);
		btns.add(addImages);
		add(btns);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == processInput)
			window.loadImage(name, img);
		if (e.getSource() == processOutput)
			window.loadImage(name + "'", processedImg);
		else if (e.getSource() == addImages)
			window.loadImage(name + "+", Processor.addImages(img, processedImg, window.getSettings()));
	}
	
}
