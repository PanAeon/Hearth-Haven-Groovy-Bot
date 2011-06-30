/*     */ package haven;
/*     */ 
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.FocusEvent;
/*     */ import java.awt.event.FocusListener;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JRadioButton;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import javax.xml.parsers.SAXParser;
/*     */ import javax.xml.parsers.SAXParserFactory;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.XMLReader;
/*     */ import org.xml.sax.helpers.DefaultHandler;
/*     */ 
/*     */ public class CustomConfig
/*     */ {
/* 114 */   public static Coord invCoord = Coord.z;
/* 115 */   public static int sfxVol = 100;
/* 116 */   public static int musicVol = 100;
/* 117 */   public static List<CharData> characterList = new ArrayList();
/*     */   public static CharData activeCharacter;
/* 119 */   public static int wdgtID = 1000;
/* 120 */   public static boolean isMusicOn = false;
/* 121 */   public static boolean isSoundOn = false;
/* 122 */   public static boolean hasNightVision = true;
/* 123 */   public static boolean isSaveable = false;
/* 124 */   public static boolean noChars = true;
/*     */ 
/*     */   public static void setDefaults()
/*     */   {
/* 128 */     sfxVol = 100;
/* 129 */     musicVol = 100;
/* 130 */     isMusicOn = false;
/* 131 */     isSoundOn = false;
/* 132 */     hasNightVision = true;
/*     */   }
/*     */   public static boolean load() {
/* 135 */     setDefaults();
/* 136 */     BufferedReader reader = null;
/*     */     try {
/* 138 */       SAXParserFactory spFactory = SAXParserFactory.newInstance();
/* 139 */       SAXParser saxParser = spFactory.newSAXParser();
/*     */ 
/* 141 */       XMLReader xmlReader = saxParser.getXMLReader();
/* 142 */       xmlReader.setContentHandler(new DefaultHandler()
/*     */       {
/*     */         public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
/*     */           throws SAXException
/*     */         {
/* 147 */           String key = qName.toUpperCase().trim();
/*     */ 
/* 150 */           if (key.equals("SOUND")) {
/* 151 */             String value = atts.getValue("enabled") == null ? "true" : atts.getValue("enabled");
/* 152 */             CustomConfig.isSoundOn = Boolean.parseBoolean(value);
/*     */ 
/* 154 */             value = atts.getValue("volume") == null ? "100" : atts.getValue("volume");
/* 155 */             CustomConfig.sfxVol = Integer.parseInt(value);
/* 156 */           } else if (key.equals("MUSIC")) {
/* 157 */             String value = atts.getValue("enabled") == null ? "true" : atts.getValue("enabled");
/* 158 */             CustomConfig.isMusicOn = Boolean.parseBoolean(value);
/*     */ 
/* 160 */             value = atts.getValue("volume") == null ? "100" : atts.getValue("volume");
/* 161 */             CustomConfig.musicVol = Integer.parseInt(value);
/*     */           }
/*     */         }
/*     */       });
/* 165 */       if (ResCache.global != null)
/* 166 */         xmlReader.parse(new InputSource(ResCache.global.fetch("config.xml")));
/*     */       else {
/* 168 */         xmlReader.parse("config.xml");
/*     */       }
/*     */       return true;
/*     */     }
/*     */     catch (FileNotFoundException e)
/*     */     {
/* 172 */       System.out.println("Config file not found, creating a new one");
/*     */     } catch (IOException e) {
/* 174 */       e.printStackTrace();
/*     */     }
/*     */     catch (NullPointerException e) {
/* 177 */       System.out.println("File format corrupted, creating a new one");
/* 178 */       e.printStackTrace();
/*     */     }
/*     */     catch (NumberFormatException e) {
/* 181 */       System.out.println("Wrong config file format, creating a new one");
/*     */     } catch (ParserConfigurationException e) {
/* 183 */       e.printStackTrace();
/*     */     } catch (SAXException e) {
/* 185 */       e.printStackTrace();
/*     */     }
/*     */     finally {
/*     */       try {
/* 189 */         reader.close(); } catch (Exception e) {
/*     */       }
/*     */     }
/* 192 */     return false;
/*     */   }
/*     */ 
/*     */   public static double getSFXVolume() {
/* 196 */     return sfxVol / 100.0D;
/*     */   }
/*     */ 
/*     */   public static synchronized void saveSettings()
/*     */   {
/*     */     try
/*     */     {
/*     */       BufferedWriter writer;
/* 202 */       if (ResCache.global != null)
/* 203 */         writer = new BufferedWriter(new OutputStreamWriter(ResCache.global.store("config.xml")));
/*     */       else {
/* 205 */         writer = new BufferedWriter(new FileWriter(new File("config.xml")));
/*     */       }
/* 207 */       writer.write("<?xml version=\"1.0\" ?>\n");
/* 208 */       writer.write("<CONFIG>\n");
/* 209 */       writer.write("\t<SOUND enabled=\"" + Boolean.toString(isSoundOn) + "\" volume=\"" + Integer.toString(sfxVol) + "\"/>\n");
/*     */ 
/* 211 */       writer.write("\t<MUSIC enabled=\"" + Boolean.toString(isMusicOn) + "\" volume=\"" + Integer.toString(musicVol) + "\"/>\n");
/*     */ 
/* 214 */       writer.write("</CONFIG>");
/* 215 */       writer.close();
/*     */     }
/*     */     catch (IOException e) {
/* 218 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) {
/* 223 */     if (!load())
/*     */     {
/* 225 */       setDefaults();
/* 226 */       JFrame configFrame = new JFrame("Screen Size");
/* 227 */       Container contentPane = configFrame.getContentPane();
/* 228 */       JPanel clientSettingsPanel = new JPanel(new GridBagLayout(), true);
/* 229 */       JButton startBtn = new JButton("Start!");
/*     */ 
/* 231 */       FilteredTextField xField = new FilteredTextField();
/* 232 */       FilteredTextField yField = new FilteredTextField();
/* 233 */       JRadioButton typeStandard = new JRadioButton("Standard resolution:", true);
/* 234 */       JRadioButton typeCustom = new JRadioButton("Custom resolution:", false);
/* 235 */       JComboBox stdRes = new JComboBox(new Coord[] { new Coord(800, 600), new Coord(1024, 768), new Coord(1280, 720), new Coord(1280, 768), new Coord(1280, 800) });
/*     */ 
/* 243 */       configFrame.setDefaultCloseOperation(3);
/*     */ 
/* 245 */       stdRes.setSelectedIndex(0);
/* 246 */       stdRes.setEditable(false);
/*     */ 
/* 248 */       xField.setNoLetters(true);
/* 249 */       yField.setNoLetters(true);
/* 250 */       xField.setColumns(4);
/* 251 */       yField.setColumns(4);
/* 252 */       xField.setText("800");
/* 253 */       yField.setText("600");
/* 254 */       xField.setEditable(false);
/* 255 */       yField.setEditable(false);
/*     */ 
/* 257 */       contentPane.setLayout(new GridBagLayout());
/*     */ 
/* 260 */       GridBagConstraints constraints = new GridBagConstraints();
/* 261 */       constraints.anchor = 17;
/*     */ 
/* 263 */       constraints.gridx = 0;
/* 264 */       constraints.gridy = 0;
/* 265 */       clientSettingsPanel.add(typeStandard, constraints);
/*     */ 
/* 267 */       constraints.gridx = 1;
/* 268 */       constraints.gridwidth = 2;
/* 269 */       clientSettingsPanel.add(stdRes, constraints);
/*     */ 
/* 271 */       constraints.gridx = 0;
/* 272 */       constraints.gridy = 1;
/* 273 */       constraints.gridwidth = 1;
/* 274 */       clientSettingsPanel.add(typeCustom, constraints);
/*     */ 
/* 276 */       constraints.gridx = 1;
/* 277 */       clientSettingsPanel.add(xField, constraints);
/*     */ 
/* 279 */       constraints.gridx = 2;
/* 280 */       clientSettingsPanel.add(yField, constraints);
/*     */ 
/* 283 */       constraints.anchor = 11;
/* 284 */       constraints.gridx = 0;
/* 285 */       constraints.gridy = 0;
/* 286 */       contentPane.add(clientSettingsPanel, constraints);
/*     */ 
/* 288 */       constraints.gridx = 2;
/* 289 */       constraints.gridy = 2;
/* 290 */       constraints.insets.top = 10;
/* 291 */       clientSettingsPanel.add(startBtn, constraints);
/*     */ 
/* 293 */       constraints.gridx = 1;
/* 294 */       constraints.gridy = 3;
/* 295 */       constraints.insets.top = 0;
/*     */ 
///* 297 */       typeStandard.addChangeListener(new ChangeListener(typeStandard, typeCustom, stdRes)
///*     */       {
///*     */         public void stateChanged(ChangeEvent e) {
///* 300 */           if ((!this.val$typeStandard.isSelected()) && (!this.val$typeCustom.isSelected()))
///*     */           {
///* 302 */             this.val$typeStandard.setSelected(true);
///*     */           }
///* 304 */           if (this.val$typeStandard.isSelected())
///*     */           {
///* 306 */             this.val$stdRes.setEnabled(true);
///* 307 */             this.val$typeCustom.setSelected(false);
///*     */           }
///*     */           else {
///* 310 */             this.val$stdRes.setEnabled(false);
///*     */           }
///*     */         }
///*     */       });
///* 314 */       typeCustom.addChangeListener(new ChangeListener(typeStandard, typeCustom, xField, yField, stdRes)
///*     */       {
///*     */         public void stateChanged(ChangeEvent e) {
///* 317 */           if ((!this.val$typeStandard.isSelected()) && (!this.val$typeCustom.isSelected()))
///*     */           {
///* 319 */             this.val$typeCustom.setSelected(true);
///*     */           }
///* 321 */           if (this.val$typeCustom.isSelected())
///*     */           {
///* 323 */             this.val$xField.enable();
///* 324 */             this.val$yField.enable();
///* 325 */             this.val$xField.setEditable(true);
///* 326 */             this.val$yField.setEditable(true);
///* 327 */             this.val$typeStandard.setSelected(false);
///* 328 */             this.val$stdRes.disable();
///*     */           }
///*     */           else {
///* 331 */             this.val$xField.setEditable(false);
///* 332 */             this.val$yField.setEditable(false);
///* 333 */             this.val$xField.disable();
///* 334 */             this.val$yField.disable();
///* 335 */             this.val$stdRes.enable();
///*     */           }
///*     */         }
///*     */       });
///* 339 */       xField.addFocusListener(new FocusListener(xField) {
///*     */         public void focusGained(FocusEvent e) {
///*     */         }
///*     */         public void focusLost(FocusEvent e) {
///*     */           try {
///* 344 */             if (Integer.parseInt(this.val$xField.getText()) < 800)
///*     */             {
///* 346 */               this.val$xField.setText("800");
///*     */             }
///*     */           }
///*     */           catch (NumberFormatException NFExcep) {
///* 350 */             this.val$xField.setText("800");
///*     */           }
///*     */         }
///*     */       });
///* 354 */       yField.addFocusListener(new FocusListener(yField) {
///*     */         public void focusGained(FocusEvent e) {
///*     */         }
///*     */         public void focusLost(FocusEvent e) {
///*     */           try {
///* 359 */             if (Integer.parseInt(this.val$yField.getText()) < 600)
///*     */             {
///* 361 */               this.val$yField.setText("600");
///*     */             }
///*     */           }
///*     */           catch (NumberFormatException NFExcep) {
///* 365 */             this.val$yField.setText("600");
///*     */           }
///*     */         }
///*     */       });
///* 369 */       startBtn.addActionListener(new ActionListener(args, configFrame)
///*     */       {
///*     */         public void actionPerformed(ActionEvent e) {
///* 372 */           CustomConfig.saveSettings();
///* 373 */           Thread mainThread = new Thread()
///*     */           {
///*     */             public void run() {
///* 376 */               MainFrame.main(CustomConfig.6.this.val$args);
///*     */             }
///*     */           };
///* 379 */           mainThread.start();
///* 380 */           this.val$configFrame.dispose();
///*     */         }
///*     */       });
/* 383 */       configFrame.pack();
/* 384 */       Toolkit toolkit = Toolkit.getDefaultToolkit();
/* 385 */       configFrame.setLocation((int)(toolkit.getScreenSize().getWidth() - configFrame.getWidth()) / 2, (int)(toolkit.getScreenSize().getHeight() - configFrame.getHeight()) / 2);
/*     */ 
/* 387 */       configFrame.setVisible(true);
/*     */     }
/*     */     else {
/* 390 */       saveSettings();
/* 391 */       MainFrame.main(args);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class CharData
/*     */   {
/*     */     String name;
/* 102 */     int hudActiveBelt = 1;
/* 103 */     String[][] hudBelt = new String[10][10];
/*     */ 
/*     */     public CharData(String name)
/*     */     {
/* 107 */       this.name = name;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 111 */       return "Name=\"" + this.name + "\"";
/*     */     }
/*     */   }
/*     */ 
/*     */   static class FilteredTextField extends JTextField
/*     */   {
/*  44 */     String defbadchars = "`~!@#$%^&*()_-+=\\|\"':;?/>.<, ";
/*  45 */     String badchars = this.defbadchars;
/*  46 */     boolean noLetters = false;
/*  47 */     boolean noNumbers = false;
/*  48 */     int maxCharacters = 0;
/*     */ 
/*     */     public void processKeyEvent(KeyEvent ev)
/*     */     {
/*  52 */       char c = ev.getKeyChar();
/*     */ 
/*  54 */       if (((Character.isDigit(c)) && (this.noNumbers) && (!ev.isAltDown())) || (this.badchars.indexOf(c) > -1))
/*     */       {
/*  56 */         ev.consume();
/*  57 */         return;
/*     */       }
/*  59 */       if (((Character.isLetter(c)) && (this.noLetters) && (!ev.isAltDown())) || (this.badchars.indexOf(c) > -1))
/*     */       {
/*  61 */         ev.consume();
/*  62 */         return;
/*     */       }
/*  64 */       if ((getText().length() >= this.maxCharacters) && (this.maxCharacters > 0) && (ev.getKeyCode() != 8) && (ev.getKeyCode() != 37) && (ev.getKeyCode() != 39) && (ev.getKeyCode() != 36) && (ev.getKeyCode() != 35))
/*     */       {
/*  72 */         ev.consume();
/*  73 */         return;
/*     */       }
/*  75 */       super.processKeyEvent(ev);
/*     */     }
/*     */ 
/*     */     public void setBadChars(String badchars)
/*     */     {
/*  80 */       this.badchars = badchars;
/*     */     }
/*     */ 
/*     */     public void setMaxCharacters(int maxChars) {
/*  84 */       this.maxCharacters = maxChars;
/*     */     }
/*     */ 
/*     */     public void setDefaultBadChars() {
/*  88 */       this.badchars = this.defbadchars;
/*     */     }
/*     */ 
/*     */     public void setNoNumbers(boolean state) {
/*  92 */       this.noNumbers = state;
/*     */     }
/*     */ 
/*     */     public void setNoLetters(boolean state) {
/*  96 */       this.noLetters = state;
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.CustomConfig
 * JD-Core Version:    0.6.0
 */