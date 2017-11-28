import java.io.InputStream;


import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.FileManager;

public class Lab1_4d {

	public static void main(String[] args) {
		org.apache.log4j.Logger.getRootLogger().
		setLevel(org.apache.log4j.Level.OFF);
		
		Dataset dataset=TDBFactory.createDataset("MyDatabases/dataset1");
		dataset.begin(ReadWrite.WRITE);
		try {
			Model model=dataset.getNamedModel("myrdf");
			InputStream in = FileManager.get().open( "Srinivasa_FOAFFriends.rdf" );
			if(in==null)
			{
				throw new IllegalArgumentException("file not found");
				
			}
			model.read(in,null);
			model.write(System.out,"RDF/XML-ABBREV");
			System.out.print("\n Prinitng N-TRIPLES\n");
			model.write(System.out,"N-TRIPLES");
			System.out.print("\nPrinting Turtle\n");
			model.write(System.out,"TURTLE");
			dataset.commit();
		}
		finally {
			dataset.end();
			
		}

	}

}
