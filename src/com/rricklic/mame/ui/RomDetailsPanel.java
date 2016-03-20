package com.rricklic.mame.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class RomDetailsPanel extends JPanel implements ListSelectionListener
{
    private static final long serialVersionUID = 1L;
    
    private static final int COMPOENENT_BUFFER = 10;
    private static final int IMAGE_WIDTH = 444;
    private static final int IMAGE_HEIGHT = 334;
    private static final String NO_IMAGE_STRING = "NO IMAGE";
    private static final String FONT_NAME = "Monospaced";
    private static final int FONT_SIZE = 18;
    private static final String MAME_HOME = "MAME_HOME";
    
    private BufferedImage image = null;
    private Rom selectedRom = null;
    
    public RomDetailsPanel(final Color backgroundColor, final int x, final int y)
    {
        setBackground(backgroundColor);
        setPreferredSize(new Dimension(x, y));
        repaint();
    }

    public void paintComponent(final Graphics g)
    {
        super.paintComponent(g);

        final Color backgroundColor = 
        		this.selectedRom == null || Rom.WorkingStatus.Unknown.equals(this.selectedRom.getWorkingStatus()) ? Color.LIGHT_GRAY :
    			Rom.WorkingStatus.Working.equals(this.selectedRom.getWorkingStatus()) ? Color.GREEN :
				Color.RED;
        	
        setBackground(backgroundColor);
        
        setForeground(Color.BLACK);
        final Font font = new Font(FONT_NAME, Font.BOLD, FONT_SIZE);
        g.setFont(font);        
        
        //Display image
        final int imagePosX = (this.getWidth() - IMAGE_WIDTH) / 2;
        final int imagePosY = COMPOENENT_BUFFER;
        if(this.image != null)
        {
            g.drawImage(this.image, imagePosX, imagePosY, IMAGE_WIDTH, IMAGE_HEIGHT, null);
        }
        else
        {
            final FontMetrics fontMetrics = g.getFontMetrics(font);
            final int textPosX = imagePosX + (IMAGE_WIDTH - fontMetrics.stringWidth(NO_IMAGE_STRING)) / 2;
            final int textPosY = imagePosY + (IMAGE_HEIGHT - FONT_SIZE)  / 2;
            
            g.setColor(Color.GRAY);
            g.fillRect(imagePosX, imagePosY, IMAGE_WIDTH, IMAGE_HEIGHT);
            g.setColor(Color.WHITE);
            g.drawString("NO IMAGE", textPosX, textPosY);
            g.setColor(Color.BLACK);
        }
        
        //Display text
        final int x = COMPOENENT_BUFFER;
        int y = IMAGE_HEIGHT + (COMPOENENT_BUFFER*3);
        g.drawString((this.selectedRom == null ? "" : "Rom Name: " + this.selectedRom.getExeName()), x, y);
        y += FONT_SIZE;
        g.drawString((this.selectedRom == null ? "" : "Full Name: " + this.selectedRom.getDisplayName()), x, y);
        y += FONT_SIZE;
        g.drawString((this.selectedRom == null ? "" : "Working Status: " + this.selectedRom.getWorkingStatus()), x, y);
        y += FONT_SIZE;
        g.drawString((this.selectedRom == null ? "" : "Is Bios: " + this.selectedRom.isBios()), x, y);
        y += FONT_SIZE;
        g.drawString((this.selectedRom == null ? "" : "Genre: " + this.selectedRom.getGenre()), x, y);
        y += FONT_SIZE;
        g.drawString((this.selectedRom == null ? "" : "Number of Players: " + this.selectedRom.getNumPlayers()), x, y);
    }    
    
    @SuppressWarnings("unchecked")
    public void valueChanged(final ListSelectionEvent e)
    {
        if(this.selectedRom == null || !e.getValueIsAdjusting())
        {
            final JList<Rom> list = (JList<Rom>)e.getSource();
            this.selectedRom = list.getSelectedValue();
            
            try
            {
            	final Map<String, String> env = System.getenv();
                final String mameHomeDir = env.get(MAME_HOME);
                final String romScreenshotImage = mameHomeDir + "\\screenshots\\" + this.selectedRom.getExeName() + ".png";
                this.image = ImageIO.read(new File(romScreenshotImage));
            }
            catch(final IOException ioe)
            {
            	System.out.print(this.selectedRom.getExeName() + ":" + ioe.toString());
                this.image = null;
            }
        
            repaint();
        }
    }
}
