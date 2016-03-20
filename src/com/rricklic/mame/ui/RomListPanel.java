package com.rricklic.mame.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionListener;

import com.rricklic.mame.ctrl.ControllerFactory;
import com.rricklic.mame.ctrl.RomController;

public class RomListPanel extends JPanel implements ActionListener, KeyListener
{
    private static final long serialVersionUID = 1L;
    private static final int FONT_SIZE = 24; 
    private static final String NO_SELECTION = "---";
    private static final String[] DATA_ALPHA = 
          { NO_SELECTION, "#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
    static final String[] DATA_GENRE = 
        { NO_SELECTION, "Action", "Beat'em Up", "Fighter", "Shooter", "Sport" };
    private final JComboBox<String> romLetterFilter;
    private final JComboBox<String> romGenreFilter;
    private final RomList list;
    private final JScrollPane scrollPane;
    private List<Rom> roms = new ArrayList<>();
    
    public RomListPanel(final Color backgroundColor, final int x, final int y)
    {
        setBackground(backgroundColor);
        setPreferredSize(new Dimension(x, y));

        this.romLetterFilter = new JComboBox<String>(DATA_ALPHA);
        this.romLetterFilter.setSelectedIndex(-1);
        this.romLetterFilter.addActionListener(this);
        
        this.romGenreFilter = new JComboBox<String>(DATA_GENRE);
        this.romGenreFilter.setSelectedIndex(-1);
        this.romGenreFilter.addActionListener(this);
        
        this.list = new RomList();
        this.list.addKeyListener(this);
        this.list.setFont(new Font("Monospaced", Font.BOLD, FONT_SIZE));
        this.list.setAlignmentX(JList.LEFT_ALIGNMENT);
        this.list.setAlignmentY(JList.LEFT_ALIGNMENT);
        this.list.requestFocus();

        this.scrollPane = new JScrollPane();
        this.scrollPane.setFocusCycleRoot(true);
        this.scrollPane.setPreferredSize(new Dimension(x, y));
        this.scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.scrollPane.setViewportView(this.list);
        final JScrollBar scrollBar = this.scrollPane.getVerticalScrollBar();
        final InputMap inputMap = scrollBar.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke("LEFT"), "negativeUnitIncrement");
        inputMap.put(KeyStroke.getKeyStroke("RIGHT"), "positiveUnitIncrement");

//        this.add(this.romLetterFilter);
//        this.add(this.romGenreFilter);
        this.add(this.scrollPane);
        
        repaint();
    }
    
    public void setData(final List<Rom> roms)
        throws IOException
    {
        this.list.setListData(roms);        
        repaint();
    }
    
    public void addListSelectionListener(final ListSelectionListener listSelectionListener)
    {
        this.list.addListSelectionListener(listSelectionListener);
    }
    
    public void triggerInitListSelection()
    {
        this.list.setSelectedIndex(0);
    }

    public void keyPressed(final KeyEvent e)
    {
        final int key = e.getKeyCode();
        switch(key)
        {
            case KeyEvent.VK_LEFT : 
                this.list.incrementIndex(-10); 
                this.list.scrollRectToVisible(this.list.getCellBounds(this.list.getSelectedIndex(), this.list.getSelectedIndex())); 
                break;
            case KeyEvent.VK_RIGHT : 
                this.list.incrementIndex(10); 
                this.list.scrollRectToVisible(this.list.getCellBounds(this.list.getSelectedIndex(), this.list.getSelectedIndex())); 
                break;
            case KeyEvent.VK_ENTER : runRom(); break;
            case KeyEvent.VK_CONTROL : runRom(); break;
        }
    }

    public void keyTyped(final KeyEvent e) {}
    public void keyReleased(final KeyEvent e) {}
    
    private void runRom()
    {
		try
		{
			final Rom rom = this.list.getSelectedValue();
			final RomController romCtrl = ControllerFactory.createRomController();
			final int exitStatus = romCtrl.runRom(rom.getExeName());
			rom.setWorkingStatus(Rom.WorkingStatus.createFromExitStatus(exitStatus));
			this.list.incrementIndex(1);
			this.list.incrementIndex(-1);
		}
		catch(final IOException | InterruptedException e)
		{
			//TODO: display in UI
			e.printStackTrace();
		}
    }

	public void actionPerformed(final ActionEvent e)
	{
		if(e.getSource() == this.romLetterFilter ||  e.getSource() == this.romGenreFilter)
		{
			filterRoms();
		}
	}
	
	private void filterRoms()
	{
		final String aRomLetterFilter = (String)this.romLetterFilter.getSelectedItem();
		final String aRomGenreFilter = (String)this.romGenreFilter.getSelectedItem();		
		
		System.out.println(aRomLetterFilter + ":" + aRomGenreFilter + ":" + this.roms);
		
		final List<Rom> filteredRoms = new ArrayList<>();
		for(final Rom rom : this.roms)
		{
			if(aRomLetterFilter != null && 
			   !aRomLetterFilter.equals(NO_SELECTION) && 
			   !aRomLetterFilter.equalsIgnoreCase(rom.getDisplayName().substring(0, 1)))
			{
				continue;
			}
			
			filteredRoms.add(rom);
		}
		
		this.list.setListData(filteredRoms);        
        repaint();
	}
}
