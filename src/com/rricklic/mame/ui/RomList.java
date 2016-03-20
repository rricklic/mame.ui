package com.rricklic.mame.ui;

import java.util.List;

import javax.swing.JList;

public class RomList extends JList<Rom>
{
    private static final long serialVersionUID = 1L;
    
    public RomList()
    {
        super();
    }    
    
    public RomList(final Rom[] data, final boolean isHorizontal)
    {
        super(data);
        this.setSelectedIndex(-1);
        if(isHorizontal) { this.setLayoutOrientation(JList.HORIZONTAL_WRAP); }
    }
    
    public void incrementIndex(final int increment)
    {
        final int newIndex = this.getSelectedIndex() + increment;
        this.setSelectedIndex(newIndex < 0 ? 0 : newIndex > this.getModel().getSize()-1 ? this.getModel().getSize()-1 : newIndex);
    }

    public void setListData(final List<Rom> roms)
    {
    	this.setListData(roms.toArray(new Rom[roms.size()]));
    }
}
