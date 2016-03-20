package com.rricklic.mame.ctrl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rricklic.mame.ui.Rom;

final class RomControllerImpl implements RomController
{
	public String fetchMameHomeDir()
	{
    	final Map<String, String> env = System.getenv();
        final String mameHomeDir = env.get(MAME_HOME);
		
        if(mameHomeDir == null)
        {
        	throw new RuntimeException(MAME_HOME + " environment variable not set.");
        }
        
        return mameHomeDir;
	}
	
	public void dumpRomDataFile()
		throws IOException
	{
    	final String mameHomeDir = fetchMameHomeDir();
	    
	    final Set<String> romNames = buildRomNameSet(mameHomeDir);	    
	    final Map<String, String> romDisplayNameMap = buildDisplayNameMap(mameHomeDir);        
        final Map<String, String> romExitStatusMap = buildExitStatusMap(mameHomeDir);
        
	    final PrintWriter writer = new PrintWriter(mameHomeDir + "/" + DATA_DIR + "/" + DATA_FILENAME);
	    
	    for(final String romName : romNames)
	    {
	    	if(!romDisplayNameMap.containsKey(romName)) { continue; }
	    	
    		final String romDisplayName = romDisplayNameMap.get(romName);
			final Rom.WorkingStatus workingStatus = Rom.WorkingStatus.createFromExitStatus(romExitStatusMap.get(romName));
			final boolean isBios = false;
			final Rom.Genre genre = Rom.Genre.Unknown;
			final int numPlayers = -1;
			
			final Rom rom = new Rom(romName, romDisplayName, workingStatus, isBios, genre, numPlayers);
    		writer.println(rom.toDelimiatedString());
	    }
	    
	    writer.close();
	}

	public List<Rom> parseRomDataFile(final String filename)
		throws IOException
	{
    	final List<Rom> roms = new ArrayList<>();
    	
        final BufferedReader reader = new BufferedReader(new FileReader(filename));
        for(String line; (line = reader.readLine()) != null; )
        {
        	roms.add(Rom.parse(line));
        }
        reader.close();
        
        return roms;
	}
	
	public int runRom(final String romName)
		throws IOException, InterruptedException
	{
    	final Map<String, String> env = System.getenv();
        final String mameHomeDir = env.get(MAME_HOME) + "\\";
        final File pathToMameExecutable = new File(mameHomeDir + "/mame.exe");
        final ProcessBuilder processBuilder = new ProcessBuilder(pathToMameExecutable.getAbsolutePath(), romName);
		processBuilder.directory(new File(mameHomeDir).getAbsoluteFile());
		final Process p = processBuilder.start();
        final int exitStatus = p.waitFor();
        
        //write exit status file for rom
        //System.out.println(this.list.getSelectedValue() + ":" + exitStatus);
	    final PrintWriter writer = new PrintWriter(mameHomeDir + "/data/exit_status/" + romName + ".exit");
	    writer.println(exitStatus);
        writer.close();
        
        return exitStatus;
	}
	
    private Set<String> buildRomNameSet(final String mameHomeDir)
    {
    	final SortedSet<String> romNames = new TreeSet<>();
	    final File romDir = new File(mameHomeDir + "/" + ROMS_DIR + "/");
	    
	    if(romDir.listFiles() == null || romDir.listFiles().length == 0)
	    {
	    	throw new RuntimeException("No roms in " + mameHomeDir + "/" + ROMS_DIR + "/");
	    }
	    
	    for(final File romFile : romDir.listFiles())
	    {
	    	if(romFile.isFile() && romFile.getName().endsWith(".zip"))
	    	{
	    		final String romFileName = romFile.getName();
	    		final String romFileNameNoExtension = 
	    				romFileName.substring(0, romFileName.indexOf("."));
	    		romNames.add(romFileNameNoExtension);
    		}
	    }
	    return romNames;
    }
    
    private Map<String, String> buildDisplayNameMap(final String mameHomeDir)
    	throws IOException
    {
    	final Map<String, String> displayNameMap = new HashMap<>();
    	
        final BufferedReader reader = new BufferedReader(new FileReader(mameHomeDir + "/" + DATA_DIR + "/" + FULLNAME_FILENAME));
        for(String line; (line = reader.readLine()) != null; )
        {
        	if(line.startsWith("Name:")) { continue; }
        	
        	final Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
        	final Matcher regexMatcher = regex.matcher(line);
        	final String[] fields = new String[2];
        	int index = 0;
        	while(regexMatcher.find() && index < 2)
        	{
        		fields[index++] = regexMatcher.group().replaceAll("\"", "");
            }

        	final String romName = fields[0];
            final String romFullName = fields[1] == null ? "FULL NAME MISSING" : fields[1];
            final int endIndex = romFullName.contains("(") ? romFullName.indexOf("(") : romFullName.length();
            displayNameMap.put(romName, romFullName.substring(0, endIndex).trim());
        }
        reader.close();   
        
        return displayNameMap;
    }

    private Map<String, String> buildExitStatusMap(final String mameHomeDir)
		throws IOException
    {
    	final Map<String, String> exitStatusMap = new HashMap<>();
    	
	    final File exitStatusDir = new File(mameHomeDir + "/" + EXITSTATUS_DIR + "/");
	    
	    if(exitStatusDir.listFiles() == null)
	    {
	    	throw new RuntimeException(mameHomeDir + "/" + EXITSTATUS_DIR + "/" + " is missing");
	    }	    
	    
	    for(final File exitStatusFile : exitStatusDir.listFiles())
	    {
	    	if(exitStatusFile.isFile() && exitStatusFile.getName().endsWith(".exit"))
	    	{	
	            final BufferedReader reader = new BufferedReader(new FileReader(exitStatusFile.getPath()));
	            final String exitStatus = reader.readLine();
	            reader.close();
	    		
	    		final String exitStatusFileName = exitStatusFile.getName();
	    		final String exitStatusFileNameNoExtension = 
	    				exitStatusFileName.substring(0, exitStatusFileName.indexOf("."));
	    		exitStatusMap.put(exitStatusFileNameNoExtension, exitStatus);
    		}
	    }
	    return exitStatusMap;
    }	
}
