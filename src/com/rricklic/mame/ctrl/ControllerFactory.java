package com.rricklic.mame.ctrl;

public class ControllerFactory
{
	public static RomController createRomController()
	{
		return new RomControllerImpl();
	}
}
