

import java.util.LinkedHashSet;
import java.util.Set;
import java.io.*;

import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;

import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.vocabulary.DC;
import org.apache.jena.vocabulary.OWL;


public class Lab6  {
		
	    public static void main (String args[]) {
	    	
	    	org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);
	         
	    	
	        Model model = ModelFactory.createDefaultModel();
	        
	       
	        Set<String> conference1 = new LinkedHashSet<String>();
			Set<String> conference2 = new LinkedHashSet<String>();
		 			
			model.read("eswc-2008-complete_modified.rdf");
			model.read("eswc-2009-complete_modified.rdf");
        
			
			String MatchedQuery = "SELECT  ?conference1 ?Name ?Papertitle ?conference2 WHERE {"
					+ "?conference1 <http://xmlns.com/foaf/0.1/mbox_sha1sum> ?mbox . "
					+ "?conference2 <http://xmlns.com/foaf/0.1/mbox_sha1sum> ?mbox ."
					+ "?conference1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Person> . "
					+ "?conference1 <http://xmlns.com/foaf/0.1/name> ?Name . "
					+ "?conference1 <http://xmlns.com/foaf/0.1/made> ?Paper . "
					+ "?Paper <http://purl.org/dc/elements/1.1/title> ?Papertitle ."
					+ "FILTER (?conference1 != ?conference2). "+ " }";

			Query query = QueryFactory.create(MatchedQuery);
			QueryExecution ExecuteQuery = QueryExecutionFactory.create(query, model);
			ResultSet finalResults = (ResultSet) ExecuteQuery.execSelect();

			while (finalResults.hasNext()) {
				QuerySolution res = finalResults.nextSolution();
				
				RDFNode personURI2008 = res.get("conference1");
				RDFNode personURI2009 = res.get("conference2");

				RDFNode name = res.get("Name");
				RDFNode paperTitle = res.get("Papertitle");

				System.out.println(personURI2008.toString() + "  ,  " + name.toString() + "  ,  " + paperTitle.toString());
				if (personURI2008.toString().contains("person2008"))
					conference1.add(personURI2008.toString());
				else
					conference2.add(personURI2008.toString());

				if (personURI2009.toString().contains("person2008"))
					conference1.add(personURI2009.toString());
				else
					conference2.add(personURI2009.toString());

			}

			System.out.println("End of First Listing");
		
			Property psamePerson = model.createProperty("http://utdallas.semclass/samePerson");

			for (int i = 0; i < conference1.size(); i++)
			{
               
			
				Resource AssertandReify = model.getResource(conference1.toArray()[i].toString());
				AssertandReify.addProperty(OWL.sameAs, model.getResource(conference2.toArray()[i].toString()))
						.addProperty(DC.creator, "SAyachitula");
				
		
				Resource resPropertyAssert = model.createResource(conference1.toArray()[i].toString());
				resPropertyAssert.addProperty(psamePerson, model.getResource(conference2.toArray()[i].toString()))
						.addProperty(DC.creator, "SAyachitula");

			}
           
			for (int i = 0; i < conference1.size(); i++) {
				
				String PeopleURI_2008 = conference1.toArray()[i].toString();

				MatchedQuery = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
						+ "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
						+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
						+ "PREFIX utd: <http://utdallas.semclass/> "
						+ "SELECT DISTINCT ?person_name ?paperTitle WHERE {" + "<" + PeopleURI_2008
						+ "> utd:samePerson ?Person_2009." + "<" + PeopleURI_2008 + "> foaf:name ?person_name . "
						+ " {<" + PeopleURI_2008 + "> foaf:made ?paper_URI .}"
						+ " UNION {?Person_2009 foaf:made ?paper_URI . }." + " ?paper_URI dc:title ?paperTitle }";

				query = QueryFactory.create(MatchedQuery);
				QueryExecution ExecuteQuery2 = QueryExecutionFactory.create(query, model);
				ResultSet results = ExecuteQuery2.execSelect();
				ResultSetFormatter.out(System.out, results);
			}

			System.out.println("End of Second Listing");
			
			Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();

			InfModel model1 = ModelFactory.createInfModel(reasoner, model);

			for (int i = 0; i < conference1.size(); i++) 
			{
			  String PeopleURI_2008 = conference1.toArray()[i].toString();
              MatchedQuery = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
            		     +"PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
            		     + "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
			             + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
						 + "SELECT DISTINCT ?name ?paperTitle WHERE {" + "<" + PeopleURI_2008
						 + "> owl:sameAs ?Person_2009." + "<" + PeopleURI_2008 + "> foaf:name ?name . " + " {<"
						 + PeopleURI_2008 + "> foaf:made ?paper_URI .}"
						 + " UNION {?Person_2009 foaf:made ?paper_URI . }." + " ?paper_URI dc:title ?paperTitle }";

				query = QueryFactory.create(MatchedQuery);
				QueryExecution ExecuteQuery3 = QueryExecutionFactory.create(query, model1);
				ResultSet results = ExecuteQuery3.execSelect();
				ResultSetFormatter.out(System.out, results);
			}

			System.out.println("End of Third Listing");
			
			PrintWriter write = null;
			try
												{
																write = new PrintWriter ("Lab6_SAyachitula.n3");
			      						model.write(write,"N3");
		    
		    
	        }
	        catch (FileNotFoundException e)
	        {
	         e.printStackTrace();
	         }
			System.out.println("N3 printed to Lab6_SAyachitula.n3");
			
    }
}
