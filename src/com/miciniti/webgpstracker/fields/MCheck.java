package com.miciniti.webgpstracker.fields;

import com.miciniti.webgpstracker.Consts;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.CheckboxField;

public class MCheck extends CheckboxField
{
	private int	color = Consts.fgColor;

	public MCheck(String label, boolean state)
	{
		super(label, state, Field.FIELD_LEFT);
		setPadding(Consts.fieldPadding);
	}

	public MCheck(String label, boolean state, long style)
	{
		super(label, state, style);
		setPadding(Consts.fieldPadding);
	}

/*
	protected void drawFocus(Graphics arg0, boolean arg1)
	{
	}
*/
	protected void paint(Graphics g)
	{
		g.setColor(color);
		super.paint(g);
	}

	public void setTextColor(int color)
	{
		this.color = color;
		invalidate();
	}  	
}

