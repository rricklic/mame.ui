package com.rricklic.mame.ui;

public final class Rom
{
	private final String exeName;
	private final String displayName;
	private WorkingStatus workingStatus;
	private final boolean isBios;
	private final Genre genre;
	private final int numPlayers;
	
	public Rom(final String exeName,
			final String displayName,
			final WorkingStatus workingStatus,
			final boolean isBios,
			final Genre genre,
			final int numPlayers)
	{
		this.exeName = exeName;
		this.displayName = displayName;
		this.workingStatus = workingStatus;
		this.isBios = isBios;
		this.genre = genre;
		this.numPlayers = numPlayers;
	}
	
	public String getExeName()
	{
		return this.exeName;
	}
	
	public String getDisplayName()
	{
		return this.displayName;
	}
	
	public WorkingStatus getWorkingStatus()
	{
		return this.workingStatus;
	}
	
	public boolean isBios()
	{
		return this.isBios;
	}
	
	public Genre getGenre()
	{
		return this.genre;
	}
	
	public int getNumPlayers()
	{
		return this.numPlayers;
	}
	
	public void setWorkingStatus(final WorkingStatus workingStatus)
	{
		this.workingStatus = workingStatus;
	}

	public String toString()
	{
		return this.displayName;
	}
	
	public String toDelimiatedString()
	{
		return this.exeName + "|" +
				this.displayName + "|" + 
				this.workingStatus.getDiscriminator() + "|" +
				(this.isBios ? "T" : "F") + "|" +
				this.genre.getDiscriminator() + "|" +
				this.numPlayers;
	}
	
	public static Rom parse(final String romString)
	{
    	final String[] field = romString.split("\\|");
    	
    	if(field.length != 6) 
    	{
    		throw new RuntimeException("Expecting 6 fields for rom line, got " + field.length + " for " + romString);
    	}
    	
    	final String romExeName = field[0];
    	final String romDisplayName = field[1];    	
    	final WorkingStatus workingStatus = WorkingStatus.createFromDiscriminator(field[2]);
    	final boolean isBios = Boolean.getBoolean(field[3]);
    	final Genre genre = Genre.createFromDiscriminator(field[4]);
    	final int numPlayers = Integer.parseInt(field[5]);
    	
        return new Rom(romExeName, romDisplayName, workingStatus, isBios, genre, numPlayers);
	}
	
	public enum WorkingStatus
	{
		Working("T"), NotWorking("F"), Unknown("?");
		
		private static final String SUCCESS_EXIT_STATUS = "0";
		private final String discriminator;		
		
		WorkingStatus(final String discriminator)
		{
			this.discriminator = discriminator;
		}
		
		String getDiscriminator()
		{
			return this.discriminator;
		}
		
		public static WorkingStatus createFromExitStatus(final int exitStatus)
		{
			return createFromExitStatus(Integer.toString(exitStatus));
		}
		
		public static WorkingStatus createFromExitStatus(final String exitStatus)
		{
			return exitStatus == null ? Unknown : 
				SUCCESS_EXIT_STATUS.equals(exitStatus.trim()) ? Working :
				NotWorking;
		}
		
		public static WorkingStatus createFromDiscriminator(final String discriminator)
		{
			for(final WorkingStatus workingStatus : values())
			{
				if(workingStatus.getDiscriminator().equals(discriminator))
				{
					return workingStatus;
				}
			}
			
			return Unknown;
		}		
	}
	
	public enum Genre
	{
		Action("A"), BeatEmUp("B"), Fighter("F"), Shooter("S"), Sports("S"), Racing("R"), Unknown("?");
		
		private final String discriminator;
		
		Genre(final String discriminator)
		{
			this.discriminator = discriminator;
		}
		
		String getDiscriminator()
		{
			return this.discriminator;
		}
		
		public static Genre createFromDiscriminator(final String discriminator)
		{
			for(final Genre genre : values())
			{
				if(genre.getDiscriminator().equals(discriminator))
				{
					return genre;
				}
			}
			
			return Unknown;
		}
	}
}
