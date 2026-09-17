package gui.model;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.jfree.data.xy.XYSeries;
import org.w3c.dom.css.RGBColor;

import gui.component.LowFreqOptions;
import gui.component.TopicCommandValue;
import gui.component.XYSeriesGraph;
import gui.component.XYSeriesHighFrequency;

/**
 * This class is the controlling class for the application. It contains the main method
 * for launching the Swing GUI and itcontrols the MQTT connection.
 */
public class MQTTFrame implements ActionListener, MqttCallback, Runnable {
	private JPanel connPanel = new JPanel();
	private JPanel pubPanel = new JPanel();
	private JPanel subPanel = new JPanel();
	private JPanel emergencyControlPanel = new JPanel();
	private PubPanel pubPanelContr;
	private SubPanel subPanelContr;
	private JPanel westPanel = new JPanel();

	// Main Components
	private ConnOpts   optionsComp = null;
	private MQTTHist historyComp = null;
	private JPanel     mqttComp = null;

	private JComboBox ipAddress;
	private JComboBox port;
	private LED       led;
	private JButton   connect;
	private JButton   disconnect;

	private JButton   emergencyStopButton;
	private MqttClient mqtt = null;
	private MqttConnectOptions opts = null;
	private boolean connected = false;
	private boolean traceEnabled = false;
	private final Object    connLostWait = new Object(); // Object to coordinated ConnectionLost and disconnect threads if
	// disconnect is hit during connectionLost
	private JFrame frame= null;

	public static JFrame theFrame = null;
	public int time;
	public int val;
	double timeRefSec;
	double timeRefNanoSec;

	String[] lowFreFileFormatArray = new String[17];


	private JPanel pnGraph = new JPanel(new GridBagLayout());
	private JPanel modeOnePanel = new JPanel( new BorderLayout());
	public XYSeriesGraph topLeft = new XYSeriesGraph("Top Left", "Time (s)", "Y-Axis", this);
	public XYSeriesGraph bottomLeft = new XYSeriesGraph("Bottom Left", "Time (s)", "Y-Axis", this);
	public XYSeriesGraph topRight = new XYSeriesGraph("Top Right", "Time (s)", "Y-Axis", this);
	public XYSeriesGraph bottomRight = new XYSeriesGraph("Bottom Right", "Time (s)", "Y-Axis", this );
	XYSeriesHighFrequency highFrequencyGraph = new XYSeriesHighFrequency("High-frequency Data Acquisition", "Time (ms)", "Values", this);
	//	XYSeriesGraph highFrequencyGraph = new XYSeriesGraph("High-frequency Data Acquisition", "Time (s)", "Y-Axis",  "4.1", "4.2", "4.3", "4.4", this );
	public TopicCommandValue topicCommandValue = new TopicCommandValue(this);

	public static double currentTime;
	public static int countNumDataHighFrequency; 

	/**
	 *  Constant controlling the display of JTextFields
	 */
	protected static final Dimension TEXT_FIELD_DIMENSION = new Dimension( 1000, 20 );
	/**
	 *  Constant controlling the display of JComboBoxes
	 */
	protected static final Dimension DROP_DOWN_DIMENSION = new Dimension(35, 20);
	protected static final Insets TEXT_MARGINS = new Insets(3,3,3,3);
	protected static final int FRAME_WIDTH = 800;//375
	protected static final int FRAME_HEIGHT = 500;//450

	// The name of the properties file
	private final static String PROP_FILE = "mqtt.properties";
	private final static String PROP_DELIM = ";";

	// Other constants
	private static final String DEFAULT_IP_ADDRESS = "127.0.0.1"; //local host
	private static final String DEFAULT_PORT_NUMBER = "1883";
	/**
	 * Constructor for MQTTFrame
	 */
	public MQTTFrame() {		
		super();
	}

	/**
	 * The main method for launching the GUI. The main method takes no arguments.
	 * <BR>
	 * This method attaches a WindowListener to the window to detect windowClosing events. When
	 * such an event is detected then various parameters are gathered from the GUI and written
	 * to a properties file before the application exits.
	 * @param args No arguments are required.
	 */
	public static void main(String[] args) {
		final MQTTFrame view = new MQTTFrame();

		theFrame = view.getJFrame();
		theFrame.setSize( MQTTFrame.FRAME_WIDTH, MQTTFrame.FRAME_HEIGHT );
		theFrame.setLocationRelativeTo(null);
		theFrame.setResizable(true);

		view.init( theFrame.getContentPane() );

		//		view.highFrequencyGraph.goals.add(0,0);
		//		view.highFrequencyGraph.goals.add(1,1);
		System.out.println("Hello");

		theFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// Don't cleanly disconnect when the window is closed.
				// This allows things like Last Will & Testament to be easily tested by
				// closing the window to unexpectedly terminate the MQTT session.
				//view.disconnect();

				// Write the properties to disk before exiting
				FileOutputStream propFile = null;
				Properties props = new Properties();

				try {
					propFile = new FileOutputStream( PROP_FILE );

					// First populate the properties object
					// IPADDRESS
					props.setProperty("IPAddressList", view.constructPropertyValue("IPAddressList") );

					// IPPORT
					props.setProperty("IPPortList", view.constructPropertyValue("IPPortList") );

					// CLIENTID
					props.setProperty("ClientId", view.constructPropertyValue("ClientId") );

					// PERSISTENCE ENABLED
					props.setProperty("Persistence", view.constructPropertyValue("Persistence") );

					// PERSISTENCE DIRECTORY
					props.setProperty("PersistenceDir", view.constructPropertyValue("PersistenceDir") );

					// Now write to disk
					props.store( propFile, "MQTT Utility properties" );

				} catch(Exception fe) {
					// If we cannot create a properties file then don't worry
				}	  

				System.exit(0);
			}
		});

	}

	/**
	 * @return This MQTTFrame's JFrame object reference.
	 */
	private JFrame getJFrame() {
		if ( frame == null ) {
			frame = new JFrame();
		}
		return frame;
	}

	/**
	 * This method builds all the components to add to the
	 * content pane of the JFrame window. It builds the connect panel itself and delegates building
	 * of the publish and subscribe panels to the PubPanel and SubPanel objects respectively.
	 * It also loads the properties file and gives the window a Windows look and feel.
	 * @param contentPane - The content pane of the JFrame window.
	 */
	protected void init( Container contentPane ) {
		FileInputStream propFile = null;
		Properties      props = new Properties();

		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			//com.sun.java.swing.plaf.windows.WindowsLookAndFeel //javax.swing.plaf.metal.MetalLookAndFeel* //javax.swing.plaf.nimbus.NimbusLookAndFeel
		} catch ( Exception ex) {
			// Don't worry if we can't set the look and feel
		}	

		// Does a properties file exist from which the GUI can be populated?
		try {
			propFile = new FileInputStream( PROP_FILE );
			props.load( propFile );
		} catch(Exception fe) {
			// If we can't find a properties file then don't worry
			propFile = null;
		}	  

		// Now build the GUI components
		setTitleText("");        

		connPanel.setLayout(new GridLayout(2, 1));
		connPanel.setBorder( new EtchedBorder() );

		// Build the main components to add to the tabbed pane.
		mqttComp = new JPanel( new BorderLayout() );
		historyComp = new MQTTHist( getJFrame(), optionsComp );

		// Add the panel which handles connecting and disconnecting from the broker
		westPanel.setLayout(new BorderLayout());
		westPanel.add(connPanel, BorderLayout.NORTH);

		// Add the panels for publish and subscribe to a JSplitPane
		// The JSplitPane allows the panels to be resized evenly, allows the user to resize the 
		// panels manually and provides a good layout for two similar panels
		JSplitPane pubsub = new JSplitPane( JSplitPane.VERTICAL_SPLIT, true,
				subPanel, pubPanel );
		pubsub.setOneTouchExpandable(true); // Allow either panel to be expanded to full size easily
		pubsub.setDividerSize(10);           // Set the divider size large enough to display the one touch expandable arrows
		pubsub.setResizeWeight(0.5);         // Resize evenly

		emergencyControlPanel.setLayout(new BorderLayout());
		emergencyControlPanel.setBorder(new EtchedBorder() );
		//		emergencyControlPanel.setBackground(new Color (177,18,38)); //this helps make the button red
		emergencyStopButton = new JButton("Emergency Stop");
		emergencyStopButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					System.out.println("Emergency Button is clicked");
					String topic = "Emergency";
					String message = "EST0000";
					publish(topic, message.getBytes(), 0, false);
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		});

		emergencyStopButton.setForeground(new Color(177,18,38));
		emergencyStopButton.setBackground(new Color (177,18,38));
		emergencyStopButton.setContentAreaFilled(true);
		emergencyStopButton.setFont(new Font("Times New Roman", Font.BOLD, 18));
		emergencyControlPanel.add(emergencyStopButton, BorderLayout.SOUTH);
		emergencyControlPanel.add(topicCommandValue.overall, BorderLayout.CENTER);

		JSplitPane pubsubAndButton = new JSplitPane( JSplitPane.VERTICAL_SPLIT, true, pubsub, emergencyControlPanel);
		westPanel.add(pubsubAndButton, BorderLayout.CENTER);
		pubsubAndButton.setOneTouchExpandable(true); // Allow either panel to be expanded to full size easily
		pubsubAndButton.setDividerSize(10);           // Set the divider size large enough to display the one touch expandable arrows
		pubsubAndButton.setResizeWeight(1);

		//Add Tabs
		JTabbedPane tabbedPane = new JTabbedPane();

		//Add graphs to the tab panel
		modeOnePanel.add(highFrequencyGraph.graphPanel, BorderLayout.CENTER);
		tabbedPane.addTab("Mode 1", modeOnePanel);
		tabbedPane.addTab("Mode 2", pnGraph);

		GridBagConstraints c = new GridBagConstraints();

		//natural height, maximum width
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.5;
		c.weighty = 0.5;
		c.ipadx = 0;
		c.ipady = 0;

		//c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0; 
		pnGraph.add(topLeft.tabbedPane, c);

		c.fill = GridBagConstraints.BOTH;
		//c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 0;
		pnGraph.add(topRight.tabbedPane, c);

		c.fill = GridBagConstraints.BOTH;
		//c.weighty = 1.0; // request any extra vertical space
		//c.weightx = 0;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		pnGraph.add(bottomLeft.tabbedPane, c);

		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.PAGE_END; //bottom of space
		//c.weightx = 0;
		c.gridx = 1;
		c.gridy = 1;
		pnGraph.add(bottomRight.tabbedPane, c);

		mqttComp.add(tabbedPane, BorderLayout.CENTER);

		// Add the pubsub JSplitPane to the outer frame
		mqttComp.add(westPanel, BorderLayout.WEST );

		ipAddress = new JComboBox();
		ipAddress.setPreferredSize( new Dimension(125,20) );
		ipAddress.setEditable( true );
		// Load any TCP/IP address info from the config file
		getProperties( props, ipAddress, "IPAddressList", DEFAULT_IP_ADDRESS );

		port = new JComboBox();
		port.setPreferredSize( new Dimension(65,20) );
		port.setEditable( true );
		// Load any TCP/IP port info from the config file
		getProperties( props, port, "IPPortList", DEFAULT_PORT_NUMBER );

		// Create the options panel
		optionsComp = new ConnOpts( this, props );

		// Add an LED to indicate the connection state
		led = new LED();
		led.setRed();
		new Thread(led).start();

		connect = new JButton( "Connect" );
		disconnect = new JButton( "Disconnect" );
		disconnect.setEnabled(false);
		JButton history = new JButton("History");

		connect.addActionListener(this);        
		disconnect.addActionListener(this);
		history.addActionListener(this);

		JPanel text = new JPanel();
		text.add( new JLabel("Broker TCP/IP address: ") );
		text.add( ipAddress );
		text.add( port );

		JPanel buttons = new JPanel();
		buttons.setLayout( new BoxLayout( buttons, BoxLayout.X_AXIS ) );
		buttons.add( new JSeparator( SwingConstants.VERTICAL ) );
		buttons.add( led );
		buttons.add( connect );
		buttons.add( new JLabel("  ") );
		buttons.add( disconnect );
		buttons.add( new JSeparator( SwingConstants.VERTICAL ) );
		buttons.add( history );
		buttons.add( new JSeparator( SwingConstants.VERTICAL ) );

		connPanel.add( text );
		connPanel.add( buttons );

		pubPanelContr = new PubPanel( pubPanel, this );
		subPanelContr = new SubPanel( subPanel, this );

		if ( propFile != null ) {
			try {
				propFile.close();
			} catch(Exception e) {
				// Don't worry if we can't close the properties file
			}		
		}

		// Now construct the tabbed pane
		JTabbedPane tabbedGui = new JTabbedPane();
		tabbedGui.addTab( "MQTT", mqttComp );
		tabbedGui.addTab( "Options", optionsComp );
		contentPane.add( tabbedGui );
		theFrame.setVisible(true);
	}

	/**
	 * A simple wrapper for the MQTT publish method. This method is invoked as a result of the
	 * publish button being pressed. If a problem is detected then an exception is thrown and an error
	 * message is displayed in the window title bar or in a separate dialog box.
	 * @param topic The topic on which the data will be published.
	 * @param message The data to be published
	 * @param qos The Quality of Service at which the publication should be delivered.
	 * @param retained Is this a retained publication or not?
	 * @throws Exception on error
	 */
	public void publish( String topic, byte[] message, int qos, boolean retained ) throws Exception {
		setTitleText( "" );
		if ( connected ) {
			mqtt.getTopic(topic).publish( message, qos, retained );
		} else {
			setTitleText( "MQTT client not connected !" );
			throw new Exception( "MQTT client not connected" );
		}		
	}

	/**
	 * A wrapper for the MQTT disconnect method.
	 * As well as disconnecting the protocol this method enables / disables buttons
	 * as appropriate when in disconnected state. It also sets the correct colour of the indicator LED.
	 */
	public void disconnect() {
		connected = false;

		// Notify connectionLost to give up. It may be running..
		synchronized(connLostWait) {
			connLostWait.notify();
		}

		// Disconnect from the broker
		if ( mqtt != null ) {
			try {
				mqtt.disconnect();
			} catch ( Exception ex ) {
				setTitleText( "MQTT disconnect error !" );
				ex.printStackTrace();
				System.exit(1);
			}	 
		}		
		// Set the LED state correctly
		// If the led is flashing then turn it off
		// This only occurs if disconnect is hit during connection lost
		if ( led.isFlashing() ) {
			led.setFlash();
		}	
		led.setRed();

		setConnected( false );

		synchronized(this) { // Grab the log synchronisation lock
			writeLogln("WebSphere MQ Telemetry transport disconnected" );
		}	
	}	

	/**
	 * A wrapper for the MQTT connect method. If the ip address, port number or persistence flag
	 * has changed since the last time then a new MqttClient object is required. If these values haven't changed then
	 * any previously created object can be used.
	 * Check whether Last Will and Testament is required and call the appropriate connect method.
	 * The only persistence implementation supported at the moment is MqttFilePersistence.
	 * @param connStr Connection string
	 * @param usePersistence Is persistence required?
	 * @throws MqttException on error
	 * @throws org.eclipse.paho.client.mqttv3.MqttException 
	 * @throws org.eclipse.paho.client.mqttv3.MqttException 
	 */
	public void connect( String connStr, boolean usePersistence ) throws org.eclipse.paho.client.mqttv3.MqttException, MqttException {
		// Connect to the broker
		// If we have a MqttClient object and the new ip address
		// or port number is not equal to the previous, or the persistence flag changes between
		// off and on then we need a new object.
		if ( (mqtt != null) &&
				(!connStr.equals(mqtt.getServerURI()) /*||
		      (usePersistence != (mqtt.getPersistence() != null) )*/ ) ) {
			//mqtt.terminate();
			mqtt = null;
		}	
		if ( mqtt == null ) {
			MqttClientPersistence persistence = null;
			if ( usePersistence ) {
				persistence = new MqttDefaultFilePersistence( optionsComp.getPersistenceDirectory() );
			}	
			mqtt = new MqttClient( connStr, optionsComp.getClientID(), persistence );
			mqtt.setCallback( this );
			// Carry the trace setting over to the new MqttClient object
			if ( traceEnabled ) {
				startTrace();
			}
		}	

		// Set the retry interval for the connection
		//mqtt.setRetry( optionsComp.getRetryInterval() );
		opts = new MqttConnectOptions();
		opts.setCleanSession(optionsComp.isCleanSessionSelected());
		opts.setKeepAliveInterval(optionsComp.getKeepAlive());

		if ( optionsComp.isLWTTopicSet() ) {
			opts.setWill(mqtt.getTopic(optionsComp.getLWTTopic()),
					optionsComp.getLWTData().getBytes(), 
					optionsComp.getLWTQoS(),
					optionsComp.isLWTRetainSelected());
		}
		mqtt.connect(opts);
	}
	/**
	 * This method is called when either the subscribe or unsubscribe buttons are pressed. It performs the 
	 * MQTT subscribe or unsubscribe and writes an entry in the history log if the history window is open.<BR>
	 * Any exceptions are caught and displayed in a dialog box.
	 * @param topic The topic to subscribe to
	 * @param qos The maximum Quality of Service at which to receive publications
	 * @param sub Is this a subscribe or unsubscribe operation? (true if subscribe).
	 */     			
	public void subscription( String topic, int qos, boolean sub ) {
		setTitleText( "" );

		if ( connected ) {
			try {
				String[] theseTopics = new String[1];
				int[] theseQoS = new int[1];

				theseTopics[0] = topic;
				theseQoS[0] = qos;

				synchronized(this) { // Grab the log synchronisation lock
					if ( sub ) {
						writeLogln( "  --> SUBSCRIBE,        TOPIC:" + topic + ", Requested QoS:" + qos );
					} else {
						writeLogln( "  --> UNSUBSCRIBE,      TOPIC:" + topic );
					}	  
				}

				if ( sub ) {
					mqtt.subscribe( theseTopics, theseQoS );
				} else {
					mqtt.unsubscribe( theseTopics );
				}	  

			} catch ( Exception ex ) {
				setTitleText( "MQTT subscription exception caught !" );
				JOptionPane.showMessageDialog( frame, ex.getMessage(), "MQTT Subscription Exception", JOptionPane.ERROR_MESSAGE );
			}	
		} else {
			setTitleText( "MQTT client not connected !" );
		}		
	}	

	/** Invoked by actionPerformed when connect is pressed. 
	 *  This allows actionPerformed to return and paint the window. This thread
	 *  then does the MQTT connect to the broker.<BR>
	 *  This method also ensures that the LED colour is set correctly and writes
	 *  an entry to the history dialog if it is open.
	 */
	public void run() {
		int rc = -1;

		// Connect to the broker
		String ipAddr = (String)ipAddress.getSelectedItem();
		String portNum = (String)port.getSelectedItem();
		String connStr = "";
		try {
			// If the entry in the IP Address drop down list contains '://' then assume
			// the connection has been explicitly entered as tcp://ip_address:port or local://broker_name.
			// Otherwise read the ip address and port number from their respective drop downs.
			if (!ipAddr.contains("://")) {
				connStr = "tcp://" + ipAddr + ":" + portNum;
			} else {
				connStr = ipAddr;
			}
			connect( connStr, optionsComp.isPersistenceSelected() );

			// Successful connect(no exception). Remember the ipAddress and port in the drop downs
			updateComboBoxList( ipAddress, ipAddr );
			updateComboBoxList( port, portNum );
			connected = true;
			led.setGreen();
			setConnected( true );

		} catch( NumberFormatException nfe ) {
			JOptionPane.showMessageDialog( frame, "Invalid port number !", "MQTT Connect Exception", JOptionPane.ERROR_MESSAGE );
		} catch ( MqttException mqe ) {
			setTitleText( "MQTT connect failed !" );
			Throwable e = mqe.getCause();
			String msg = "";
			if ( e == null ) {
				e = mqe;
			} else if ( mqe.getMessage() != null ) {
				msg += mqe.getMessage() + "\n";
			}
			msg += e;
			JOptionPane.showMessageDialog( frame, msg, "MQTT Connect Exception", JOptionPane.ERROR_MESSAGE );
			e.printStackTrace();
		} catch ( Exception ex ) {
			setTitleText( "MQTT connect failed !" );
			JOptionPane.showMessageDialog( frame, ex, "MQTT Connect Exception", JOptionPane.ERROR_MESSAGE );
			ex.printStackTrace();
		}

		if ( !connected ) {
			led.setRed();
			setConnected( false );
		}

		synchronized(this) { // Grab the log synchronisation lock
			if ( connected ) {
				writeLogln("WebSphere MQ Telemetry transport connected to " + mqtt.getServerURI() );
			} else {
				writeLogln("ERROR:WebSphere MQ Telemetry transport failed to connect to " + connStr );
			}	
		}    

	}

	/**
	 *  Implement the ActionListener interface and catch user interface events. Button pressed events handled are as follows:
	 * <UL><LI>Connect - Check the client is not already connected, set the LED colour to amber to indicate connect is in progress and
	 * start a thread to do the connect.
	 * <LI>Disconnect - Check the client is connected and then disconnect the MQTT protocol
	 * <LI>Otions - If the options dialog is opened then reset it's size and position to default values before making it visible
	 * </UL>
	 * @param e The action event to process.
	 */
	public void actionPerformed( ActionEvent e) {

		setTitleText( "" );

		if ( e.getActionCommand().equals("Connect") ) {
			// When the connect button is pressed we are either connected or not connected
			// If we are connected then say so.
			// If we are not connected then
			//      1. Set the LED to Amber, the state to connecting and start a thread to do the actual connect.
			//         This allows the GUI thread to return and paint the window correctly
			if ( connected ) {
				// Already connected
				setTitleText( "MQTT session already active !" );
			} else {
				// Initialise the GUI prior to connecting by setting the LED to amber.
				// Start a thread to do the connect.
				connect.setEnabled(false);
				led.setAmber();
				connected = false;
				new Thread(this).start();
			}	
		} else if ( e.getActionCommand().equals("Disconnect") ) {
			if ( connected ) {
				// Disconnect from the broker
				disconnect();
			}	else {
				setTitleText( "MQTT client not connected !" );
			}	
		} else if ( e.getActionCommand().equals("History") ) {
			historyComp.enableHistory();
		}	

	}	

	/**
	 * This method accepts a string on text and displays it in the window's title bar.
	 * @param extraText The text to be appended to some default words and displayed.
	 */
	// Synchronized as this may also be called on the connectionLost thread, which is
	// created by the Java MQTT Client
	public synchronized void setTitleText( String extraText ) {
		if ( extraText.equals("") ) {
			frame.setTitle( "Illinois MQTT" );
		} else {
			frame.setTitle( "Illinois MQTT Utility - " + extraText );
		}		
	}	

	/**
	 * The method is part of the MqttSimpleCallback interface
	 * <BR>In the event of the MQTT connection being broken the LED is set to colour amber and made to flash.
	 * The code then keeps trying to reconnect until either a successful
	 * reconnect occurs or the disconnect button is pressed. Finally the LED is stopped flashing and set to 
	 * green or red depending upon whether the connect was successful or not.
	 */
	public void connectionLost(java.lang.Throwable cause) {
		int rc = -1;

		// Flip the LED to Amber and set it flashing
		led.setAmber();
		led.setFlash();

		setTitleText( "Connection Lost!....Reconnecting" );
		synchronized(this) { // Grab the log synchronisation lock
			writeLogln( "MQTT Connection Lost!....Reconnecting to " + mqtt.getServerURI() );
		}	

		try {
			// While we have failed to reconnect and disconnect hasn't
			// been called by another thread retry to connect
			while ( (rc == -1) &&  connected ) {

				try {
					synchronized(connLostWait) {
						connLostWait.wait(10000);
					}
				} catch (InterruptedException iex) {
					// Don't care if we are interrupted
				}		

				synchronized(this) { // Grab the log synchronisation lock
					if ( connected ) {
						writeLog( "MQTT reconnecting......" );
						try {
							connect( mqtt.getServerURI(), optionsComp.isPersistenceSelected() );
							rc = 0;
						} catch (MqttException mqte) {
							// Catch any MQTT exceptions, set rc to -1 and retry
							rc = -1;
						}		
						if ( rc == -1 ) {
							writeLogln( "failed" );
						} else {
							writeLogln( "success !" );
						}		
					}
				}
			}	
			// Remove title text once we have reconnected
			setTitleText( "" );
		} catch (Exception ex) {
			setTitleText( "MQTT connection broken !" );
			ex.printStackTrace();
			disconnect();
			//throw ex;
		} finally {
			// Set the flashing off whatever happens
			if ( led.isFlashing() ) {
				led.setFlash(); // Flash off
			}	
		}	

		// If we get here and we are connected then set the led to green
		if ( connected ) {
			led.setGreen();
			setConnected( true );
		} else {
			led.setRed();
			setConnected( false );
		}	
	}	

	/**
	 * This method takes a XYSeriesGraph as a parameter and clears the data on the graph if the time passed is greater than the recording length. 
	 * @param g
	 */
	public void clearDataBasedOnRecordingLength(XYSeriesGraph g) {
		System.out.println("Inside clearDataBasedOnRecordingLength" + ( currentTime/1000 - (g.tref/1000)));
		System.out.println("g.doubleRecordingLength:" + g.doubleRecordingLength);
		if ((currentTime/1000 - (g.tref/1000)) >= g.doubleRecordingLength) {
			g.goals.clear();
			g.goals1.clear();
			g.goals2.clear();
			g.goals3.clear();

			g.tref = System.currentTimeMillis();
		}
	}
	public void clearDataAfterCertainInput(XYSeriesHighFrequency g) {
		g.goals.clear();
		g.tref = System.currentTimeMillis();
		countNumDataHighFrequency = 0;
	}

	public int IntValExtraction (String hex) {
		assert hex.length() == 8;
		int hexVal1 = Integer.parseInt(hex.substring(1, 8), 16);
		int hexVal2 = Integer.parseInt(hex.substring(0, 1), 16);
		int Val = hexVal2 << 28 | hexVal1; 
		return Val;
	}

	public double IQ24ValueExtraction(int IntNumber) {
		double wholepart = 0;
		double iq24number = 0;;
		double fractionalpart = (double)(IntNumber & 0x00ffffff) /(Math.pow(2,24));
		if (((IntNumber >> 31) & 0x1) == 0) {
			wholepart = (double)((IntNumber & 0xff000000) >> 24);
		}
		else {
			wholepart = (double)((((IntNumber & 0xff000000) >> 24) | 0xffffff00));
		}
		iq24number = wholepart + fractionalpart;

		return iq24number;
	}



	public void chooseGraphToPlotOn(XYSeriesGraph g, String t, MqttMessage m) {
		System.out.println("chooseGraphToPlotOn is being called");
		String dataString = new String(m.getPayload());

		String valueOne = dataString.substring(0, 10).split("x")[1];
		String valueTwo = dataString.substring(10, 20).split("x")[1];
		int valueOneExtractFromHex = IntValExtraction(valueOne);
		int valueTwoExtractFromHex = IntValExtraction(valueTwo);

		double scale_TopicOne_1 = valueOneExtractFromHex * Double.parseDouble(g.txt_scale_one_lineTwo.getText());
		double scale_TopicOne_2 = valueTwoExtractFromHex * Double.parseDouble(g.txt_scale_one_lineThree.getText());

		double scale_TopicTwo_1 = valueOneExtractFromHex * Double.parseDouble(g.txt_scale_two_lineTwo.getText());
		double scale_TopicTwo_2 = valueTwoExtractFromHex * Double.parseDouble(g.txt_scale_two_lineThree.getText());

		double iq24ValueOne = IQ24ValueExtraction(valueOneExtractFromHex);
		double iq24ValueTwo = IQ24ValueExtraction(valueTwoExtractFromHex);

		System.out.println("Should be false:" + g.interval.getText().isEmpty());
		if (!g.interval.getText().isEmpty()) {			
			if (((currentTime/1000 - (g.trefPrivate/1000))) >= Double.parseDouble(g.interval.getText())) {
				double t0; 				
				if (!g.txtTopicOne.getText().isEmpty() && t.equals(g.txtTopicOne.getText())) {					
					System.out.println("Inside the second condition");
					if(!g.txtTopicOne_Val1.getText().isEmpty() && !g.txtTopicOne_Val2.getText().isEmpty()) {
						subPanelContr.updateReceivedData( t, m.getPayload(), m.getQos(), m.isRetained() );
						t0 = (currentTime - g.tref)/1000;
						if (g.cbTopicOne_1.isSelected()) {
							g.goals.add(t0,scale_TopicOne_1);
						}
						if (g.cbTopicOne_2.isSelected()) {
							g.goals1.add(t0,scale_TopicOne_2);
						}
						System.out.println("Added value to the series");
						System.out.println(valueOneExtractFromHex);
						System.out.println(valueTwoExtractFromHex);
						System.out.println(t0);
						clearDataBasedOnRecordingLength(g);
						g.trefPrivate = System.currentTimeMillis();
						lowFreFileFormatArray[1] = g.txtTopicOne_Val1.getText();
						lowFreFileFormatArray[2] = String.valueOf(valueOneExtractFromHex);
						lowFreFileFormatArray[3] = g.txt_scale_one_lineTwo.getText();
						lowFreFileFormatArray[4] = String.valueOf(scale_TopicOne_1);

						lowFreFileFormatArray[5] = g.txtTopicOne_Val2.getText();
						lowFreFileFormatArray[6] = String.valueOf(valueTwoExtractFromHex);
						lowFreFileFormatArray[7] = g.txt_scale_one_lineThree.getText();
						lowFreFileFormatArray[8] = String.valueOf(scale_TopicOne_2);
					}
				}

				if (!g.txtTopicTwo.getText().isEmpty() && t.equals(g.txtTopicTwo.getText())) {
					subPanelContr.updateReceivedData( t, m.getPayload(), m.getQos(), m.isRetained() );
					t0 = currentTime - g.tref;
					if(g.cbTopicTwo_1.isSelected()) {
						g.goals2.add(t0/1000,scale_TopicTwo_1);
					}
					if(g.cbTopicTwo_2.isSelected()) {
						g.goals3.add(t0/1000,scale_TopicTwo_2);
					}
					clearDataBasedOnRecordingLength(g);
					g.trefPrivate = System.currentTimeMillis();
					lowFreFileFormatArray[9] = g.txtTopicTwo_Val1.getText();
					lowFreFileFormatArray[10] = String.valueOf(valueOneExtractFromHex);
					lowFreFileFormatArray[11] = g.txt_scale_two_lineTwo.getText();
					lowFreFileFormatArray[12] = String.valueOf(scale_TopicTwo_1);

					lowFreFileFormatArray[13] = g.txtTopicTwo_Val2.getText();
					lowFreFileFormatArray[14] = String.valueOf(valueTwoExtractFromHex);
					lowFreFileFormatArray[15] = g.txt_scale_two_lineThree.getText();
					lowFreFileFormatArray[16] = String.valueOf(scale_TopicTwo_2);
				}
				System.out.println("Here is the inside of the condition");
				BufferedWriter out = null;

				t0 = MQTTFrame.currentTime;
				try {
					if (!g.txtFilename.getText().trim().isEmpty()) {
						String filePath = "./data-files/" + g.txtFilename.getText();
				        File file = new File(filePath);
						FileWriter fstream = new FileWriter(file, true); //true = append data
						out = new BufferedWriter(fstream);
						BigDecimal d = new BigDecimal(String.valueOf(t0));
						if (g.btnStart.getModel().isPressed()) {
							out.write("Time\t1.1_" + g.txtTopicOne_Val1.getText() + 
									"\t1.1_RawVal\t1.1_Scale_Factor\t1.1_ScaledVal\t1.2" +
									g.txtTopicOne_Val2.getText() + "\t1.2_RawVal\t1.2_Scale_Factor\t1.2_ScaledVal\t2.1_" +
									g.txtTopicTwo_Val1.getText() + "\t2.1_RawVal\t2.1_Scale_Factor\t2.1_ScaledVal\t2.2_" +
									g.txtTopicTwo_Val2.getText() + "\t2.2_RawVal\t2.2_Scale_Factor\t2.2_ScaledVal\n");
						}
						lowFreFileFormatArray[0] = d.toPlainString();

						//						out.write(d.toPlainString() + "\t\t" + valueOneExtractFromHex + "\t" + g.txt_scale_one_lineTwo.getText() +
						//								"\t" + scale_TopicOne_1 + "\t\t" + valueTwoExtractFromHex + "\t" + g.txt_scale_one_lineTwo.getText() +
						//								"\t" + scale_TopicOne_2 + "\n");

						for (int i = 0; i < 17; i ++) {
							out.write(lowFreFileFormatArray[i] + ",\t");
						}
						out.write("\n");
					}

				} catch (IOException ex) {
					System.err.println("Error: " + ex.getMessage());
					ex.printStackTrace();
				} finally {
					if (out != null) {
						try {
							out.close();
						} catch (IOException ioex) {
							ioex.printStackTrace();
						}
					}
				}
			}
		}
	}

	public void plotOnHighFrequency(XYSeriesHighFrequency g, String t, MqttMessage m) {
		// System.out.println("plotOnHighFrequency() is called");
		if (!g.txtTopic.getText().isEmpty() && t.equals(g.txtTopic.getText())) {
			
			// 1. Update SubPanel received data
			subPanelContr.updateReceivedData(t, m.getPayload(), m.getQos(), m.isRetained());
			
			// 2. Parse payload consistent with Low Frequency logic
			String dataString = new String(m.getPayload());
			String valueOne = dataString.substring(0, 10).split("x")[1];
			int valueOneExtractFromHex = IntValExtraction(valueOne);

			// 3. Calculate elapsed time
			double t0 = (currentTime - g.tref) / 1000.0;

			// 4. Thread-safe UI update
			javax.swing.SwingUtilities.invokeLater(() -> {
				g.goals.add(t0, valueOneExtractFromHex);
				
				// Auto-scroll X-axis window
				if (t0 > g.doubleRecordingLength && g.doubleRecordingLength > 0) {
					((org.jfree.chart.plot.XYPlot) g.cp.getChart().getPlot())
						.getDomainAxis().setRange(t0 - g.doubleRecordingLength, t0);
				}
			});

			// 5. Corrected File Writing Logic (Inside Topic Check & Directory Matching)
			// Note: We want the file to display the data that being displayed right before the clear button is clicked.
			if (!g.txtFilename.getText().trim().isEmpty()) {
				BufferedWriter out = null;
				try {
					// Ensure directory path matches LowFreq behavior
					String filePath = "./data-files/" + g.txtFilename.getText();
					File file = new File(filePath);
					
					// Ensure parent directories exist
					if (file.getParentFile() != null) {
						file.getParentFile().mkdirs();
					}

					FileWriter fstream = new FileWriter(file, true); // true = append mode.
					out = new BufferedWriter(fstream);
					
					// Write timestamp and extracted signal value
					out.write(t0 + "\t" + valueOneExtractFromHex + "\n");
				} catch (IOException ex) {
					System.err.println("High Frequency File Write Error: " + ex.getMessage());
					ex.printStackTrace();
				} finally {
					if (out != null) {
						try {
							out.close();
						} catch (IOException ioex) {
							ioex.printStackTrace();
						}
					}
				}
			}
		}
	}

	/**
	 * The method is part of the MqttCallback interface<BR>
	 * Pass the message as is to the SubPanel object which will display it.
	 */
	public void messageArrived( String topic, MqttMessage message) {
		try {
			currentTime = System.currentTimeMillis();

			new Thread(() -> chooseGraphToPlotOn(topLeft, topic, message)).start();
			new Thread(() -> chooseGraphToPlotOn(topRight, topic, message)).start();
			new Thread(() -> chooseGraphToPlotOn(bottomLeft, topic, message)).start();
			new Thread(() -> chooseGraphToPlotOn(bottomRight, topic, message)).start();
			new Thread(() -> plotOnHighFrequency(highFrequencyGraph, topic, message)).start();

			topLeft.fileContent = message.getPayload().clone();
			topRight.fileContent = message.getPayload().clone();
			bottomLeft.fileContent = message.getPayload().clone();
			bottomRight.fileContent = message.getPayload().clone();

			//			currentTime = System.currentTimeMillis();
			//			new Thread() { 
			//		        public void run() {
			//		        	chooseGraphToPlotOn(topLeft, topic, message);
			//		        }
			//		    }.start();
			//		    
			//		    new Thread() { 
			//		        public void run() {
			//		        	chooseGraphToPlotOn(topRight, topic, message);
			//		        }
			//		    }.start();
			//		    
			//		    new Thread() {
			//		    	public void run() {
			//		    		chooseGraphToPlotOn(bottomRight, topic, message);
			//		    	}
			//		    }.start();
			//		    
			//		    new Thread() {
			//		    	public void run() {
			//		    		chooseGraphToPlotOn(bottomLeft, topic, message);
			//		    	}
			//		    }.start();
			//		    
			//		    new Thread() {
			//		    	public void run() {
			//		    		plotOnHighFrequency(highFrequencyGraph, topic, message);
			//		    	}
			//		    }.start();

		}
		catch (Exception e)
		{ 
			e.printStackTrace();		
		}
		System.out.println("Hello inside msg arrived");
	}	

	public void deliveryComplete( IMqttDeliveryToken token ) {

	}

	/**
	 * If a new topic is typed into the publish topic box then the
	 * subscribe topic box needs to be updated.
	 * @param topicName The topic name to add to the subscribe topic list
	 */
	public void updateSubscribeTopicList( String topicName ) {
		subPanelContr.updateTopicList( topicName );
	}	

	/**
	 * If a new topic is typed into the subscribe topic box then the
	 * publish topic box needs to be updated.
	 * @param topicName The topic name to add to the publish topic list
	 */
	public void updatePublishTopicList( String topicName ) {
		pubPanelContr.updateTopicList( topicName );
	}	

	/**
	 * This method handles string items in JComboBox drop downs. It checks
	 * to see if an item already exists in the list. If it doesn't, then it updates the list.
	 * @param list The JComboBox object to update
	 * @param itemName The value to add into the list if it doesn't already exist
	 * @return True if the item was added to the list
	 */
	public boolean updateComboBoxList( JComboBox list, String itemName ) {
		int listCount = list.getItemCount();
		boolean found = false;

		if ( itemName == null ) {
			return false;
		}

		for( int i=0; i<listCount; i++ ) {
			if ( ((String)list.getItemAt(i)).equals(itemName) ) {
				// This topic already exists in the list, so don't add it again
				found = true;
				break;
			}	
		}	

		if ( !found ) {
			list.addItem( itemName );
			return true;
		}	

		return false;
	}

	/**
	 * This method calls the MQTT startTrace method to produce trace of the protocol flows
	 * @throws MqttException on error
	 */    
	public void startTrace() throws MqttException {
		traceEnabled = true;
		/*
    	if ( mqtt != null ) {
    		try {
		    mqtt.startTrace();
    		} catch ( MqttException mqe ) {
    			traceEnabled = false;
    			Throwable e = mqe.getCause();
    			if ( e == null ) {
    				e = mqe;
    			}
         		JOptionPane.showMessageDialog( frame, e,
            		 "MQTT start trace exception !", JOptionPane.ERROR_MESSAGE );
    			throw mqe;
    		}		
		}*/	
	}	

	/**
	 * This method calls the MQTT stopTrace method to stop trace of the protocol flows
	 */    
	public void stopTrace() {
		traceEnabled = false;
		if ( mqtt != null ) {
			/*mqtt.stopTrace()*/
			JOptionPane.showMessageDialog( frame, "Trace file mqe0.trc generated in the current directory",
					"MQTT Trace", JOptionPane.INFORMATION_MESSAGE );
		}	
	}	

	/**
	 *  Write to the history dialog window and append a newline character after the text
	 * @param logdata The line of text to display in the history log
	 */
	public void writeLogln( String logdata) {
		writeLog( logdata + System.getProperty("line.separator") );
	}

	/**
	 *  Write to the history dialog window
	 * @param logdata The line of text to display in the history log
	 */
	public void writeLog( String logdata) {
		if ( historyComp != null ) {
			try {
				historyComp.write( logdata );
			} catch (Exception e) {
				setTitleText("Log write failed!"); 
			}		
		}	
	}

	/** Add properties from a Properties object to a JComboBox. The properties to add
	 *  are selected using the specified key. Each key returns a single string, which may be
	 *  further broken down into substrings delimited by the PROP_DELIM character.
	 *  If no properties are found then the default value is used.
	 * @param props The properties object from which to get the property value.
	 * @param comp The drop down box to which the tokens within the key must be added.
	 * @param key The key to identify the property to access.
	 * @param defValue The default value for the key if no value is found.
	 */
	public void getProperties( Properties props, JComboBox comp, String key, String defValue ) {
		String parms = props.getProperty( key, defValue );

		StringTokenizer st = new StringTokenizer( parms, PROP_DELIM );

		while ( st.hasMoreTokens() ) {
			comp.addItem( st.nextToken() );
		}	
	}	

	/**
	 *  For a given property key construct a value to associate with the key
	 *  The value can then be inserted in a properties object
	 * @param prop The property to construct a string value for.
	 * @return A string to write into the properties file.
	 */
	public String constructPropertyValue( String prop ) {
		String retString = null;

		// For IPADDRESS and IPPORT there may be mulitple values, so
		// delimit the values with the PROP_DELIM character     	
		if ( prop.equals("IPAddressList") ) {
			int numAddrs = ipAddress.getItemCount();
			if (numAddrs > 0) {
				retString = "";
			} else {
				retString = DEFAULT_IP_ADDRESS;
			}	
			for( int i=0; i < numAddrs; i++ ) {
				retString += ipAddress.getItemAt(i);
				// Don't add a delimiter after the last token
				if ( i != numAddrs - 1 ) {
					retString += PROP_DELIM;
				}
			}	
		} else if ( prop.equals("IPPortList") ) {
			int numPorts = port.getItemCount();
			if (numPorts > 0) {
				retString = "";
			} else {
				retString = DEFAULT_PORT_NUMBER;
			}	
			for( int i=0; i < numPorts; i++ ) {
				retString += port.getItemAt(i);
				// Don't add a delimiter after the last token
				if ( i != numPorts - 1 ) {
					retString += PROP_DELIM;
				}
			}	
		} else if ( prop.equals("ClientId") ) {
			retString = optionsComp.getClientID();
		} else if ( prop.equals("Persistence") ) {
			retString = String.valueOf( optionsComp.isPersistenceSelected() );
		} else if ( prop.equals("PersistenceDir") ) {
			retString = optionsComp.getPersistenceDirectory();
		}			

		return retString;
	}	

	/**
	 *  If we are not connected then disable the disconnect, publish,
	 *  subscribe, and unsubscribe buttons. Enable connect.
	 *  If we are connected then do the inverse.
	 * @param b True if connected, false otherwise.
	 */
	private void setConnected( boolean b ) {
		pubPanelContr.enableButtons( b );
		subPanelContr.enableButtons( b );
		disconnect.setEnabled( b );
		connect.setEnabled( !b );
	}	
}