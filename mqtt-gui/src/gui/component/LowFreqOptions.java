package gui.component;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import gui.model.SubPanel;

public class LowFreqOptions {

	public ChartPanel cp;

	public JPanel options = new JPanel();
	public JPanel optionsLeft = new JPanel();
	public JPanel optionsRight = new JPanel();

	private XYSeriesGraph xySeriesGraph = null;

	public void subscribeOnClick(XYSeriesGraph g) {
		System.out.println("Low Freq's onclick subscribe is being called");
		g.tref = System.currentTimeMillis();
		//g.trefPrivate = System.currentTimeMillis();
		//Topic 1:
		if (!g.txtTopicOne.getText().trim().isEmpty()) {
			xySeriesGraph.getMQTTframe().subscription(g.txtTopicOne.getText(), 1 , true );	
			System.out.println("Topic One of is called");
			if (SubPanel.strTopicList.contains(g.txtTopicOne.getText())) {
			} else if ( !SubPanel.strTopicList.contains(g.txtTopicOne.getText())) {
				SubPanel.subTopicArea.append(g.txtTopicOne.getText() + "\n");
				SubPanel.strTopicList.add(g.txtTopicOne.getText());
			}
		}
		//Topic 2:
		if (!g.txtTopicTwo.getText().trim().isEmpty()) {
			xySeriesGraph.getMQTTframe().subscription( g.txtTopicTwo.getText(), 1 , true );
			System.out.println("Topic Three of is called");
			if (SubPanel.strTopicList.contains(g.txtTopicTwo.getText())) {
			} else if ( !SubPanel.strTopicList.contains(g.txtTopicTwo.getText())) {
				SubPanel.subTopicArea.append(g.txtTopicTwo.getText() + "\n");
				SubPanel.strTopicList.add(g.txtTopicTwo.getText());
			}
		}
	}

	public LowFreqOptions(XYSeriesGraph g) {
		xySeriesGraph = g;
		options.setLayout(new FlowLayout());
		optionsLeft.setLayout(new GridLayout(6,1));
		optionsRight.setLayout(new GridLayout(8,1));

		//OPTION LEFT
		g.lowerValueLimit.setLayout(new BoxLayout(g.lowerValueLimit, BoxLayout.X_AXIS));
		JLabel lblLowerValueLimit = new JLabel("Lower value limit: ");
		g.lowerValueLimit.add(lblLowerValueLimit);
		g.lowerValueLimit.add(g.txtLowerValueLimit);
		g.setLowerValueLimit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//rangeAxis.setRange(Double.parseDouble(txtLowerValueLimit.getText()), Double.parseDouble(txtUpperValueLimit.getText()) );
				xySeriesGraph.range.setLowerBound(Double.parseDouble(g.txtLowerValueLimit.getText()));
			}
		});
		g.lowerValueLimit.add(g.setLowerValueLimit);
		optionsLeft.add(g.lowerValueLimit);


		g.upperValueLimit.setLayout(new BoxLayout(g.upperValueLimit, BoxLayout.X_AXIS));
		JLabel lblUpperValueLimit = new JLabel("Upper value limit: ");
		g.upperValueLimit.add(lblUpperValueLimit);
		g.upperValueLimit.add(g.txtUpperValueLimit);
		g.setUpperValueLimit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				xySeriesGraph.range.setUpperBound(Double.parseDouble(g.txtUpperValueLimit.getText()));
			}
		});
		g.upperValueLimit.add(g.setUpperValueLimit);
		optionsLeft.add(g.upperValueLimit);


		g.recordingLengthInSecs.setLayout(new BoxLayout(g.recordingLengthInSecs, BoxLayout.X_AXIS));
		JLabel lblRecordingLength = new JLabel("Recording Length (s): ");
		g.recordingLengthInSecs.add(lblRecordingLength);
		g.recordingLengthInSecs.add(g.txtRecordingLength);
		g.recordingLengthInSecs.add(g.setRecordingLength);

		g.setRecordingLength.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				g.doubleRecordingLength = Double.parseDouble(g.txtRecordingLength.getText());
				xySeriesGraph.domain.setRange(0, g.doubleRecordingLength);
			}
		});

		optionsLeft.add(g.recordingLengthInSecs);

		g.filename.setLayout(new BoxLayout(g.filename, BoxLayout.X_AXIS));
		JLabel lblFilename = new JLabel("File Name: ");
		g.filename.add(lblFilename);
		g.filename.add(g.txtFilename);
		g.filename.add(g.setFilename);
		g.setFilename.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {	
				BufferedWriter out = null;
				try {
					if (!g.txtFilename.getText().trim().isEmpty()) {
						String filePath = "./data-files/" + g.txtFilename.getText();
				        File file = new File(filePath);
						FileWriter fstream = new FileWriter(file, true); //true = append data
						out = new BufferedWriter(fstream);

						out.write("Time,\t1.1_" + g.txtTopicOne_Val1.getText() + "," + 
								"\t1.1_RawVal,\t1.1_Scale_Factor,\t1.1_ScaledVal,\t1.2" +
								g.txtTopicOne_Val2.getText()  + "," + "\t1.2_RawVal,\t1.2_Scale_Factor,\t1.2_ScaledVal,\t2.1_" +
								g.txtTopicTwo_Val1.getText()  + "," + "\t2.1_RawVal,\t2.1_Scale_Factor,\t2.1_ScaledVal,\t2.2_" +
								g.txtTopicTwo_Val2.getText()  + "," + "\t2.2_RawVal,\t2.2_Scale_Factor,\t2.2_ScaledVal,\n");
					}
				} catch (IOException ex) {
					// TODO: handle exception
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
		});

		optionsLeft.add(g.filename);

		g.startClearButtons.setLayout(new BoxLayout(g.startClearButtons, BoxLayout.X_AXIS));
		g.startClearButtons.add(g.btnStart);
		g.startClearButtons.add(g.btnClear);
		optionsLeft.add(g.startClearButtons);

		lblLowerValueLimit.setPreferredSize(lblRecordingLength.getPreferredSize());
		lblUpperValueLimit.setPreferredSize(lblRecordingLength.getPreferredSize());
		lblFilename.setPreferredSize(lblRecordingLength.getPreferredSize());

		//OPTIONS RIGHT
		//TOPIC ONE:
		//Topic 1 line 1
		g.topicOne.setLayout(new BoxLayout(g.topicOne, BoxLayout.X_AXIS));
		JLabel topicOneLabel =  new JLabel("Topic: "); 
		g.topicOne.add(topicOneLabel);
		g.topicOne.add(g.txtTopicOne);
		g.topicOne.add(g.unsubTopicOne);
		g.unsubTopicOne.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				xySeriesGraph.getMQTTframe().subscription( g.txtTopicOne.getText(), 0, false );
				SubPanel.strTopicList.remove(g.txtTopicOne.getText());
				SubPanel.subTopicArea.setText("");
				for( String s : SubPanel.strTopicList) {
					SubPanel.subTopicArea.append(s + "\n");
				}
			}
		});

		optionsRight.add(g.topicOne);
		//Topic 1's line 2
		g.topicOneLineTwo.setLayout(new BoxLayout(g.topicOneLineTwo, BoxLayout.X_AXIS));
		JLabel lbltopicOneLineTwo = new JLabel("");
		g.topicOneLineTwo.add(lbltopicOneLineTwo);
		g.topicOneLineTwo.add(g.cbTopicOne_1);
		g.topicOneLineTwo.add(g.txtTopicOne_Val1);
		//g.topicOneLineTwo.add(g.cb_scale_one_lineTwo);
		g.topicOneLineTwo.add(g.lbl_scale_topicOne_1);
		g.topicOneLineTwo.add(g.txt_scale_one_lineTwo);

		//Topic 1 line 3
		g.topicOneLineThree.setLayout(new BoxLayout(g.topicOneLineThree, BoxLayout.X_AXIS));
		JLabel lbltopicOneLineThree = new JLabel("");
		g.topicOneLineThree.add(lbltopicOneLineThree);
		g.topicOneLineThree.add(g.cbTopicOne_2);
		g.topicOneLineThree.add(g.txtTopicOne_Val2);
		//g.topicOneLineThree.add(g.cb_scale_one_lineThree);
		g.topicOneLineThree.add(g.lbl_scale_topicOne_2);
		g.topicOneLineThree.add(g.txt_scale_one_lineThree);		

		optionsRight.add(g.topicOneLineTwo);
		optionsRight.add(g.topicOneLineThree);

		//TOPIC TWO
		//Topic 2 line 1
		g.topicTwo.setLayout(new BoxLayout(g.topicTwo, BoxLayout.X_AXIS));
		JLabel topicTwoLabel = new JLabel("Topic: ");
		g.topicTwo.add(topicTwoLabel);
		g.topicTwo.add(g.txtTopicTwo);
		g.topicTwo.add(g.unsubTopicThree);
		g.unsubTopicThree.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				xySeriesGraph.getMQTTframe().subscription( g.txtTopicTwo.getText(), 0, false );
				SubPanel.strTopicList.remove(g.txtTopicTwo.getText());
				SubPanel.subTopicArea.setText("");
				for( String s : SubPanel.strTopicList) {
					SubPanel.subTopicArea.append(s + "\n");
				}
			}
		});

		optionsRight.add(g.topicTwo);
		//Topic 2's displays:
		//Topic 2 line 2
		g.topicTwoLineTwo.setLayout(new BoxLayout(g.topicTwoLineTwo, BoxLayout.X_AXIS));
		JLabel lbltopicTwoLineTwo = new JLabel("");
		g.topicTwoLineTwo.add(lbltopicTwoLineTwo);
		g.topicTwoLineTwo.add(g.cbTopicTwo_1);
		g.topicTwoLineTwo.add(g.txtTopicTwo_Val1);
		//g.topicTwoLineTwo.add(g.cb_scale_two_lineTwo);
		g.topicTwoLineTwo.add(g.lbl_scale_topicTwo_1);
		g.topicTwoLineTwo.add(g.txt_scale_two_lineTwo);
		//Topic 1 line 3
		g.topicTwoLineThree.setLayout(new BoxLayout(g.topicTwoLineThree, BoxLayout.X_AXIS));
		JLabel lbltopicTwoLineThree = new JLabel("");
		g.topicTwoLineThree.add(lbltopicTwoLineThree);
		g.topicTwoLineThree.add(g.cbTopicTwo_2);
		g.topicTwoLineThree.add(g.txtTopicTwo_Val2);
		//g.topicTwoLineThree.add(g.cb_scale_two_lineThree);
		g.topicTwoLineThree.add(g.lbl_scale_topicTwo_2);
		g.topicTwoLineThree.add(g.txt_scale_two_lineThree);		

		optionsRight.add(g.topicTwoLineTwo);
		optionsRight.add(g.topicTwoLineThree);

		g.pnInterval.setLayout(new BoxLayout(g.pnInterval, BoxLayout.X_AXIS));
		JLabel lblInterval = new JLabel("Tick: ");
		g.pnInterval.add(lblInterval);
		g.pnInterval.add(g.interval);

		optionsRight.add(g.pnInterval);

		lblInterval.setPreferredSize(topicOneLabel.getPreferredSize());
		//		topicTwoLabel.setPreferredSize(topicOneLabel.getPreferredSize());
		//topicOneLineTwo.setPreferredSize(topicOneLabel.getPreferredSize());
		g.topicTwoLineTwo.setPreferredSize(topicOneLabel.getPreferredSize());

		g.btnStart.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				subscribeOnClick(g);
				g.xyDataset.removeSeries(g.goals);
				g.goals.setKey("1_" + g.txtTopicOne_Val1.getText());
				xySeriesGraph.xyDataset.addSeries(g.goals);

				xySeriesGraph.xyDataset.removeSeries(g.goals1);
				g.goals1.setKey("2_" + g.txtTopicOne_Val2.getText());
				xySeriesGraph.xyDataset.addSeries(g.goals1);

				g.xyDataset.removeSeries(g.goals2);
				g.goals2.setKey("3_" + g.txtTopicTwo_Val1.getText());
				xySeriesGraph.xyDataset.addSeries(g.goals2);

				xySeriesGraph.xyDataset.removeSeries(g.goals3);
				g.goals3.setKey("4_" + g.txtTopicTwo_Val2.getText());
				xySeriesGraph.xyDataset.addSeries(g.goals3);


				System.out.println("Button Start (low Freq) is clicked");
			}
		});

		g.btnClear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				g.goals.clear();
				g.goals1.clear();
				g.goals2.clear();
				g.goals3.clear();

				g.tref = System.currentTimeMillis();
			}
		});

		//JTabbedPane tabbedPane = new JTabbedPane();

		options.add(optionsLeft);
		options.add(optionsRight);
	}

}
