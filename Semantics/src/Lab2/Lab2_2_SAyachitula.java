package Lab2;

import java.io.FileWriter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.VCARD;

public class Lab2_2_SAyachitula {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		org.apache.log4j.Logger.getRootLogger().
		setLevel(org.apache.log4j.Level.OFF);
		Model model= ModelFactory.createDefaultModel();
		Property movie=model.createProperty("http://utdallas/semclass/mName","MNAME");
		Property year=model.createProperty("http://utdallas/semclass/mYear","MYEAR");
		Resource movie1=model.createResource("http://utdallas/semclass/mname/Dr.Strangelove").addProperty(year,"1964").addProperty(VCARD.NAME, " Stanley Kubrick");
		Resource movie2=model.createResource("http://utdallas/semclass/mname/AClockworkOrange").addProperty(year,"1971").addProperty(VCARD.NAME, " Stanley Kubrick");
		Resource film= model.createResource("http://utdallas/semclass/FILMS").addProperty(movie,movie1).addProperty(movie,movie2);
		try {
            FileWriter fr = new FileWriter ("C:\\Users\\tr3nzu\\eclipse-workspace\\Semantics\\films.rdf");
            model.write(fr, "RDF/XML-ABBREV");
            fr.close();
           }
      catch (Exception e) {
           e.printStackTrace();
          }     
	}

}
