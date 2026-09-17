package gui.component;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import gui.model.MQTTFrame;
import gui.model.SubPanel;


public class XYSeriesHighFrequency {
	public JPanel graphPanel;
	public XYSeries goals;
	public ChartPanel cp;
	
	JPanel options = new JPanel();
	JPanel optionsLeft = new JPanel();
	JPanel optionsRight = new JPanel();
	
	JPanel lowerValueLimit = new JPanel();
	JPanel upperValueLimit = new JPanel();
	JPanel dataLengthNumData = new JPanel();
	JPanel recordingLength = new JPanel();
	JPanel topic = new JPanel();
	JPanel filename = new JPanel();
	JPanel startClearButtons = new JPanel();
	
	JTextField txtLowerValueLimit = new JTextField(7);
	JTextField txtUpperValueLimit = new JTextField(7);
	JTextField txtDataPointLength = new JTextField(7);
	JTextField txtRecordingLength = new JTextField(7);
	public JTextField txtTopic = new JTextField(7);
	public JTextField txtFilename = new JTextField(7);	
	
	JButton setLowerValueLimit = new JButton("Set");
	JButton setUpperValueLimit = new JButton("Set");
	JButton setDataLengthNumData = new JButton("Set");
	JButton setRecordingLength = new JButton("Set");
	
	Icon icon = new ImageIcon("image/unsubscribe.png");
	JButton unsubscribeTopicButton = new JButton(icon);
	JButton setFilename = new JButton("Set");
	
	JButton btnStart = new JButton("Start");
	JButton btnClear = new JButton("Clear");
	
	public double doubleUpperTime;
	public double doubleLowerTime;
	public long tref;
	public long trefPrivate;
	public int numLength;
	public double doubleRecordingLength;
	
	private MQTTFrame mqttMgr = null;
	
	public void subscribeOnClick() {
		System.out.println("On click of High Freq");
		tref = System.currentTimeMillis();
		trefPrivate = System.currentTimeMillis();

		if (txtTopic.getText() != null) {
			mqttMgr.subscription( txtTopic.getText(), 1 , true );	
		}
		
		if (SubPanel.strTopicList.contains(txtTopic.getText())) {
		} else if ( !SubPanel.strTopicList.contains(txtTopic.getText())) {
			SubPanel.subTopicArea.append(txtTopic.getText() + "\n");
			SubPanel.strTopicList.add(txtTopic.getText());
		}	
	}
	public XYSeriesHighFrequency(String title, String xAxisName, String yAxisName, MQTTFrame aMqttMgr) {
		System.out.println("An instance is created from this XYSeriesHighFrequency constructor");
		mqttMgr = aMqttMgr;
		goals = new XYSeries(title);
		System.out.println("goals = new XYSeries() - high frequency");
		
		XYSeriesCollection xyDataset = new XYSeriesCollection(goals);
		JFreeChart chart = ChartFactory.createXYLineChart(
				title, xAxisName, yAxisName,
				xyDataset, PlotOrientation.VERTICAL, true, true, false);
		
		XYPlot plot = chart.getXYPlot();
		NumberAxis range = (NumberAxis)plot.getRangeAxis();
		NumberAxis domain = (NumberAxis)plot.getDomainAxis();
		domain.setRange(0, 100);
		
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
		renderer.setSeriesStroke( 0 , new BasicStroke( 3.0f ) );
		renderer.setSeriesShapesVisible(0, false);
		plot.setRenderer(renderer);
		
		
		cp = new ChartPanel(chart) {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(320, 240);
			}
		};
		
		
		
		graphPanel = new JPanel();
		graphPanel.setLayout(new BorderLayout());
		options.setLayout(new FlowLayout());
		optionsLeft.setLayout(new GridLayout(5,1));
		//optionsRight.setLayout(new GridLayout(4,1));
		
		
		lowerValueLimit.setLayout(new BoxLayout(lowerValueLimit, BoxLayout.X_AXIS));
		JLabel lblLowerValueLimit = new JLabel("Lower value imit: ");
		lowerValueLimit.add(lblLowerValueLimit);
		lowerValueLimit.add(txtLowerValueLimit);
		lowerValueLimit.add(setLowerValueLimit);
		
		setLowerValueLimit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				range.setLowerBound(Double.parseDouble(txtLowerValueLimit.getText()));
			}
		});
		
		optionsLeft.add(lowerValueLimit);
		
		upperValueLimit.setLayout(new BoxLayout(upperValueLimit, BoxLayout.X_AXIS));
		JLabel lblUpperValueLimit = new JLabel("Upper value limit: ");
		upperValueLimit.add(lblUpperValueLimit);
		upperValueLimit.add(txtUpperValueLimit);
		upperValueLimit.add(setUpperValueLimit);
		
		setUpperValueLimit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				range.setUpperBound(Double.parseDouble(txtUpperValueLimit.getText()));
				
			}
		});
		optionsLeft.add(upperValueLimit);
		
		//OptionsRight
		dataLengthNumData.setLayout(new BoxLayout(dataLengthNumData, BoxLayout.X_AXIS));
		JLabel lblDataLength = new JLabel("Data count: ");
		topic.add(lblDataLength);
		topic.add(txtDataPointLength);
		setDataLengthNumData.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				numLength = Integer.parseInt(txtDataPointLength.getText());
			}
		});
		topic.add(setDataLengthNumData);
		optionsLeft.add(dataLengthNumData);
		
		
		recordingLength.setLayout(new BoxLayout(recordingLength, BoxLayout.X_AXIS));
		JLabel lblRecordingLength = new JLabel("Recording Length: ");
		recordingLength.add(lblRecordingLength);
		recordingLength.add(txtRecordingLength);
		setRecordingLength.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				doubleRecordingLength = Double.parseDouble(txtRecordingLength.getText());
				domain.setRange(0, doubleRecordingLength);
			}
		});
		recordingLength.add(setRecordingLength);
		optionsLeft.add(recordingLength);
		
		
		topic.setLayout(new BoxLayout(topic, BoxLayout.X_AXIS));
		JLabel lblTopic = new JLabel("  Topic: ");
		topic.add(lblTopic);
		topic.add(txtTopic);
		topic.add(unsubscribeTopicButton);
		
		lblLowerValueLimit.setPreferredSize(lblRecordingLength.getPreferredSize());
		lblTopic.setPreferredSize(lblRecordingLength.getPreferredSize());
		lblDataLength.setPreferredSize(lblRecordingLength.getPreferredSize());
		lblUpperValueLimit.setPreferredSize(lblRecordingLength.getPreferredSize());
		
		unsubscribeTopicButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				mqttMgr.subscription( txtTopic.getText(), 0, false );
				SubPanel.strTopicList.remove(txtTopic.getText());
				SubPanel.subTopicArea.setText("");
				for( String s : SubPanel.strTopicList) {
					SubPanel.subTopicArea.append(s + "\n");
				}
			}
		});
		optionsLeft.add(topic);
		
		
		filename.setLayout(new BoxLayout(filename, BoxLayout.X_AXIS));
		JLabel lblFilename = new JLabel("File Name: ");
		filename.add(lblFilename);
		filename.add(txtFilename);
		setFilename.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		filename.add(setFilename);
		
		optionsRight.add(filename);
		
		startClearButtons.setLayout(new BoxLayout(startClearButtons, BoxLayout.X_AXIS));
		startClearButtons.add(btnStart);
		startClearButtons.add(btnClear);
		
		optionsRight.add(startClearButtons);

		lblFilename.setPreferredSize(lblDataLength.getPreferredSize());
		lblTopic.setPreferredSize(lblDataLength.getPreferredSize());
		
		
		btnStart.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				subscribeOnClick();
				System.out.println("Button Start (High Freq is clicked)");
			}
		});

		//when clear button in clicked, take the current count, then read the file, delete the file before it 
		btnClear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				goals.clear();
				
				tref = System.currentTimeMillis();
			}
		});
		
		options.add(optionsLeft);
		options.add(optionsRight);
		graphPanel.add(cp, BorderLayout.CENTER);
		graphPanel.add(options, BorderLayout.SOUTH);
		cp.setMouseWheelEnabled(true);
	}	
}