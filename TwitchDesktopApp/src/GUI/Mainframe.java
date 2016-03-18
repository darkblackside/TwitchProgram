package GUI;
import java.awt.FlowLayout;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ChatComponents.Chat;
import GUI.Controllers.ChatController;
import exceptions.ConnectionException;
import exceptions.NoSettingsException;
import exceptions.SettingsNotInitializedException;
import models.Settings;
import models.SettingsBuilder;

public class Mainframe extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8159124900675570990L;
	private OptionPane tabbedpane;
	private Chat c;
	
	public Mainframe()
	{
		//Initializing settings, views and controllers
		initializeSettings();
		try {
			c = new Chat();
		} catch (ClassNotFoundException | IOException | NoSettingsException | SettingsNotInitializedException
				| ConnectionException e) {
			e.printStackTrace();
			showError("Error while connecting to Server");
			if(JOptionPane.showConfirmDialog(this, "Do you want to reconfigure the system?") == JOptionPane.YES_OPTION)
			{
				SettingsBuilder.deleteConfig(true);
				showError("Please restart application in order to reconfigure");
			}
		}
		
		tabbedpane = new OptionPane();
		
		this.setLayout(new FlowLayout());
		ChatView chView = new ChatView();
		this.add(chView);
		ChatController chController = new ChatController(chView, c.getUsersList());
		c.addListener(chController);
		chView.addListenerForButtons(chController);
		chController.addBroadcastListener(c);
		this.add(tabbedpane);
		this.pack();
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	private void showError(String string)
	{
		JOptionPane.showMessageDialog(this, string, "Error", JOptionPane.ERROR_MESSAGE);
	}

	private void initializeSettings()
	{
		//Wichtig, da sonst die Settings nicht funktionieren:
		Settings s = new Settings();
		s.addSetting("botname", "darkblackside");
		s.addSetting("server", "irc.twitch.tv");
		s.addSetting("port", "6667");
		s.addSetting("oauthkey", "oauth:xxxx");
		s.addSetting("channelname", "darkblackside");
		
		if(!SettingsBuilder.settingsFileExists())
		{
			s.updateSetting("botname", JOptionPane.showInputDialog("Name your chatbot (likely your channelname on twitch)"));
			s.updateSetting("oauthkey", JOptionPane.showInputDialog("Enter your oauth key (for beta you have to get the key without this program)"));
			s.updateSetting("channelname", JOptionPane.showInputDialog("Type your channelname"));
		}
		
		try {
			SettingsBuilder.initialize(s);
		} catch (ClassNotFoundException | IOException e) {
			showError("Error while initializing settings");
			e.printStackTrace();
		}
	}

	public OptionPane getOptionsPane()
	{
		return tabbedpane;
	}
	
	public static void main(String[] args)
	{
		try
		{
			Mainframe mf = new Mainframe();
			mf.setVisible(true);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(ABORT);
		}
	}
}