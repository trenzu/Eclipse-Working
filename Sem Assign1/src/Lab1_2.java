
import java.io.IOException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.VCARD;
public class Lab1_2 {

	
	
public static void main(String [] ar) throws IOException
{
	org.apache.log4j.Logger.getRootLogger().
	setLevel(org.apache.log4j.Level.OFF);
	
	   
	Model model= ModelFactory.createDefaultModel();
	
	Resource person=model.createResource("http://utdallas/semclass/SirTimothy").addProperty(VCARD.NAME,"Sir Timothy John Berners-Lee").addProperty(VCARD.TITLE,"Computer Scientist").addProperty(VCARD.BDAY,"June 8, 1955").addProperty(VCARD.EMAIL,"timbl@w3.org");
	
	model.write(System.out,"RDF/XML-ABBREV");
	System.out.print("\n\n");
	model.write(System.out,"N-TRIPLES");
	System.out.print("\n\n");
	model.write(System.out,"TURTLE");
	
	
}
}
