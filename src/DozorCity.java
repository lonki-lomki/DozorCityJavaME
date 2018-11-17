import java.io.InputStream;

import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;


public class DozorCity extends MIDlet implements CommandListener {
	
	static final Command UPDATE_CMD = new Command("Update", Command.ITEM, 1);
    static final Command EXIT_CMD = new Command("Exit", Command.EXIT, 1);

	
	private Form mMainForm;
	Display display;
    ChoiceGroup stopCg;
    String[] busStops;
    ChoiceGroup tablo;
    Font fnt = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
    boolean flagDataReady = false;

  
  public DozorCity() {
  	  display = Display.getDisplay(this);
  	  mMainForm = new Form("Welcome to DozorCity");
  	  mMainForm.addCommand(UPDATE_CMD);
  	  mMainForm.addCommand(EXIT_CMD);
  	  mMainForm.setCommandListener(this);
  	  
  	  stopCg = new ChoiceGroup("Choose a bus stop", Choice.POPUP);
  	  mMainForm.append(stopCg);
  	  
  	  busStops = new String[3];
  	  busStops[0] = "One";
  	  busStops[1] = "Two";
  	  busStops[2] = "Three";
  	  
  	  for(int i = 0; i < busStops.length; i++) {
  	  	  stopCg.append(busStops[i], null);
  	  }
  	  
  	  tablo = new ChoiceGroup(busStops[0]+": Bus Stop Tablo", Choice.EXCLUSIVE);
  	  tablo.setLayout(Item.LAYOUT_EXPAND);
  	  mMainForm.append(tablo);
  	  
  	  mMainForm.setItemStateListener(new ItemStateListener() {
  	  		  public void itemStateChanged(Item item) {
  	  		  	  if (item instanceof ChoiceGroup) {
  	  		  	  	  ChoiceGroup obj = (ChoiceGroup)item;
  	  		  	  	  if (obj == stopCg) {
  	  		  	  	  	  int idx = obj.getSelectedIndex();
  	  		  	  	  	  String busStop = busStops[idx];
  	  		  	  	  	  tablo.setLabel(busStop+": Bus Stop Tablo");
  	  		  	  	  }
  	  		  	  }
  	  		  }
  	  });
  	  
  }
  
  public void startApp() {
  	  display.setCurrent(mMainForm);
  }
  
  public void pauseApp() {}
  
  public void destroyApp(boolean unconditional) {}
  
  public void commandAction(Command c, Displayable s) {
  	  if (s instanceof Form) {
  	  	  Form obj = (Form)s;
  	  	  if (obj == mMainForm) {
  	  	  	  if (c == UPDATE_CMD) {
	  	  	  	  //System.out.println("Update pressed!!!");
  	  	  	  	  updateTransportTablo();
  	  	  	  } else if (c == EXIT_CMD) {
  	  	  	  	  notifyDestroyed();
  	  	  	  }
  	  	  }
  	  }
  }
  
  void updateTransportTablo() {
  	  int idx = stopCg.getSelectedIndex();
  	  
  /*	  
  	  if (idx == 0) {
  	  }
  */
  	  
  	  Thread httpThread = new Thread(new HttpTask(this, "http://paralitix.in.ua/test.json"));
  	  httpThread.start();
  	  
  	  try {
  	  	  Thread.sleep(500);
  	  } catch (Exception ex) {
  	  	  ex.printStackTrace();
  	  }
  	  
  }
  
  String getJSONfromURL(String url) {
  	  HttpConnection conn = null;
      InputStream input = null;
  	  String method = HttpConnection.GET;
  	  StringBuffer buf = new StringBuffer();
  	  
  	  try {
  	  	  conn = (HttpConnection)Connector.open(url);
  	  	  conn.setRequestMethod(method);
  	  	  conn.setRequestProperty("User-Agent", "Profile/MIDP-1.0 Confirguration/CLDC-1.0");
  	  	  //setConfig(conn);	// Set UserAgent, Content-Language
  	  	  
  	  	  int respCode = conn.getResponseCode();
  	  	  
  	  	  System.out.println("respCode: " + respCode);
  	  	  
  	  	  if (respCode == conn.HTTP_OK) {
  	  	  	  input = conn.openInputStream();
  	  	  	  int chr;
  	  	  	  while((chr = input.read()) != -1) {
  	  	  	  	  buf.append((char) chr);
  	  	  	  }
  	  	  }
      } catch (OutOfMemoryError mem) {
      	  mem.printStackTrace();
      } catch (Exception ex) {
      	  ex.printStackTrace();
      }
      
      try {
      	  if (input != null) {
      	  	  input.close();
      	  }
      	  if (conn != null) {
      	  	  conn.close();
      	  }
      } catch (Exception ex) {
      	  ex.printStackTrace();
      }
      
      if (buf.length() > 0) {
      	  return buf.toString();
      }
      
  	  return null;
  }
  
  /**
   * Callback method to display received information
   */
  public void callback(String value) {
  	  flagDataReady = true;
  	  System.out.println("Returned from thread: " + value);
  	  
  	  int idx_a1 = value.indexOf("\"a1\"");
  	  int idx_a2 = value.indexOf("\"a2\"");
  	  int idx_a3 = value.indexOf("\"a3\"");
  	  System.out.println("a1:" + idx_a1 + " a2:" + idx_a2 + " a3:" + idx_a3);

  	  int a1_begin = value.indexOf('[', idx_a1);
  	  int a1_end = value.indexOf(']', a1_begin);
  	  int a2_begin = value.indexOf('[', idx_a2);
  	  int a2_end = value.indexOf(']', a2_begin);
  	  int a3_begin = value.indexOf('[', idx_a3);
  	  int a3_end = value.indexOf(']', a3_begin);
  	  System.out.println("a1:" + value.substring(a1_begin+1, a1_end));
  	  System.out.println("a2:" + value.substring(a2_begin+1, a2_end));
  	  System.out.println("a3:" + value.substring(a3_begin+1, a3_end));

  	  // Clear information from display
  	  tablo.deleteAll();
  	  
  	  parseTransport("A", value.substring(a1_begin+1, a1_end));		// Parse bus (a1 array)
  	  parseTransport("T", value.substring(a2_begin+1, a2_end));	    // Parse trolleybus (a2 array)
  	  parseTransport("Tv", value.substring(a3_begin+1, a3_end));	// Parse trams (a3 array)
  	  
   	  // Set font for each row
  	  for (int i=0; i<tablo.size(); i++) {
	  	  tablo.setFont(i, this.fnt);
  	  }

  }
  
  /**
   * Parse data and send result to screen (ChoiceGroup tablo)
   * @param TrType transport type (Bus, Trolleybus, Tram)
   * @param info input JSON string
   */
  void parseTransport(String trType, String info) {
  	  if (info.length() <= 3) {
  	  	  return;
  	  }
  	  String tmp = new String(info);
  	  int cnt = 0;
  	  // Parse loop
  	  while(true) {
  	  	  if (tmp.length() <= 3) {
  	  	  	  break;
  	  	  }
  	  	  int idx = tmp.indexOf('}');
  	  	  if (idx <= 3) {
  	  	  	  break;
  	  	  }
  	  	  String obj = tmp.substring(1, idx);
  	  	  Transport tr = new Transport(obj);
  	  	  //System.out.println("obj:" + obj);
  	  	  //System.out.println("Transport:" + tr.toString());
  	  	  
  	  	  // Align route and time
  	  	  String route = trType + tr.rId;
  	  	  String time = "" + tr.t;
  	  	  StringBuffer sb = new StringBuffer(route);
  	  	  for (int i=0; i<(10-route.length()-time.length()); i++) {
  	  	  	  sb.append(" ");
  	  	  }
  	  	  sb.append(time);
  	  	  
  	  	  // Add values to tablo
  	  	  tablo.append(sb.toString(), null);
  	  	  System.out.println("To tablo: " + sb.toString());
  	  	  cnt++;
  	  	  
  	  	  idx = tmp.indexOf('{', 1);
  	  	  if (idx < 0) {
  	  	  	  break;
  	  	  }
  	  	  tmp = new String(tmp.substring(idx));
  	  }

  }
  
  
  // C L A S S
  public class HttpTask implements Runnable {
  	  
  	  DozorCity dc;
  	  String url;
  	  
  	  public HttpTask(DozorCity obj, String url) {
  	  	  this.dc  = obj;
  	  	  this.url = url;
  	  }
  	  
  	  public void run() {
  	  	  String str = getJSONfromURL(url);
  	  	  dc.callback(str);
  	  }
  }
  
  public class Transport {
  	  public int rId;
  	  public int dId;
  	  public int t;
  	  
  	  // Construct by components 
  	  public Transport(int rId, int dId, int t) {
  	  	  this.rId = rId;
  	  	  this.dId = dId;
  	  	  this.t = t;
  	  }
  	  
  	  // Construct by JSON object
  	  public Transport(String obj) {
  	  	  // >>> next object:"rId":1364,"dId":7742,"t":9
  	  	  String[] arr = split(obj, ',');
  	  	  for(int i=0; i<arr.length; i++) {
  	  	  	  String[] arr2 = split(arr[i], ':');
  	  	  	  if ("\"rId\"".equals(arr2[0])) {
  	  	  	  	  this.rId = Integer.parseInt(arr2[1]); 
  	  	  	  }
  	  	  	  if ("\"dId\"".equals(arr2[0])) {
  	  	  	  	  this.dId = Integer.parseInt(arr2[1]); 
  	  	  	  }
  	  	  	  if ("\"t\"".equals(arr2[0])) {
  	  	  	  	  this.t = Integer.parseInt(arr2[1]); 
  	  	  	  }
  	  	  }
  	  }
  	  
  	  /**
  	   * Split string to array by delimiter 
  	   */
  	  private String[] split(String str, char delim) {
  	  	  StringBuffer sb = new StringBuffer();
  	  	  java.util.Vector v = new java.util.Vector();
  	  	  for (int i=0; i<str.length(); i++) {
  	  	  	  if (str.charAt(i) == delim) {
  	  	  	  	  // Split. Add to Vector
  	  	  	  	  v.addElement(sb.toString());
  	  	  	  	  // Empty string
  	  	  	  	  sb.setLength(0);
  	  	  	  } else {
  	  	  	  	  // Accumulate
  	  	  	  	  sb.append(str.charAt(i));
  	  	  	  }
  	  	  }
  	  	  // Add rest of string, if...
  	  	  if (sb.length() > 0) {
  	  	  	  v.addElement(sb.toString());
  	  	  }
  	  	  
  	  	  String[] result = new String[v.size()];
  	  	  for (int i=0; i<v.size(); i++) {
  	  	  	  result[i] = (String) v.elementAt(i);
  	  	  }
  	  	  return result;
  	  }
  	  
  	  public String toString() {
  	  	  return "rId:" + this.rId + " dId:" + this.dId + " t:" + this.t;
  	  }
  	  
  }
  
}
