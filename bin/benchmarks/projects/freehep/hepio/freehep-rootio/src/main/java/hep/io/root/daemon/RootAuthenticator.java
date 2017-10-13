package hep.io.root.daemon;

import java.awt.Component;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import javax.swing.JOptionPane;

/** 
 * A simple authenticator for use with Root Deamon protocol
 * @author Tony Johnson
 */
public class RootAuthenticator extends Authenticator
{
   private Component parent;
   
   public RootAuthenticator(Component parent)
   {
      this.parent = parent;
   }
   protected PasswordAuthentication getPasswordAuthentication() {
      AuthentificationPanel message = new AuthentificationPanel(getRequestingScheme());
      int rc = JOptionPane.showConfirmDialog(parent,message,"Authentification required",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
      if (rc == JOptionPane.OK_OPTION) return message.getPasswordAuthentication();
      else return null;
   }
}