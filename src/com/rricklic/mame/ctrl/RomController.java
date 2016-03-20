package com.rricklic.mame.ctrl;

import java.io.IOException;
import java.util.List;

import com.rricklic.mame.ui.Rom;

public interface RomController
{
    String MAME_HOME = "MAME_HOME";
    String ROMS_DIR = "roms";
    String DATA_DIR = "data";
    String EXITSTATUS_DIR = "data/exit_status";
    String DATA_FILENAME = "roms3.dat";
    String FULLNAME_FILENAME = "romsfullname.dat";

    String fetchMameHomeDir();
    
	void dumpRomDataFile()
		throws IOException;

	List<Rom> parseRomDataFile(String filename)
		throws IOException;
	
	int runRom(String romName)
		throws IOException, InterruptedException;
}
