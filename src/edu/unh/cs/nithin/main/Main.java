/**
 * @Author: Nithin
 * @Date:   2019-03-17T17:15:55-04:00
 * @Last modified by:   Nithin
 * @Last modified time: 2019-09-01T16:22:02-04:00
 */
package edu.unh.cs.nithin.main;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import edu.unh.cs.nithin.arrfTools.TrainSet;
import edu.unh.cs.nithin.classifier.RandomForestClassifier;
import edu.unh.cs.nithin.re_rank.ClassifierReRank;
import edu.unh.cs.nithin.retrieval_model.BM25;
import edu.unh.cs.nithin.tools.Indexer;
import edu.unh.cs.nithin.tools.QrelsGenerator;
import edu.unh.cs.treccar_v2.Data.Page;

public class Main {

	public static void main(String[] args) throws Exception {
		System.setProperty("file.encoding", "UTF-8");
		String choice = args[0];
		switch(choice) {
			case "retrieval":
				retrieval(args[1], args[2], args[3]);
				break;
			case "wikikreator":
				wikikreator(args[1], args[2]);
				break;
			case "build-classifer-model":
				buildClassifierModel(args[1]);
				break;
			case "classify-runfile":
				classifyRunFile(args[1], args[2], args[3]);
				break;
			case "Index":
				index();
				break;
			default:
				System.out.println("mode is not given ");
				break;
		}
	}

	/**
	 * [retrieval- execute BM25 retrieval model and generate a run file
	 *  for given query corpus and paragraph index corpus.
	 *  All the String paramters are file paths]
	 * @param pagesFile  [queries]
	 * @param indexPath  [paragraph index]
	 * @param outputPath [run file]
	 * @throws Exception 
	 */
	private static void retrieval(String pagesFile, String indexPath, String outputPath) throws Exception {
		System.out.println(" Starting retrieval");
		String[] categoryNames = new String[] {"Category:Environmental terminology"};
		BM25 bm25 = new BM25(pagesFile, indexPath, outputPath);
		
		String[] queryStrings = new String[] { "Ecological thinning", "Ecophysiological repercussions of thinning ", "Ecological thinning research" };
		String outFile = outputPath + "/runFiles/" + "ecology";
		bm25.querySearch(queryStrings, outFile);
		classifyRunFile(outFile, indexPath, outputPath);
	
		
//		for(String catName : categoryNames) {
//			bm25.SectionSearch(catName);
//			System.out.println(" Retrieval over");
//			String runFile = outputPath + "/runFiles/" + catName.replaceAll("[^A-Za-z0-9]", "_");
//			classifyRunFile(runFile, indexPath, outputPath);
//		}
	}

	/**
	 * [buildClassifierModel Train a weka format classifier model
	 * given training set and model output path]
	 * @param arffFile  [weka format training set]
	 * @param modelPath [output path .modelfile]
	 * @throws Exception
	 */
	private static void buildClassifierModel(String outputPath) throws Exception {
		System.out.println(" Building Random Forest Classifier Model");
		RandomForestClassifier rfc = new RandomForestClassifier(outputPath);
		rfc.buildRandomForestModel();
	}
	
	/***
	 * predicts the paraheading for each line in given runfile
	 * @param runFile filepath
	 * @param indexPath filepath
	 * @param outputPath filepath
	 * @throws Exception
	 */
	private static void classifyRunFile(String runFile, String indexPath, String outputPath) throws Exception {
		runFile = "/Users/Nithin/Desktop/outputFilesIR/runFiles/ecologypercent20.txt";
		ClassifierReRank crr = new ClassifierReRank(runFile, indexPath, outputPath);
		crr.classifyRunFile(runFile);
		crr.classifyRunFile(runFile, "Category_Environmental_terminology");
		
	}

	/**
	 * [index index all the paragraphs in para-corpus]
	 * @throws IOException
	 */
	private static void index() throws IOException {
		Indexer indexer = new Indexer();
	}

	/**
	 * @param trainingCorpus
	 * @param outputPath
	 * @throws IOException 
	 */
	private static void wikikreator(String trainingCorpus, String outputPath) throws IOException {
//		"Category:Articles containing video clips", "Category:RTT", "Category:Deserts", "Category:Environmental terminology"
		String[] categoryNames = new String[] {"Category:Articles containing video clips", "Category:Environmental terminology"};
		QrelsGenerator qg = new QrelsGenerator(trainingCorpus, outputPath, categoryNames);
		Map<String, List<Page>> categoryPages = qg.getCategoriesPages();
		qg.generateQrels(categoryPages); 
		TrainSet ts = new TrainSet(categoryPages, outputPath);
		ts.createCategoryTrainSet();
	}
}
