package com.miciniti.webgpstracker.fields;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.BasicEditField;

import com.miciniti.webgpstracker.Consts;

public class MEdit extends BasicEditField
{
	private int	color = Consts.fgColor;

	public MEdit(String label, String initial, int max, long style)
	{
		super(label, initial, max, Field.FIELD_LEFT);
		setPadding(Consts.fieldPadding);
	}

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

