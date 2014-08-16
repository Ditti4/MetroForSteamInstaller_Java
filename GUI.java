import static com.sun.jna.platform.win32.WinReg.HKEY_LOCAL_MACHINE;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import net.lingala.zip4j.core.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.sun.jna.platform.win32.Advapi32Util;


public class GUI extends JFrame implements ActionListener{
  
	private JLabel textversion = new JLabel(), versionL = new JLabel(), textnewversion = new JLabel(), newversionL = new JLabel(), updateNotice = new JLabel();
	private JCheckBox checkyCustomStyle = new JCheckBox();
	private JButton doIt = new JButton();
	private String installPath = "", version = "", newversion = "", dl = "", cookie = "";
	
	public GUI(String title) {
	    super(title);
	    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	    int frameWidth = 300; 
	    int frameHeight = 210;
	    setSize(frameWidth, frameHeight);
	    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (d.width - getSize().width) / 2;
	    int y = (d.height - getSize().height) / 2;
	    setLocation(x, y);
	    setResizable(false);
	    Container cp = getContentPane();
	    cp.setLayout(null);
	    cp.setBackground(new Color(43, 43, 43));
	    
	    textversion.setText("Current version:");
	    textversion.setForeground(new Color(255, 255, 255));
	    textversion.setBounds(10, 20, 100, 15);
	    cp.add(textversion);
	    
	    textnewversion.setText("Newest version:");
	    textnewversion.setForeground(new Color(255, 255, 255));
	    textnewversion.setBounds(10, 40, 100, 15);
	    cp.add(textnewversion);
	    
	    checkyCustomStyle.setText("Install new custom.styles (use with caution!)");
	    checkyCustomStyle.setBackground(new Color(43, 43, 43));
	    checkyCustomStyle.setForeground(new Color(255, 255, 255));
	    checkyCustomStyle.setBounds(10, 100, 300, 15);
	    checkyCustomStyle.addActionListener(this);
	    cp.add(checkyCustomStyle);
	    
	    doIt.setText("Update Metro for Steam now!");
	    doIt.setBackground(new Color(43, 43, 43));
	    doIt.setForeground(new Color(255, 255, 255));
	    doIt.setBounds(10, 130, 200, 40);
	    doIt.setContentAreaFilled(false);
	    doIt.addActionListener(this);
	    cp.add(doIt);
	    
	    if(System.getProperty("os.name").contains("Windows")) {
	        if(System.getenv("ProgramFiles(x86)") != null) {
	        	installPath = Advapi32Util.registryGetStringValue(HKEY_LOCAL_MACHINE, "Software\\Wow6432Node\\Valve\\Steam", "InstallPath");
	        } else {
	        	installPath = Advapi32Util.registryGetStringValue(HKEY_LOCAL_MACHINE, "Software\\Valve\\Steam", "InstallPath");
	        }
	    }
	    
	    try {
		    Scanner sc = new Scanner(new File(installPath + "/skins/Metro for Steam/resource/menus/steam.menu"));
		    
		    while (sc.hasNextLine()) {
		    	String str = sc.nextLine();
		    	if(str.contains("SkinVersion")) {
		    		version = str.substring(str.indexOf("Steam - ") + 8, str.indexOf("\"", str.indexOf("Steam - ") + 8));
		    	    sc.close();
		    		break;
		    	}
		    }
		    sc.close();
	    } catch(FileNotFoundException exc) {
	    	version = "None";
	    	checkyCustomStyle.setSelected(true);
	    }
	    
		versionL.setText(version);
		versionL.setForeground(new Color(255, 255, 255));
		versionL.setBounds(150, 20, 100, 15);
		cp.add(versionL);
	    
	    try {
		    URL url = new URL("http://fav.me/d4u3kjv");
		    URLConnection con = url.openConnection();
		    String page = IOUtils.toString(con.getInputStream(), "UTF-8");
		    newversion = page.substring(page.indexOf("Metro for Steam - ") + 18, page.indexOf(" by Bone", page.indexOf("Metro for Steam - ") + 18));
			newversionL.setText(newversion);
			newversionL.setForeground(new Color(255, 255, 255));
			newversionL.setBounds(150, 40, 100, 15);
			cp.add(newversionL);
		    
			if(newversion.equalsIgnoreCase(version)) {
				updateNotice.setForeground(new Color(130, 186, 0));
				updateNotice.setText("All good, you already have the newest version.");
			} else {
				updateNotice.setForeground(new Color(93, 178, 255));
				updateNotice.setText("New version available, you should  update.");
			}

		    updateNotice.setBounds(10, 60, 300, 15);
		    cp.add(updateNotice);
			
		    dl = page.substring(page.indexOf("http://www.deviantart.com/download/"), page.indexOf("\"", page.indexOf("http://www.deviantart.com/download/"))).replace("&amp;", "&");
		    for(int i = 0;; i++) {
		        if (con.getHeaderFieldKey(i) == null && con.getHeaderField(i) == null) {
		        	break;
		        }
		        if("Set-Cookie".equalsIgnoreCase(con.getHeaderFieldKey(i))) {
		        	cookie = con.getHeaderField(i);
		    	}
		    }
	    } catch(Exception exc) {
	    	exc.printStackTrace();
	    }
	    
	    setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == doIt) {
			try {
				updateNotice.setForeground(new Color(93, 178, 255));
				updateNotice.setText("Working...");
				Thread.sleep(1000);
				File dest = new File("mfsstuff/metroforsteam.zip");
			    URL url = new URL(dl);
			    URLConnection con = url.openConnection();
			    con.setRequestProperty("REFERER", "http://boneyardbrew.deviantart.com/art/Metro-for-Steam-3-8-Beta-4-292419787");
			    con.setRequestProperty("COOKIE", cookie);
			    FileUtils.copyInputStreamToFile(con.getInputStream(), dest);
			    
			    ZipFile zip = new ZipFile("mfsstuff/metroforsteam.zip");
			    zip.extractAll("mfsstuff");
			    if(!checkyCustomStyle.isSelected()) {
				    new File("mfsstuff/Metro for Steam/custom.styles").delete();
			    }
				FileUtils.copyDirectory(new File("mfsstuff/Metro for Steam"), new File(installPath + "/skins/Metro for Steam"));
			    FileUtils.deleteDirectory(new File("mfsstuff"));
			    updateNotice.setForeground(new Color(130, 186, 0));
			    updateNotice.setText("Done! Have fun!");
			} catch(Exception exc) {
				exc.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		new GUI("Metro for Steam updater");
	}
  
}
