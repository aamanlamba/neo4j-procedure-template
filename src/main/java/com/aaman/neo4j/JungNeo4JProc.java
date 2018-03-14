package com.aaman.neo4j;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.logging.Log;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Mode;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.Procedure;
import org.neo4j.procedure.UserFunction;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import javax.swing.JFrame;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.renderers.Renderer.Vertex;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;

import org.apache.commons.collections4.Transformer;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.ogm.model.Result;

import com.aaman.neo4j.NodeInfo;
import com.aaman.neo4j.FullTextIndex.SearchHit;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import static org.neo4j.driver.v1.Values.parameters;
@SuppressWarnings("unused")
public class JungNeo4JProc {
	   // This field declares that we need a GraphDatabaseService
    // as context when any procedure in this class is invoked
    @Context
    public GraphDatabaseService db;

    // This gives us a log instance that outputs messages to the
    // standard log, normally found under `data/log/console.log`
    @Context
    public Log log;
    
    @UserFunction
    @Description("com.aaman.neo4j.getJungSVG(query) - return JUNG-rendered SVG of query results")
    public String getJungSVG(
            @Name("string") String query) throws IOException {
    		  String SVGResultStr = "";
        if (query == null) {
            return null;
        }
        JungGraph graph = new JungGraph();
        SVGResultStr = graph.generateSVGGraph(query, db);
        return SVGResultStr;
    }



    @UserFunction
    @Description("com.aaman.neo4j.getJungJSON(query) - return JUNG-rendered JSON of query results - nodes, coordinates, edges")
    public String getJungJSON(
            @Name(value="query", defaultValue = "") String query) throws IOException {
		  String JSONResultStr = "";
	        if (query == null) {
	            return null;
	        }
	        JungGraph graph = new JungGraph();
	        JSONResultStr = graph.generateJSONGraph(query, db);
	        return JSONResultStr;
    }
}
