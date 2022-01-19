package com.miciniti.webgpstracker;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.decor.BorderFactory;

public class PasswordDialog extends Dialog
{
    private PasswordEditField 	password;

    public PasswordDialog(String text)
    {
        super(Dialog.D_OK_CANCEL, text, 0, Bitmap.getPredefinedBitmap(Bitmap.QUESTION), Dialog.GLOBAL_STATUS);
        
        Manager delegate = getDelegate();
       
        if( delegate instanceof DialogFieldManager)
        {
            DialogFieldManager dfm = (DialogFieldManager)delegate;
            Manager manager = dfm.getCustomManager();
            
            if( manager != null )
            {
                password = new PasswordEditField("", "", 32, EditField.FIELD_HCENTER | EditField.NO_NEWLINE | EditField.EDITABLE | EditField.CONSUME_INPUT);
                password.setBorder(BorderFactory.createSimpleBorder(Consts.fieldBorder));    	
                password.setMargin(Consts.fieldMargin);
                password.setPadding(Consts.fieldPadding);
                
                manager.add(password);
            }
        }
    }    

    public String getPassword()
    {
      return password.getText().trim();
    }

}
