package Lab2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFWriter;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.DC;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.VCARD;

public class Lab2_3_SAyachitula {

	public static void main (String args[]) {
		org.apache.log4j.Logger.getRootLogger().
		setLevel(org.apache.log4j.Level.OFF);
		
		String nameSpace = "http://utdallas/semclass/";
		String pNamespace = nameSpace + "person#";
		String mNameSapace = nameSpace + "movie#";
		String dNameSapace = nameSpace + "director#";
		String aNameSapace = nameSpace + "author#";
		String bNameSapace = nameSpace + "book#";
		
			String d1URI = pNamespace + "/StanleyKubrick";
				String d1name = "Stanley Kubrick";
		
										String m1URI = mNameSapace + "/AClockWorkOrange";
											String m1Name = "A Clock Work Orange";
													String m1Year = "1971";
		
		String m2URI = mNameSapace + "/DrStrangelove";
			String m2Name = "Dr Stranlove";
				String m2Year = "1964";
		
							String b1URI = bNameSapace + "/RedAlert";
		String b1Name = "Red Alert";
					String b1Year = "1958";
							String b1Author = "Peter George";
		
		String b2URI = bNameSapace + "/AClockworkOrange";
					String b2Name = "A Clockwork Orange";
					String b2Year = "1962";
		String b2Author = "Anthony Burgess";
		
		String a1URI = aNameSapace + "/AnthonyBurgess";
						String a1Name = "Anthony Burgess";
		
		String a2URI = aNameSapace + "/PeterGeorge";
					String a2Name = "Peter George";
		
		Model model = ModelFactory.createDefaultModel();
		
		Resource person = model.createResource(pNamespace);
		person.addProperty(RDF.type, person);
		
		Resource movie = model.createResource(mNameSapace);
		movie.addProperty(RDF.type, movie);
		
		Resource director = model.createResource(dNameSapace);
		director.addProperty(RDF.type, director);
		
		Resource author = model.createResource(aNameSapace);
		author.addProperty(RDF.type, author);
		
		Resource book = model.createResource(bNameSapace);
		book.addProperty(RDF.type, book);
		
		Property movieName = model.createProperty(mNameSapace, "Name");
	    Property movieYear = model.createProperty(mNameSapace, "Year"); 
	    Property movieDirectedBy = model.createProperty(dNameSapace, "directedBy");
		Property movieBasedOn = model.createProperty(bNameSapace, "basedOn");
		
		Resource sKubrick = model.createResource(pNamespace)
				.addProperty(RDF.type, person)
				.addProperty(VCARD.FN, d1name);
		
		Resource movie1 = model.createResource(m1URI)
				.addProperty(RDF.type, movie) 
				.addProperty(movieName, m1Name) 
				.addProperty(movieYear, m1Year);
		
		Resource movie2 = model.createResource(m2URI)
				.addProperty(RDF.type, movie) 
				.addProperty(movieName, m2Name) 
				.addProperty(movieYear, m2Year);
		
		Resource aBurgess = model.createResource(a1URI)
				.addProperty(RDF.type, author)
				.addProperty(VCARD.FN, a1Name);
		
		Resource pGeorge = model.createResource(a2URI)
				.addProperty(RDF.type, author)
				.addProperty(VCARD.FN, a2Name);
		
		Resource Book1 = model.createResource(b1URI)
				.addProperty(DC.type, book)
				.addProperty(DC.creator, aBurgess)
				.addProperty(DC.title, b1Name)
				.addProperty(DC.date, b1Year);
		
		Resource Book2 = model.createResource(b2URI)
				.addProperty(DC.type, book)
				.addProperty(DC.creator, pGeorge)
				.addProperty(DC.title, b2Name)
				.addProperty(DC.date, b2Year);
		
		String directory = "MyDatabases/Dataset1";
		Dataset dataset = TDBFactory.createDataset(directory);
		
		dataset.begin(ReadWrite.WRITE);
		Model TDBModel = dataset.getNamedModel("myrdf");
		TDBModel.add(model);
		
		RDFWriter ntrip = TDBModel.getWriter("N3");
		RDFWriter xmlwriter = TDBModel.getWriter("RDF/XML");
		
		
		
		try {
			ntrip.write(TDBModel,new FileOutputStream(new File("Lab2_3_SAyachitula.n3")),"n3");
		    xmlwriter.write(TDBModel,new FileOutputStream(new File("Lab2_3_SAyachityla.rdf")),"xml");
		} 
			catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//dataset.commit();
		//dataset.end();
		
	}

}
