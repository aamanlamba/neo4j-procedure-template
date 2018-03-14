package com.aaman.neo4j;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.commons.codec.binary.Base64;
import org.freehep.graphics2d.VectorGraphics;
import org.freehep.graphicsio.svg.SVGGraphics2D;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import com.aaman.neo4j.MovieVertex;
import com.aaman.neo4j.NodeInfo;
import com.aaman.neo4j.PersonVertex;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class JungGraph {

	private final DirectedSparseGraph<NodeInfo,String> graph;
    private final ObjectMapper objectMapper;

	
	public JungGraph() {
	     objectMapper = new ObjectMapper();
	     objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
	   	 graph = new DirectedSparseGraph<>();
	}

	public  String generateJSONGraph(final String cql, final GraphDatabaseService db)
			throws IOException {
				  
			try (org.neo4j.graphdb.Transaction tx = db.beginTx()) {
				org.neo4j.graphdb.Result result = db.execute(cql);	
			
				 while (result.hasNext()) {
         			Map<String,Object> row = result.next();
         			String targetNode = row.get("title").toString();
         			// record.get(1).get("title").toString();
		        	  	String sourceNode = row.get("name").toString();
		        	  
		        	  	String tagline = row.get("tagline").toString();
		        	  	String released = row.get("released").toString();
		        	  	int born = (int) row.get("born");
		        	  	String rel = sourceNode + "-ACTED_IN-"+ targetNode;
		        	  
		        	  	MovieVertex mv = new MovieVertex(targetNode,tagline,released,"Movie");
		        	  	PersonVertex pv = new PersonVertex(sourceNode,born,"Person");
		        	  	graph.addVertex(pv);
		        	  
		        	  	graph.addVertex(mv);
		        	  	graph.addEdge(rel, pv, mv);
		        	  
				 } 
		    		}		
				String JSONgraph = renderJSONGraph(graph);
				return JSONgraph;

	}


	private String renderJSONGraph(DirectedSparseGraph<NodeInfo, String> g) throws IOException {
	
		String JSONGraph="";
		ISOMLayout<NodeInfo,String> layout = new ISOMLayout<NodeInfo,String>(g);
		Dimension viewerDim = new Dimension(800,800);
		Rectangle viewerRect = new Rectangle(viewerDim);
	    VisualizationViewer<NodeInfo,String> vv =
	      new VisualizationViewer<NodeInfo,String>(layout, viewerDim);
	    GraphElementAccessor<NodeInfo, String> pickSupport = 
	            vv.getPickSupport();
	        Collection<NodeInfo> vertices = 
	            pickSupport.getVertices(layout, viewerRect);
	        
	        //print vertices collection as JSON array
	        String verticesJSON = objectMapper.writeValueAsString(vertices);
	        JSONGraph += verticesJSON;
	        return JSONGraph;
	}
	
	public  String generateSVGGraph(final String cql, final GraphDatabaseService db)
			throws IOException {
				  
			try (org.neo4j.graphdb.Transaction tx = db.beginTx()) {
				org.neo4j.graphdb.Result result = db.execute(cql);	
			
				 while (result.hasNext()) {
         			Map<String,Object> row = result.next();
         			String targetNode = row.get("title").toString();
         			// record.get(1).get("title").toString();
		        	  	String sourceNode = row.get("name").toString();
		        	  
		        	  	String tagline = row.get("tagline").toString();
		        	  	String released = row.get("released").toString();
		        	  	int born = (int) row.get("born");
		        	  	String rel = sourceNode + "-ACTED_IN-"+ targetNode;
		        	  
		        	  	MovieVertex mv = new MovieVertex(targetNode,tagline,released,"Movie");
		        	  	PersonVertex pv = new PersonVertex(sourceNode,born,"Person");
		        	  	graph.addVertex(pv);
		        	  
		        	  	graph.addVertex(mv);
		        	  	graph.addEdge(rel, pv, mv);
		        	  
				 } 
		    		}		
				String SVGgraph = renderSVGGraph(graph);
				return SVGgraph;

	}
	private String renderSVGGraph(DirectedSparseGraph<NodeInfo, String> g) throws IOException {
		String SVGGraph="";
		ISOMLayout<NodeInfo,String> layout = new ISOMLayout<NodeInfo,String>(g);
		Dimension viewerDim = new Dimension(800,800);
		Rectangle viewerRect = new Rectangle(viewerDim);
	    VisualizationViewer<NodeInfo,String> vv =
	      new VisualizationViewer<NodeInfo,String>(layout, viewerDim);
	    GraphElementAccessor<NodeInfo, String> pickSupport = 
	            vv.getPickSupport();
	        Collection<NodeInfo> vertices = 
	            pickSupport.getVertices(layout, viewerRect);
	        
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
    

    // Get a DOMImplementation.
    DOMImplementation domImpl =
      GenericDOMImplementation.getDOMImplementation();

    // Create an instance of org.w3c.dom.Document.
    String svgNS = "http://www.w3.org/2000/svg";
    Document document = domImpl.createDocument(svgNS, "svg", null);

    // Create an instance of the SVG Generator.
    org.apache.batik.svggen.SVGGraphics2D svgGenerator = new org.apache.batik.svggen.SVGGraphics2D(document);

    svgGenerator.setSVGCanvasSize(viewerDim);
    vv.print(svgGenerator);

    boolean useCSS = true; // we want to use CSS style attributes

    StringWriter sw = new StringWriter();
    svgGenerator.stream(sw, useCSS);
    String result = sw.toString();
    sw.close();
 
    byte[] bytes = new byte[(int)result.length()];
    String encodedSVGString = new String(Base64.encodeBase64(bytes), "UTF-8");
    
   	return encodedSVGString;
  }

}
