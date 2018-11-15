import java.io.InputStream;

import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

//import org.json.me.*;

public class DozorCity extends MIDlet implements CommandListener {
	
	static final Command UPDATE_CMD = new Command("Update", Command.ITEM, 1);
    static final Command EXIT_CMD = new Command("Exit", Command.EXIT, 1);

	
	private Form mMainForm;
	Display display;
    ChoiceGroup stopCg;
    String[] busStops;
    ChoiceGroup tablo;
    Font fnt = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
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
  	  
  	  Thread httpThread = new Thread(new HttpTask(this, "http://paralitix.in.ua/test.json"));
  	  httpThread.start();
  	  
  	  try {
  	  	  Thread.sleep(500);
  	  } catch (Exception ex) {
  	  	  ex.printStackTrace();
  	  }
  	  
  	  
  	  if (idx == 0) {
  	  	  tablo.deleteAll();
  	  	  tablo.append("A7       3", null);
  	  	  tablo.append("T4       4", null);
  	  	  tablo.append("A42      9", null);
  	  	  tablo.append("A37     10", null);
  	  	  tablo.append("T9      11", null);
  	  	  tablo.setFont(0, fnt);
  	  	  tablo.setFont(1, fnt);
  	  	  tablo.setFont(2, fnt);
  	  	  tablo.setFont(3, fnt);
  	  	  tablo.setFont(4, fnt);
  	  }
  	  if (idx == 1) {
  	  	  tablo.deleteAll();
  	  	  tablo.append("A7       1", null);
  	  	  tablo.append("T4       2", null);
  	  	  tablo.append("A42      3", null);
  	  	  tablo.setFont(0, fnt);
  	  	  tablo.setFont(1, fnt);
  	  	  tablo.setFont(2, fnt);
  	  }
  	  if (idx == 2) {
  	  	  tablo.deleteAll();
  	  	  tablo.append("A7       5", null);
  	  	  tablo.append("A42      7", null);
  	  	  tablo.setFont(0, fnt);
  	  	  tablo.setFont(1, fnt);
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
  	  	  
          //System.out.println(buf.toString());
  	  	  
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
  
  public void callback(String value) {
  	  flagDataReady = true;
  	  System.out.println("Returned from thread: " + value);
  	  /*
  	  try {
  	  	  JSONObject jo = new JSONObject(value);
  	  	  System.out.println("jo:"+jo.toString());
  	  	  JSONObject data = (JSONObject) jo.opt("data");
  	  	  System.out.println("data:"+data.toString());
  	  	  JSONArray a1 = (JSONArray) data.opt("a1");
  	  	  System.out.println("a1:"+a1.toString());
  	  	  JSONArray a2 = (JSONArray) data.opt("a2");
  	  	  System.out.println("a2:"+a2.toString());
  	  } catch (JSONException ex) {
  	  	  ex.printStackTrace();
  	  }
  	  */
  	  int idx_a1 = value.indexOf("\"a1\"");
  	  int idx_a2 = value.indexOf("\"a2\"");
  	  int idx_a3 = value.indexOf("\"a3\"");
  	  System.out.println("a1:" + idx_a1 + " a2:" + idx_a2 + " a3:" + idx_a3);
  	  /*
  	  String str_a1 = value.substring(idx_a1+6, idx_a2-2);
  	  String str_a2 = value.substring(idx_a2+6, idx_a3-2);
  	  System.out.println("str_a1:" + str_a1);
  	  System.out.println("str_a2:" + str_a2);
  	  */
  	  int a1_begin = value.indexOf('[', idx_a1);
  	  int a1_end = value.indexOf(']', a1_begin);
  	  int a2_begin = value.indexOf('[', idx_a2);
  	  int a2_end = value.indexOf(']', a2_begin);
  	  int a3_begin = value.indexOf('[', idx_a3);
  	  int a3_end = value.indexOf(']', a3_begin);
  	  System.out.println("a1:" + value.substring(a1_begin+1, a1_end));
  	  System.out.println("a2:" + value.substring(a2_begin+1, a2_end));
  	  System.out.println("a3:" + value.substring(a3_begin+1, a3_end));
  	  
  	  parseTransport("B", value.substring(a1_begin+1, a1_end));		// Parse bus (a1 array)
  	  parseTransport("Tb", value.substring(a2_begin+1, a2_end));	// Parse trolleybus (a2 array)
  	  parseTransport("Tr", value.substring(a3_begin+1, a3_end));	// Parse trams (a3 array)
  }
  
  void parseTransport(String TrType, String info) {
  	  if (info.length() <= 3) {
  	  	  return;
  	  }
  	  String tmp = new String(info);
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
  	  	  System.out.println("obj:" + obj);
  	  	  System.out.println("Transport:" + tr.toString());
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
  	  	  String[] arr = split(obj, ",");
  	  	  for(int i=0; i<arr.length; i++) {
  	  	  	  String[] arr2 = split(arr[i], ";");
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
  	  	  /*
  	  	  StringTokenizer token = new StringTokenizer(str, delim);
  	  	  String[] result = new String[token.countTokens()];
  	  	  int i = 0;
  	  	  while (token.hasMoreTokens()) {
  	  	  	  result[i++] = token.nextToken(); 
  	  	  }
  	  	  return result;
  	  	  */
  	  	  StringBuilder sb = new StringBuilder();
  	  	  Vector v = new Vector();
  	  	  for(int i=0; i<str.length(); i++) {
  	  	  	  if (str.charAt(i) == delim) {
  	  	  	  	  // Split. Add to Vector
  	  	  	  } else {
  	  	  	  	  // Accumulate
  	  	  	  }
  	  	  }
  	  	  
  	  }
  	  
  	  public String toString() {
  	  	  return "rId:" + this.rId + " dId:" + this.dId + " t:" + this.t;
  	  }
  	  
  	  
  }
  
  
}
