package com.rricklic.mame.util;

import java.io.IOException;

import com.rricklic.mame.ctrl.ControllerFactory;
import com.rricklic.mame.ctrl.RomController;

public class RomNameFileDumper
{
    public static void main(final String[] arg)
    {    
    	try
    	{
    		final RomNameFileDumper dumper = new RomNameFileDumper();
    		dumper.run();
    	}
    	catch(final Throwable t)
    	{
    		t.printStackTrace();
    		System.exit(1);
    	}
    }
    
    public void run()
		throws IOException
    {
		final RomController romCtrl = ControllerFactory.createRomController();
		romCtrl.dumpRomDataFile();
	}
}
