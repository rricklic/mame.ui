package com.rricklic.mame.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.rricklic.mame.ctrl.ControllerFactory;
import com.rricklic.mame.ctrl.RomController;


//AKA window
public class MameUIWindow extends JFrame
{
    private static final long serialVersionUID = 1L;
    private static final String MAME_HOME = "MAME_HOME";
    
    public static void main(final String[] arg)
    {
    	try
    	{
    		final RomController romCtrl = ControllerFactory.createRomController();
    		romCtrl.dumpRomDataFile();
    	
        	final JFrame mameUiWindow = new MameUIWindow();
        	mameUiWindow.pack();
        	mameUiWindow.setVisible(true);
    	}
    	catch(final Throwable t)
    	{
    		t.printStackTrace();
    		System.exit(1);
    	}
    }    
    
    public MameUIWindow()
    {
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final int x = (int)screenSize.getWidth();
        final int y = (int)screenSize.getHeight();
        
        System.out.println(x + "x" + y);
        
        this.setTitle("Mame UI");
        this.setSize(x, y);
        this.setLocation(0, 0);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setUndecorated(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setAlwaysOnTop(true);
        this.setLocationByPlatform(true);
        
        //this.addWindowListener(new Terminator());
        
        final Container contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());
        
        //final TopPanel top = new TopPanel(Color.RED, x, 50);
        final RomListPanel romListPanel = new RomListPanel(Color.BLUE, (int)((double)x)/2, y);
        
        try
        {
        	final Map<String, String> env = System.getenv();
            final String mameHomeDir = env.get(MAME_HOME);
            final RomController romCtrl = ControllerFactory.createRomController();
            final List<Rom> roms = romCtrl.parseRomDataFile(mameHomeDir + "/data/roms.dat");
            romListPanel.setData(roms);
        }
        catch(final IOException e)
        { 
            throw new RuntimeException("Unable to open /home/dev92/rricklic/data/roms.dat");
        }

        final RomDetailsPanel romDetailsPanel = new RomDetailsPanel(Color.GREEN, (int)((double)x)/2, y);

        
        romListPanel.addListSelectionListener(romDetailsPanel);
        romListPanel.triggerInitListSelection();
        
        //contentPane.add(top, BorderLayout.PAGE_START);
        contentPane.add(romListPanel, BorderLayout.LINE_START);
        contentPane.add(romDetailsPanel, BorderLayout.LINE_END);
    }
}
