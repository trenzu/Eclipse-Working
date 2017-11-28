import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

public class Lab3_2 {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);
		Model model=ModelFactory.createDefaultModel();
		long time=System.currentTimeMillis();
		//System.out.println("Current time is "+time);
		System.out.println("Reading the file");
		model.read("Monterey.rdf");
		long aTime=System.currentTimeMillis();
		System.out.println("Time for loading the file Montery.rdf took "+(aTime-time)/1000+ " seconds");
		//model.write(System.out,"RDF/XML-ABBREV");		
		System.out.println("Running the query..");
		String queryString= "SELECT ?x ?y " + 
				"WHERE { <http://urn.monterey.org/incidents#incident1> ?x ?y }";
		Query myQuery=QueryFactory.create(queryString);
		QueryExecution qe = QueryExecutionFactory.create(myQuery, model);
		ResultSet results = qe.execSelect();
	
		FileOutputStream fr = new FileOutputStream ("Lab3_2_SAyachitula.xml");
		ResultSetFormatter.outputAsXML(fr, results);
		
		qe.close();
		System.out.println("Done! File Lab3_2_SAyachitula.xml created in the root directory of the project");
	}

}
