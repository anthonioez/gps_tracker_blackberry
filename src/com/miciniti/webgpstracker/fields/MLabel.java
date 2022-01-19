package com.miciniti.webgpstracker.fields;

import com.miciniti.webgpstracker.Consts;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.LabelField;

public class MLabel extends LabelField
{
	private int	color = Consts.fgColor;

	public MLabel(String label)
	{
		super(label, Field.FIELD_LEFT);
		setPadding(Consts.fieldPadding);
	}

	public MLabel(String label, int color)
	{
		super(label, Field.FIELD_LEFT);
		setPadding(Consts.fieldPadding);
	}

	public MLabel(String label, long style)
	{
		super(label, style);
		setPadding(Consts.fieldPadding);
	}


	protected void drawFocus(Graphics arg0, boolean arg1)
	{
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

