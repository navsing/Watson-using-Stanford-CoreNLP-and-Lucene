package edu.arizona.cs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.*;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;

public class App {
    
    /**
     * @param args
     * @throws IOException
     * @throws ParseException
     */
    
    public static void main(String[] args) throws IOException, ParseException {
        // TODO Auto-generated method stub


	int flag = Integer.parseInt(args[0]);
	int scoring = Integer.parseInt(args[1]);


        StandardTokenizer stdToken = new StandardTokenizer();
        TokenStream tokenStream;
        StandardAnalyzer analyzer = new StandardAnalyzer();
        Directory index = new RAMDirectory();
        
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        if(scoring == 0){
            System.out.println("Score with Classic Similarity");
            config.setSimilarity(new ClassicSimilarity());
        }
        IndexWriter w = new IndexWriter(index, config);
        
        
        StanfordCoreNLP pipeline = null;
        if (flag == 0) {
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
            pipeline = new StanfordCoreNLP(props);
        }
        
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("src/main/resources/consolidate"));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }

        String line = "";
        String docName = "";
        int i = 0;
        String l = "";
        String docInfo = "";
        {
            try {
                while ((line = reader.readLine()) != null) {
                    if (line.length() > 2) {
                        if ((line.charAt(0) == '[' && line.charAt(1) == '[')) {
                            if (line.length() > 10 && (line.substring(2, 6).equalsIgnoreCase("File")
                                                       || line.substring(2, 7).equalsIgnoreCase("Image"))) {
                                continue;
                            }
                            if (i != 0) {
                                addDoc(w, docInfo, docName);
                            }
                            docName = "";
                            docName = line.substring(2, line.indexOf(']'));
                            docInfo = "";
                        } else {
                            if (flag == 1 || flag == 2) {
                                stdToken.setReader(new StringReader(line));
                                
                                tokenStream = new StopFilter(
                                                             new ASCIIFoldingFilter(new ClassicFilter(new LowerCaseFilter(stdToken))),
                                                             EnglishAnalyzer.getDefaultStopSet());
                                if(flag ==1 ){
					tokenStream = new PorterStemFilter(tokenStream);
				}
                                tokenStream.reset();
                                CharTermAttribute token = tokenStream.getAttribute(CharTermAttribute.class);
                                while (tokenStream.incrementToken()) {
                                    l = l + " " + token;
                                }
                                tokenStream.close();
                            } else {
                                Annotation document = new Annotation(line);
                                pipeline.annotate(document);
                                List<CoreMap> sentences = document.get(SentencesAnnotation.class);
                                for (CoreMap sentence : sentences) {
                                    for (CoreLabel tokenCL : sentence.get(TokensAnnotation.class)) {
                                        String word = tokenCL.get(TextAnnotation.class);
                                        l = l + " " + word;
                                    }
                                }
                            }
                            docInfo += l;
                            l = "";
                        }
                    }
                    i++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        w.close();

	/*protected static TokenStreamComponents createComponents(String fieldName, Reader reader) throws ParseException{
	      System.out.println("1");
	    Tokenizer source = new ClassicTokenizer();

	    source.setReader(reader);
	    TokenStream filter = new StandardFilter( source);

	    filter = new LowerCaseFilter(filter);
	    SynonymMap mySynonymMap = null;

	    try {

		mySynonymMap = buildSynonym();

	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    filter = new SynonymFilter(filter, mySynonymMap, false);     

	    return new TokenStreamComponents(source, filter);

	}

	private static SynonymMap buildSynonym() throws IOException, ParseException
	{    System.out.print("build");
	    File file = new File("wn\\wn_s.pl");

	    InputStream stream = new FileInputStream(file);

	    Reader rulesReader = new InputStreamReader(stream); 
	    SynonymMap.Builder parser = null;
	    parser = new WordnetSynonymParser(true, true, new StandardAnalyzer(CharArraySet.EMPTY_SET));
	    System.out.print(parser.toString());
	   ((WordnetSynonymParser) parser).parse(rulesReader);  
	    SynonymMap synonymMap = parser.build();
	    return synonymMap;
	}*/
        
        BufferedReader readerQuery = null;
        try {
            readerQuery = new BufferedReader(new FileReader("src/main/resources/questions.txt"));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        
        StandardTokenizer stdToken1 = new StandardTokenizer();
        
        TokenStream tokenStream1;
        /*TokenStreamComponents TSC = createComponents( "" , new StringReader("some text goes here")); 
	TokenStream stream = TSC.getTokenStream();
	CharTermAttribute termattr = stream.addAttribute(CharTermAttribute.class);
	stream.reset();
	while (stream.incrementToken()) {
	    System.out.println(termattr.toString());
	}
	*/
	String l1 = "";
        int r = 0;
        String str = "";
        String queryLine = "";
        {
            try {
                while ((queryLine = readerQuery.readLine()) != null) {
                    if (r % 4 == 1) {
                        if (flag == 1 || flag == 2) {
                            stdToken1.setReader(new StringReader(queryLine));
                            tokenStream1 = new StopFilter(
                                                          new ASCIIFoldingFilter(new ClassicFilter(new LowerCaseFilter(stdToken1))),
                                                          EnglishAnalyzer.getDefaultStopSet());
                           if(flag == 1 ){
				 tokenStream1 = new PorterStemFilter(tokenStream1);
				}                            
			    tokenStream1.reset();
                            CharTermAttribute token = tokenStream1.getAttribute(CharTermAttribute.class);
                            while (tokenStream1.incrementToken()) {
                                l1 = l1 + " " + token;
                            }
                            tokenStream1.close();
                        } else {
                            Annotation document = new Annotation(queryLine);
                            
                            pipeline.annotate(document);
                            List<CoreMap> sentences = document.get(SentencesAnnotation.class);
                            for (CoreMap sentence : sentences) {
                                for (CoreLabel tokenCL : sentence.get(TokensAnnotation.class)) {
                                    String word = tokenCL.get(TextAnnotation.class);
                                    l1 = l1 + " " + word;
                                }
                                
                            }
                        }
                        queryLine = l1;
                        l1 = "";
                        
                        Query q = new QueryParser("docInfo", analyzer).parse(QueryParser.escape(queryLine));
                        
                        int hitsPerPage = 100000000;
                        IndexReader reader1 = DirectoryReader.open(index);
                        IndexSearcher searcher = new IndexSearcher(reader1);
                        TopDocs docs = searcher.search(q, hitsPerPage);
                        ScoreDoc[] hits = docs.scoreDocs;
                        
                        System.out.println("Found " + hits.length + " hits.");
                        if (hits.length > 0) {
                            for (int k = 0; k < 10; ++k) {
                                int docId = hits[k].doc;
                                Document d = searcher.doc(docId);
                                System.out
                                .println((k + 1) + ". " + d.get("docName") + "\t" + " score= " + hits[k].score);
                            }
                        } else {
                            System.out.println("Sorry we could find no relevant document.");
                        }
                        System.out.println();
                        reader1.close();
                    }
                    r++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }
    
    private static void addDoc(IndexWriter w, String docInfo, String docName) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("docInfo", docInfo, Field.Store.YES));
        
        // use a string field for isbn because we don't want it tokenized
        doc.add(new StringField("docName", docName, Field.Store.YES));
        w.addDocument(doc);
    }
    
}
