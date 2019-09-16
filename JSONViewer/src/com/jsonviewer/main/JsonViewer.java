package com.jsonviewer.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.json.XML;

public class JsonViewer extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String rootElement = "jsonRootElement";

	public JsonViewer() {
        initializeUI();
    }

    private void initializeUI() {
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(600, 300));

        final JTextArea textArea = new JTextArea();

        // Set the contents of the JTextArea.
        //String text = "The quick brown fox jumps over the lazy dog.";
        //textArea.setText(text);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane pane = new JScrollPane(textArea);
        pane.setPreferredSize(new Dimension(600, 300));
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JButton pasteButton = new JButton("Paste");
        pasteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	 Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
            	 Transferable t = c.getContents(this);
            	 if (t == null)
            	     return;
            	 try {
            		 textArea.setText((String) t.getTransferData(DataFlavor.stringFlavor));
            	 } catch (Exception ex){
            		 
            	 }
            }
        });
        
        JButton copyButton = new JButton("Copy");
        copyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	StringSelection stringSelection = new StringSelection(textArea.getText());
            	Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            	clipboard.setContents(stringSelection, null);
            }
        });
        
        JButton formatButton = new JButton("Format");
        formatButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Get the contents of the JTextArea component.
            	jsonBeautify(textArea);
            }
        });
        
        JButton whiteSpaceButton = new JButton("Remove space");
        whiteSpaceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	jsonUnBeautify(textArea);
            }
        });
        
        JButton jsonButton = new JButton("JSON");
        jsonButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	xmlToJson(textArea);
            }
        });
        
        JButton xmlButton = new JButton("XML");
        xmlButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	jsonToXml(textArea); 
            }
        });

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	textArea.setText("");
            }
        });
        
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(pasteButton);
        buttonsPanel.add(copyButton);
        buttonsPanel.add(formatButton);
        buttonsPanel.add(whiteSpaceButton);
        buttonsPanel.add(jsonButton);
        buttonsPanel.add(xmlButton);
        buttonsPanel.add(clearButton);        
        add(buttonsPanel, BorderLayout.SOUTH);
        
        
        this.add(pane, BorderLayout.CENTER);
        //this.add(formatButton, BorderLayout.EAST);
    }

    public static void showFrame() {
        JPanel panel = new JsonViewer();
        panel.setOpaque(true);

        JFrame frame = new JFrame("Go-Getters JSON Viewer");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private static void jsonBeautify(JTextArea textArea) {
		String contents = textArea.getText();
		if(contents != null && !contents.isEmpty()) {
			ObjectMapper mapper = new ObjectMapper();
	        try {
	            Object jsonObject = mapper.readValue(contents, Object.class);
	            textArea.setText(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject));
	        } catch (IOException e) {
	        	JOptionPane.showMessageDialog(textArea, e.getMessage());
	        }
		}
	}
    
    private static void jsonUnBeautify(JTextArea textArea) {
		String contents = textArea.getText();
		if(contents != null && !contents.isEmpty()) {
			textArea.setText(contents.replaceAll("\\s+",""));
		}
	}
    
    private static void jsonToXml(JTextArea textArea) {
		String contents = textArea.getText();
		if(contents != null && !contents.isEmpty()) {
			try {
				JSONObject json = new JSONObject(contents);
				
				String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-15\"?>\n<"+rootElement+">" 
	                     + XML.toString(json) + "</"+rootElement+">";
	        
	        		
				//textArea.setText(XML.toString(json));
				//FORMAT
				//textArea.setText(new XmlFormatter().format(xml));
				String withRoot = new XmlFormatter().format(xml);
				withRoot = withRoot.replace("<"+rootElement+">", "");
				withRoot = withRoot.replace("</"+rootElement+">", "");
				textArea.setText(withRoot);
				
				
				/*Source xmlInput = new StreamSource(new StringReader(XML.toString(json)));
	            StringWriter stringWriter = new StringWriter();
	            StreamResult xmlOutput = new StreamResult(stringWriter);
	            TransformerFactory transformerFactory = TransformerFactory.newInstance();
	            transformerFactory.setAttribute("indent-number", 2);
	            Transformer transformer = transformerFactory.newTransformer(); 
	            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	            transformer.transform(xmlInput, xmlOutput);
	            textArea.setText(xmlOutput.getWriter().toString());*/
			} catch (Exception e) {
				JOptionPane.showMessageDialog(textArea, e.getMessage());
			}
		}
	}
    
    private static void xmlToJson(JTextArea textArea) {
		String contents = textArea.getText();
		if(contents != null && !contents.isEmpty()) {
			try {
				JSONObject xmlJSONObj = XML.toJSONObject(contents);
				//FORMAT
				ObjectMapper mapper = new ObjectMapper();
				Object jsonObject = mapper.readValue(xmlJSONObj.toString(), Object.class);
		        textArea.setText(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject));
			} catch (Exception e) {
				JOptionPane.showMessageDialog(textArea, e.getMessage());
			}
			
		}
	}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JsonViewer.showFrame();
            }
        });
    }
}
